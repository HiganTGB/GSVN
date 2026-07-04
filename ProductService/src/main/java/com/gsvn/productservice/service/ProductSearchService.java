package com.gsvn.productservice.service;

import com.gsvn.productservice.client.MediaClient;
import com.gsvn.productservice.common.PageCursorResponse;
import com.gsvn.productservice.converter.ProductConverter;
import com.gsvn.productservice.exc.AppException;
import com.gsvn.productservice.exc.ErrorCode;
import com.gsvn.productservice.mapper.ProductMapper;
import com.gsvn.productservice.mapper.SkuMapper;
import com.gsvn.productservice.model.dto.ProductSkuMapDTO;
import com.gsvn.productservice.model.dto.ProductCardDTO;
import com.gsvn.productservice.model.dto.ProductDetailDTO;
import com.gsvn.productservice.model.entity.Product;
import com.gsvn.productservice.model.entity.SaleStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductSearchService {
    private final ProductMapper productMapper;
    private final SkuMapper skuMapper;
    private final SkuService skuService;
    private final VariantService variantService;
    private final ProductConverter productConverter;
    private final MediaClient mediaClient;

    public ProductDetailDTO getDetail(Integer productId) {
        Product product = productMapper.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.ITEM_NOT_EXISTED));

        var skuResponse = skuService.getSkusByProduct(productId);
        var variantResponse = variantService.getVariantByProduct(productId);
        return productConverter.toDetailDTO(product, skuResponse, variantResponse);
    }

    public PageCursorResponse<ProductCardDTO> search(
            String keyword,
            List<Integer> brandIds,
            List<Integer> categoryIds,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            List<SaleStatus> saleStatuses,
            String cursor,
            String sortBy,
            String direction,
            Integer limit) {

        OffsetDateTime lastCreatedAt = null;
        Integer lastId = null;
        String sortField = switch (sortBy != null ? sortBy.toLowerCase() : "") {
            case "name" -> "name";
            case "price" -> "price";
            default -> "createdAt";
        };

        String sortOrder = "asc".equalsIgnoreCase(direction) ? "ASC" : "DESC";

        if (cursor != null && !cursor.isEmpty()) {
            try {
                String decoded = new String(Base64.getDecoder().decode(cursor));
                String[] parts = decoded.split("\\|");
                if (parts.length == 2) {
                    lastCreatedAt = OffsetDateTime.parse(parts[0]);
                    lastId = Integer.parseInt(parts[1]);
                }
            } catch (Exception e) {
                log.error("Invalid cursor format: {}", cursor);
                throw new AppException(ErrorCode.INVALID_CURSOR);
            }
        }

        int queryLimit = (limit != null && limit > 0) ? limit : 20;


        List<ProductCardDTO> results = productMapper.searchProducts(
                keyword,
                brandIds,
                categoryIds,
                minPrice,
                maxPrice,
                saleStatuses,
                lastCreatedAt,
                sortField,
                sortOrder,
                lastId,
                queryLimit + 1
        );

        if (results.isEmpty()) {
            return PageCursorResponse.of(Collections.emptyList(), queryLimit, null);
        }
        log.error(results.toString());
        enrichProductData(results);

        return PageCursorResponse.of(
                results,
                queryLimit,
                item -> encodeCursor(item.getCreatedAt(), item.getId())
        );
    }

    private void enrichProductData(List<ProductCardDTO> products) {
        if (products == null || products.isEmpty()) return;

        List<Integer> productIds = products.stream()
                .map(ProductCardDTO::getId)
                .toList();

        List<ProductSkuMapDTO> skuMappings = skuMapper.findAllSkuIdsByProductIds(productIds);

        Map<Integer, List<Long>> skuMap = skuMappings.stream()
                .collect(Collectors.groupingBy(
                        ProductSkuMapDTO::getProductId,
                        Collectors.mapping(ProductSkuMapDTO::getSkuId, Collectors.toList())
                ));
        products.forEach(p ->
                p.setSkuIds(skuMap.getOrDefault(p.getId(), Collections.emptyList()))
        );
    }

    private String encodeCursor(OffsetDateTime createdAt, Integer id) {
        if (createdAt == null || id == null) return null;
        String rawCursor = createdAt.toString() + "|" + id;
        return Base64.getEncoder().encodeToString(rawCursor.getBytes());
    }
}