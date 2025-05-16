package com.tgb.gsvnbackend.service.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.tgb.gsvnbackend.exc.DataViolationException;
import com.tgb.gsvnbackend.exc.NotFoundException;
import com.tgb.gsvnbackend.lib.Oath2UtilsConverter;
import com.tgb.gsvnbackend.model.entity.Order;
import com.tgb.gsvnbackend.model.entity.QOrder;
import com.tgb.gsvnbackend.model.enumeration.PaymentMethod;
import com.tgb.gsvnbackend.model.enumeration.PaymentStatus;
import com.tgb.gsvnbackend.model.enumeration.State;
import com.tgb.gsvnbackend.queue.producer.OrderProducer;
import com.tgb.gsvnbackend.repository.mongoRepository.OrderRepository;
import com.tgb.gsvnbackend.service.CachingService;
import com.tgb.gsvnbackend.service.OrderService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class OrderServiceImp implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderProducer orderProducer;
    private final CachingService cachingService;
    private final String CACHE_ORDER_LIFETIME_PREFIX="orderTime:";
    private final String CACHE_ORDER_URL_PREFIX="orderUrlVnpay:";
    private final QOrder order=QOrder.order;
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    public OrderServiceImp(OrderRepository orderRepository, OrderProducer orderProducer, CachingService cachingService) {
        this.orderRepository = orderRepository;
        this.orderProducer = orderProducer;
        this.cachingService = cachingService;
        log.info("OrderServiceImp initialized.");
    }
    public void create(Order order)
    {
        log.info("Creating a new order with cart ID: {}", order.getCartId());
        Order savedOrder= orderRepository.save(order);
        orderProducer.sendItemReverse(order);
        log.info("Order created with ID: {}, sending item reversal message.", savedOrder.getId());
    }
    public void orderReversedSuccess(String orderId, List<Order.LineItem> lineItemList)
    {
        log.info("Order reversal successful for order ID: {}", orderId);
        orderRepository.findById(orderId).ifPresent(order -> {
            String cartId=order.getCartId();
            order.setLineItems(lineItemList);
            BigDecimal parallelSubTotal = lineItemList.parallelStream()
                    .map(item -> item.getPricing().getSale().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            order.setState((Objects.equals(
                    order.getPayment().getMethod(), PaymentMethod.COD.name()))?
                    State.Processing:State.Pending );
            Order savedOrder= orderRepository.save(order);
            log.info("Order {} updated with line items, state set to {}, total amount: {}", orderId, savedOrder.getState(), parallelSubTotal);
            // set redis order expired time payment
            if(Objects.equals(savedOrder.getPayment().getMethod(), PaymentMethod.VNPAY.name())) {
                cachingService.putWithExpiration(CACHE_ORDER_LIFETIME_PREFIX, orderId, 24, TimeUnit.HOURS);
                log.info("Set Redis expiration for order ID {} to 24 hours.", orderId);
            }
            orderProducer.sentPaymentInit(savedOrder);
            log.info("Sending payment initialization message for order ID: {}", orderId);
        });
    }
    public void orderReversedFail(String orderId)
    {
        log.warn("Order reversal failed for order ID: {}", orderId);
        orderRepository.findById(orderId).ifPresent(order -> {
            String cartId=order.getCartId();
            order.setState(State.Reject);
            orderRepository.save(order);
            log.info("Order {} state set to Reject.", orderId);
            orderProducer.sentCartFailResult(order);
            log.info("Sending cart failure result message for order ID: {}", orderId);
        });
    }

    public void orderPayment(String orderId,String paymentId,String urlPayment)
    {
        log.info("Processing order payment for order ID: {}, payment ID: {}, payment URL: {}", orderId, paymentId, urlPayment);
        orderRepository.findById(orderId).ifPresent(order -> {
            order.setPayment(new Order.Payment(
                    order.getPayment().getMethod(),
                    paymentId,
                    PaymentStatus.Pending.name()));
            orderRepository.save(order);
            log.info("Order {} payment details updated to payment ID: {}, status: Pending.", orderId, paymentId);
            orderProducer.sentCartSuccessResult(order,urlPayment);
            log.info("Sending cart success result message for order ID: {}, payment URL: {}", orderId, urlPayment);
        });
    }
    public void orderPaid(String orderId, String paymentId, boolean success) {
        log.info("Processing order paid notification for order ID: {}, payment ID: {}, success: {}", orderId, paymentId, success);
        orderRepository.findById(orderId).ifPresent(order -> {
            if (order.getPayment() != null && order.getPayment().getPaymentId().equals(paymentId)) {
                String paymentStatus = success ? PaymentStatus.Success.name() : PaymentStatus.Failed.name();
                order.setPayment(new Order.Payment(
                        order.getPayment().getMethod(),
                        paymentId,
                        paymentStatus));
                log.info("Order {} payment status updated to {}.", orderId, paymentStatus);
                if (success) {
                    order.setState(order.getState() == State.Delivered ? State.Success : State.Processing);
                    cachingService.deleteById(CACHE_ORDER_LIFETIME_PREFIX, orderId);
                    log.info("Order {} state updated to {}, removed expiration from Redis.", orderId, order.getState());
                }
                orderRepository.save(order);
                log.info("Order {} updated in database.", orderId);
            } else {
                log.warn("Payment ID {} does not match for order ID {}.", paymentId, orderId);
            }
        });
    }
    public Page<Order> getAllOrders(int page, int size, String sortBy, String sortDirection, String userId, String keyword, State state) {
        log.info("Getting all orders - Page: {}, Size: {}, Sort by: {}, Direction: {}, User ID: {}, Keyword: {}, State: {}", page, size, sortBy, sortDirection, userId, keyword, state);
        BooleanExpression predicate = null;

        if (userId != null && !userId.isEmpty()) {
            predicate = order.userId.eq(userId);
            log.debug("Filtering orders by user ID: {}", userId);
        }

        if (keyword != null && !keyword.isEmpty()) {
            BooleanExpression keywordPredicate = order.id.containsIgnoreCase(keyword)
                    .or(order.cartId.containsIgnoreCase(keyword));
            predicate = (predicate == null) ? keywordPredicate : predicate.or(keywordPredicate);
            log.debug("Filtering orders by keyword: {}", keyword);
        }

        if (state != null) {
            BooleanExpression statePredicate = order.state.eq(state);
            predicate = (predicate == null) ? statePredicate : predicate.and(statePredicate);
            log.debug("Filtering orders by state: {}", state);
        }

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        JPAQuery<Order> query = new JPAQuery<Order>(entityManager)
                .from(order)
                .where(predicate);

        long total = query.fetch().size();
        log.debug("Total orders found: {}", total);

        List<Order> results = query
                .orderBy(getOrderSpecifier(sortBy, sortDirection, order))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        log.debug("Retrieved {} orders for page {}", results.size(), page);

        return new PageImpl<>(results, pageable, total);
    }
    private OrderSpecifier<?> getOrderSpecifier(String sortBy, String sortDirection, QOrder order) {
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        log.debug("Creating order specifier - Sort by: {}, Direction: {}", sortBy, sortDirection);
        return switch (sortBy) {
            case "cartId" -> direction.isAscending() ? order.cartId.asc() : order.cartId.desc();
            case "userId" -> direction.isAscending() ? order.userId.asc() : order.userId.desc();
            case "state" -> direction.isAscending() ? order.state.asc() : order.state.desc();
            case "orderDate" -> direction.isAscending() ? order.createAt.asc() : order.createAt.desc();
            default -> direction.isAscending() ? order.id.asc() : order.id.desc();
        };
    }
    public Order getOrderById(String orderId) {
        log.info("Getting order by ID: {}", orderId);
        return orderRepository.findById(orderId).orElseThrow(()-> {
            log.error("Order not found with ID: {}", orderId);
            return new NotFoundException("Order not found");
        });
    }
    public Order changeState(String orderId,State state) {
        log.info("Changing state of order ID {} to {}", orderId, state);
        Order order = getOrderById(orderId);
        order.setState(state);
        Order savedOrder = orderRepository.save(order);
        log.info("Order {} state updated to {}", orderId, savedOrder.getState());
        return savedOrder;
    }
    public String createOrderPaymentAgain(String orderId, Principal user)
    {
        String userId= Oath2UtilsConverter.getUserId(user);
        log.info("Attempting to create payment again for order ID: {} by user ID: {}", orderId, userId);
        Order order = getOrderById(orderId);
        if (Objects.equals(order.getState(), State.Cancel) || Objects.equals(order.getState(), State.Reject)) {
            log.warn("Cannot create payment for canceled/rejected order ID: {}", orderId);
            throw new DataViolationException("Order was canceled");
        }
        if(!Objects.equals(order.getUserId(), userId))
        {
            log.warn("User ID {} does not own order ID {}", userId, orderId);
            throw new DataViolationException("Order not found");
        }
        if(Objects.equals(order.getPayment().getStatus(), PaymentStatus.Success.name()))
        {
            log.warn("Order ID {} was already paid.", orderId);
            throw new DataViolationException("Order was paid");
        }
        if(Objects.equals(order.getPayment().getMethod(),PaymentMethod.COD.name()))
        {
            log.warn("Order ID {} has COD as payment method.", orderId);
            throw new DataViolationException("Order method are COD");
        }
        orderProducer.sentPaymentInit(order);
        log.info("Payment initialization message sent again for order ID: {}", orderId);
        return order.getCartId(); // return cartId to polling again
    }
}