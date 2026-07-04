package com.gsvn.productservice.service.impl;

import com.gsvn.productservice.client.MediaClient;
import com.gsvn.productservice.common.ApiResponse;
import com.gsvn.productservice.common.PageResponse;
import com.gsvn.productservice.converter.ProductConverter;
import com.gsvn.productservice.exc.AppException;
import com.gsvn.productservice.exc.ErrorCode;
import com.gsvn.productservice.exc.InvalidResourceException;
import com.gsvn.productservice.mapper.*;
import com.gsvn.productservice.model.dto.request.ProductBasicUpdateRequest;
import com.gsvn.productservice.model.dto.request.ProductPreOrderUpdateRequest;
import com.gsvn.productservice.model.dto.request.ProductCreateRequest;
import com.gsvn.productservice.model.dto.response.*;
import com.gsvn.productservice.model.dto.response.ProductBasicResponse;
import com.gsvn.productservice.model.dto.response.ProductPreCampaignResponse;
import com.gsvn.productservice.model.entity.Product;
import com.gsvn.productservice.model.entity.ProductPreHistory;
import com.gsvn.productservice.model.entity.SaleStatus;
import com.gsvn.productservice.model.internal.UploadType;
import com.gsvn.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private final ProductConverter productConverter;
    private final MediaClient mediaClient;
    private final BrandMapper brandMapper;
    private final CategoryMapper categoryMapper;
    @Transactional
    public Integer createProduct(ProductCreateRequest request) {
        Product product = productConverter.toEntity(request);
        if(!brandMapper.existsById(request.getBrandId()))
        {
            throw new InvalidResourceException(ErrorCode.INVALID_KEY,"brandId");
        }
        if(!categoryMapper.existsById(request.getCategoryId()))
        {
            throw new InvalidResourceException(ErrorCode.INVALID_KEY,"categoryId");
        }
        productMapper.insert(product);
        return product.getId();
    }
    @Transactional
    public ProductBasicResponse updateBasic(ProductBasicUpdateRequest request,Integer productId)
    {
        Product product = productMapper.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.ITEM_NOT_EXISTED));
        if(!brandMapper.existsById(request.getBrandId()))
        {
            throw new InvalidResourceException(ErrorCode.INVALID_KEY,"brandId");
        }
        if(!categoryMapper.existsById(request.getCategoryId()))
        {
            throw new InvalidResourceException(ErrorCode.INVALID_KEY,"categoryId");
        }
        productConverter.updateBasicEntity(request,product);
        productMapper.updateBasic(product);
        return productConverter.toBasicResponse(product);
    }

    @Transactional
    public void deleteProduct(Integer productId) {
        if (!productMapper.existsById(productId)) {
            throw new AppException(ErrorCode.ITEM_NOT_EXISTED);
        }
        productMapper.delete(productId);
    }

    @Transactional
    public ProductPreCampaignResponse updatePreCampaign(ProductPreOrderUpdateRequest request, Integer productId)
    {
        Product product = productMapper.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.ITEM_NOT_EXISTED));
        productConverter.updateCampaignEntity(request,product);
        productMapper.updatePreOrder(product);
        var productPreHistory=ProductPreHistory.builder()
                .preName(product.getPreName())
                .preStartAt(product.getPreStartAt())
                .preEndAt(product.getPreEndAt())
                .productId(product.getId())
                .preReleaseDate(product.getPreReleaseDate())
                .build();
        productMapper.insertPreOrderHistory(productPreHistory);
        return productConverter.toPreCampaignResponse(product);
    }
    @Transactional
    public Boolean changeActivePreCampaign(Integer productId,Boolean isActive)
    {
        Product product = productMapper.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.ITEM_NOT_EXISTED));
        if(!product.canChangeActiveCampaign())
        {
            throw new AppException(ErrorCode.INVALID_REQUEST_BODY);
        }
        productMapper.toggleActiveCampaign(productId,isActive);
        return true;
    }

    @Transactional
    public ProductPreCampaignResponse deletePreCampaign(Integer productId) {
        if (!productMapper.existsById(productId)) {
            throw new AppException(ErrorCode.ITEM_NOT_EXISTED);
        }
        productMapper.deletePreOrder(productId);
        return productConverter.toPreCampaignResponse(productMapper.findById(productId).get());
    }

    @Transactional
    public String uploadMainImage(Integer productId, MultipartFile file) {
        ApiResponse<String> mediaResponse = mediaClient.upload(file, UploadType.PRODUCT_AVATAR.toString(), String.valueOf(productId));
        if (mediaResponse.result() != null) {
            String imageUrl = mediaResponse.result();
            productMapper.updateImages(productId, imageUrl, null);
            return imageUrl;
        }
        throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
    }

    @Transactional
    public List<String> addGalleryImage(Integer productId, MultipartFile file) {
        Product product = productMapper.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.ITEM_NOT_EXISTED));

        ApiResponse<String> mediaResponse = mediaClient.upload(file, UploadType.PRODUCT_GALLERY.toString(), String.valueOf(productId));
        String newImageUrl = mediaResponse.result();

        List<String> currentGallery = product.getGalleryImages();
        if (currentGallery == null) {
            currentGallery = new ArrayList<>();
        }
        currentGallery.add(newImageUrl);

        productMapper.updateImages(productId, null, currentGallery);
        return currentGallery;
    }

    @Transactional
    public void deleteGalleryImage(Integer productId, String objectName) {
        Product product = productMapper.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.ITEM_NOT_EXISTED));

        mediaClient.deleteFile(objectName);

        List<String> currentGallery = product.getGalleryImages();
        if (currentGallery != null) {
            currentGallery.removeIf(url -> url.contains(objectName));
            // Cập nhật lại List
            productMapper.updateImages(productId, null, currentGallery);
        }
    }

    private Map<String, String> prepareSignedUrlMap(Product product) {
        List<String> allPaths = new ArrayList<>();
        if (product.getImageUrl() != null) allPaths.add(product.getImageUrl());

        if (product.getGalleryImages() != null) {
            allPaths.addAll(product.getGalleryImages());
        }

        if (allPaths.isEmpty()) return Map.of();

        try {
            ApiResponse<Map<String, String>> response = mediaClient.getPreviewUrls(allPaths);
            return response.result() != null ? response.result() : Map.of();
        } catch (Exception e) {
            log.error("Failed to fetch signed URLs", e);
            return Map.of();
        }
    }

    public PageResponse<ProductBasicResponse> getPage(String keyword,
                                                      Integer brandId,
                                                      Integer categoryId,
                                                      SaleStatus saleStatus,
                                                      Boolean isActive,
                                                      String sortBy,
                                                      String direction,
                                                      int page,
                                                      int size) {

        int offset = (page - 1) * size;

        String sortField = switch (sortBy != null ? sortBy : "") {
            case "name" -> "name";
            case "price" -> "id";
            default -> "created_at";
        };

        String sortOrder = "asc".equalsIgnoreCase(direction) ? "ASC" : "DESC";

        List<Product> entities = productMapper.findPage(
                keyword,
                brandId,
                categoryId,
                saleStatus,
                isActive,
                offset,
                size,
                sortField,
                sortOrder
        );
        List<ProductBasicResponse> content = entities.stream()
                .map(productConverter::toBasicResponse)
                .toList();
        List<String> imagePaths = content.stream()
                .map(ProductBasicResponse::getImageUrl)
                .filter(path -> path != null && !path.isEmpty())
                .distinct()
                .toList();
        if (!imagePaths.isEmpty()) {
            Map<String, String> urlMap = mediaClient.getPreviewUrls(imagePaths).result();
            content.forEach(response -> {
                if (response.getImageUrl() != null) {
                    String fullUrl = urlMap.getOrDefault(response.getImageUrl(), response.getImageUrl());
                    response.setImageUrl(fullUrl);
                }
            });
        }

        long totalElements = productMapper.countSearch(keyword, brandId, categoryId, saleStatus ,isActive);

        return PageResponse.of(content, totalElements, page, size);
    }
    @Scheduled(cron = "0 0 0 * * *")
    private void syncProductStatuses() {
        try {
            int rows = productMapper.updateProductStatusForCron();
        } catch (Exception e) {
            log.error("Error cron job: {}", e.getMessage());
        }
    }
    // For Admin
    @Transactional(readOnly = true)
    public ProductBasicResponse getBasic(Integer productId)
    {
        Product product = productMapper.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.ITEM_NOT_EXISTED));
        try {
            if(!product.getImageUrl().isBlank())
            {
                product.setImageUrl(mediaClient.getPreviewUrl(product.getImageUrl()).result());
            }
        }catch (Exception e)
        {
            product.setImageUrl(null);
        }
        return productConverter.toBasicResponse(product);
    }
    @Transactional(readOnly = true)
    public ProductPreCampaignResponse getPreResponse(Integer productId)
    {
        Product product = productMapper.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.ITEM_NOT_EXISTED));
        return productConverter.toPreCampaignResponse(product);
    }
    @Transactional(readOnly = true)
    public List<PreHistoryResponse> getPreHistoryResponse(Integer productId)
    {
        var history = productMapper.findPreHistoryByProduct(productId);
        return productConverter.toHistoryResponseList(history);
    }
    @Transactional(readOnly = true)
    public Map<String,String> getGallery(Integer productId)
    {
        Product product = productMapper.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.ITEM_NOT_EXISTED));
        if(product.getGalleryImages()!=null&&!product.getGalleryImages().isEmpty())
        {
            return mediaClient.getPreviewUrls(product.getGalleryImages()).result();
        }
        else
            return Collections.emptyMap();

    }

}