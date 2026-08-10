package com.gsvn.searchservice.service;

import com.gsvn.searchservice.client.InventoryClient;
import com.gsvn.searchservice.client.MediaClient;
import com.gsvn.searchservice.client.ProductClient;
import com.gsvn.searchservice.common.ApiResponse;
import com.gsvn.searchservice.common.PageCursorResponse;
import com.gsvn.searchservice.converter.ProductConverter;
import com.gsvn.searchservice.model.internal.SkuResponse;
import com.gsvn.searchservice.model.internal.SkuSearchResponse;
import com.gsvn.searchservice.model.internal.SkuSellableDTO;
import com.gsvn.searchservice.model.internal.SkuStockResponse;
import com.gsvn.searchservice.model.response.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

    private final ProductClient productClient;
    private final InventoryClient inventoryClient;
    private final MediaClient mediaClient;
    private final ProductConverter productConverter;


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



        var productResponse = productClient.search(
                keyword, brandIds, categoryIds,
                minPrice, maxPrice, saleStatuses,
                cursor,sortBy, direction, limit
        );

        PageCursorResponse<ProductCardDTO> pageData = productResponse.result();

        if (pageData == null || pageData.getContent().isEmpty()) {
            return pageData;
        }

        List<ProductCardDTO> cards = pageData.getContent();

        List<Long> allSkuIds = cards.stream()
                .flatMap(card -> card.getSkuIds().stream())
                .distinct().toList();

        List<String> imagePaths = cards.stream()
                .map(ProductCardDTO::getImageUrl)
                .filter(path -> path != null && !path.isEmpty())
                .distinct().toList();
        log.error(imagePaths.toString());
        Map<Long, Boolean> sellableMap = getSellableBooleanMap(allSkuIds);
        Map<String, String> imageUrlMap = getImageUrlMap(imagePaths);

        cards.forEach(card -> {
            boolean hasStock = card.getSkuIds().stream()
                    .anyMatch(skuId -> sellableMap.getOrDefault(skuId, false));

            card.setIsOutOfStock(!hasStock);
            card.setDisplayStatus(determineDisplayStatus(card.getSaleStatus(), hasStock));

            if (card.getImageUrl() != null && imageUrlMap.containsKey(card.getImageUrl())) {
                card.setImageUrl(imageUrlMap.get(card.getImageUrl()));
            }
        });


        return pageData;
    }

    public ProductFullDetailDTO getDetail(Integer productId) {

        var detailResponse = productClient.getDetail(productId);
        ProductDetailDTO detail = detailResponse.result();
        if (detail == null) return null;

        List<Long> skuIds = detail.getSkus().stream()
                .map(ProductDetailDTO.SkuInternalResponse::getId).toList();

        List<String> allPaths = Stream.concat(
                Stream.of(detail.getImageUrl()),
                detail.getGalleryImages().stream()
        ).filter(path -> path != null && !path.isEmpty()).distinct().toList();

        Map<Long, SkuSellableDTO> inventoryMap = getFullInventoryMap(skuIds);

        Map<String, String> urlMap = getImageUrlMap(allPaths);

        ProductFullDetailDTO fullDetail = productConverter.toFullDetail(detail, inventoryMap);

        if (fullDetail.getImageUrl() != null) {
            fullDetail.setImageUrl(urlMap.getOrDefault(fullDetail.getImageUrl(), fullDetail.getImageUrl()));
        }
        if (fullDetail.getGalleryImages() != null) {
            fullDetail.setGalleryImages(fullDetail.getGalleryImages().stream()
                    .map(path -> urlMap.getOrDefault(path, path))
                    .toList());
        }

        return fullDetail;
    }


    private Map<Long, Boolean> getSellableBooleanMap(List<Long> skuIds) {
        Map<Long, SkuSellableDTO> fullMap = getFullInventoryMap(skuIds);
        return fullMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().isSellable()));
    }

    private Map<Long, SkuSellableDTO> getFullInventoryMap(List<Long> skuIds) {
        if (skuIds == null || skuIds.isEmpty()) return Map.of();
        try {
            var response = inventoryClient.checkSellable(skuIds);
            List<SkuSellableDTO> list = response.result();

            if (list == null) return Map.of();
            return list.stream()
                    .collect(Collectors.toMap(SkuSellableDTO::getSkuId, dto -> dto, (v1, v2) -> v1));
        } catch (Exception e) {
            log.error("Failed to check inventory: {}", e.getMessage());
            return Map.of();
        }
    }

    private Map<String, String> getImageUrlMap(List<String> paths) {
        if (paths == null || paths.isEmpty()) return Map.of();
        try {
            var response = mediaClient.getPreviewUrls(paths);
            return response.result() != null ? response.result() : Map.of();
        } catch (Exception e) {
            log.error("Failed to fetch image URLs: {}", e.getMessage());
            return Map.of();
        }
    }

    private String determineDisplayStatus(SaleStatus saleStatus, boolean hasStock) {
        if (!hasStock&&!saleStatus.equals(SaleStatus.RUMOR)) return DisplayStatus.OUT_OF_STOCK.name();
        return switch (saleStatus) {
            case RUMOR -> DisplayStatus.RUMOR.name();
            case COMING_SOON -> DisplayStatus.COMING_SOON.name();
            case PREORDER_OPEN -> DisplayStatus.PREORDER_OPEN.name();
            case PREORDER_CLOSED -> DisplayStatus.PREORDER_CLOSED.name();
            case AVAILABLE -> DisplayStatus.IN_STOCK.name();
            default -> DisplayStatus.IN_STOCK.name();
        };
    }


    public List<SkuStockResponse> getProductStockByWarehouse(Integer productId, Integer warehouseId) {
        ApiResponse<List<SkuResponse>> skuResponse = productClient.getSkus(productId);
        List<SkuResponse> skus = skuResponse.result();

        if (skus == null || skus.isEmpty()) {
            return Collections.emptyList();
        }

        return skus.stream()
                .map(sku -> inventoryClient.getStock(sku.getId(), warehouseId).result())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public Map<Long, List<SkuStockResponse>> getAllProductStocks(Integer productId) {
        ApiResponse<List<SkuResponse>> skuResponse = productClient.getSkus(productId);
        List<SkuResponse> skus = skuResponse.result();

        if (skus == null || skus.isEmpty()) {
            return Collections.emptyMap();
        }

        return skus.stream()
                .map(sku -> inventoryClient.getAllWarehousesStock(sku.getId()).result())
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .collect(Collectors.groupingBy(SkuStockResponse::getSkuId));
    }
    public List<SkuResponse> getSkusByProduct(Integer productId)
    {
        return productClient.getSkus(productId).result();
    }
    public List<SkuSearchResponse> searchSkus( String keyword)
    {
        return productClient.quickSearch(keyword).result();
    }

    public List<SkuInfoResponse> getSkuInfosForSearch(Integer productId, Integer currentWarehouseId) {
        List<SkuResponse> skus = productClient.getSkus(productId).result();
        if (skus == null || skus.isEmpty()) return Collections.emptyList();

        List<Long> skuIds = skus.stream().map(SkuResponse::getId).toList();

        Map<Long, SkuSellableDTO> sellableMap = getFullInventoryMap(skuIds);
        Map<Long, List<SkuStockResponse>> allStocksMap = new ConcurrentHashMap<>();

        List<CompletableFuture<Void>> futures = skus.stream().map(sku ->
                CompletableFuture.supplyAsync(() -> {
                    try {
                        var response = inventoryClient.getAllWarehousesStock(sku.getId());
                        return response.result();
                    } catch (Exception e) {
                        log.error("Failed to fetch stocks for SKU ID {}: {}", sku.getId(), e.getMessage());
                        return null;
                    }
                }).thenAccept(stockList -> {
                    if (stockList != null) {
                        allStocksMap.put(sku.getId(), stockList);
                    }
                })
        ).toList();
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        return skus.stream().map(sku -> {
            List<SkuStockResponse> stockDetails = allStocksMap.getOrDefault(sku.getId(), Collections.emptyList());
            SkuSellableDTO sellable = sellableMap.getOrDefault(sku.getId(), new SkuSellableDTO());
            int currentWhStock = stockDetails.stream()
                    .filter(s -> s.getWarehouseId().equals(currentWarehouseId))
                    .mapToInt(s -> s.getAvailableStock() != null ? s.getAvailableStock() : 0)
                    .sum();
            int otherWhStock = stockDetails.stream()
                    .filter(s -> !s.getWarehouseId().equals(currentWarehouseId))
                    .mapToInt(s -> s.getAvailableStock() != null ? s.getAvailableStock() : 0)
                    .sum();

            return SkuInfoResponse.builder()
                    .id(sku.getId())
                    .skuCode(sku.getSkuCode())
                    .sellingPrice(sku.getSellingPrice())
                    .prePrice(sku.getPrePrice())
                    .preDepositAmount(sku.getPreDepositAmount())
                    .isActive(sku.getIsActive())
                    .currentWarehouseStock(currentWhStock)
                    .otherWarehousesStock(otherWhStock)
                    .inventoryStatus(sellable)
                    .build();
        }).collect(Collectors.toList());
    }
}