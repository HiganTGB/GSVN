package com.gsvn.productservice.controller;

import com.gsvn.productservice.common.ApiResponse;
import com.gsvn.productservice.common.PageResponse;
import com.gsvn.productservice.model.dto.request.ProductBasicUpdateRequest;
import com.gsvn.productservice.model.dto.request.ProductPreOrderUpdateRequest;
import com.gsvn.productservice.model.dto.request.ProductCreateRequest;
import com.gsvn.productservice.model.dto.response.PreHistoryResponse;
import com.gsvn.productservice.model.dto.response.ProductBasicResponse;
import com.gsvn.productservice.model.dto.response.ProductPreCampaignResponse;
import com.gsvn.productservice.model.dto.response.SkuResponse;
import com.gsvn.productservice.model.entity.SaleStatus;
import com.gsvn.productservice.service.ProductService;
import com.gsvn.productservice.service.SkuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final SkuService skuService;

    @PostMapping
    public ApiResponse<Integer> create(@RequestBody @Valid ProductCreateRequest request) {
        return new ApiResponse<>(productService.createProduct(request));
    }

    @PutMapping("/{id}/basic")
    public ApiResponse<ProductBasicResponse> updateBasic(
            @PathVariable Integer id,
            @RequestBody @Valid ProductBasicUpdateRequest request) {
        return new ApiResponse<>(productService.updateBasic(request, id));
    }


    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        productService.deleteProduct(id);
        return new ApiResponse<>();
    }


    @PutMapping("/{id}/pre")
    public ApiResponse<ProductPreCampaignResponse> updatePreCampaign(
            @PathVariable Integer id,
            @RequestBody @Valid ProductPreOrderUpdateRequest request) {
        return new ApiResponse<>(productService.updatePreCampaign(request, id));
    }

    @DeleteMapping("/{id}/pre")
    public ApiResponse<ProductPreCampaignResponse> resetCampaign(@PathVariable Integer id) {
        return new ApiResponse<>(productService.deletePreCampaign(id));
    }

    @PostMapping(value = "/{id}/main-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> uploadMainImage(
            @PathVariable Integer id,
            @RequestPart("file") MultipartFile file) {
        return new ApiResponse<>(productService.uploadMainImage(id, file));
    }

    @GetMapping(value = "/{id}/gallery")
    public ApiResponse<Map<String,String>> getGalleryImages(
            @PathVariable Integer id){
        return new ApiResponse<>(productService.getGallery(id));
    }
    @PostMapping(value = "/{id}/gallery", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<List<String>> addGalleryImage(
            @PathVariable Integer id,
            @RequestPart("file") MultipartFile file) {
        return new ApiResponse<>(productService.addGalleryImage(id, file));
    }

    @DeleteMapping("/{id}/gallery")
    public ApiResponse<Void> deleteGalleryImage(
            @PathVariable Integer id,
            @RequestParam String objectName) {
        productService.deleteGalleryImage(id, objectName);
        return new ApiResponse<>();
    }


    @GetMapping("/search")
    public ApiResponse<PageResponse<ProductBasicResponse>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer brandId,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) SaleStatus saleStatus,
            @RequestParam(required = false,defaultValue = "true") Boolean isActive,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        return new ApiResponse<>(productService.getPage(
                keyword, brandId, categoryId, saleStatus,isActive,
                sortBy, direction, page, size
        ));
    }
    @GetMapping("/{id}/basic")
    public ApiResponse<ProductBasicResponse> getBasic(
            @PathVariable Integer id) {
        return new ApiResponse<>(productService.getBasic(id));
    }
    @GetMapping("/{id}/pre")
    public ApiResponse<ProductPreCampaignResponse> getPreCampaign(
            @PathVariable Integer id) {
        return new ApiResponse<>(productService.getPreResponse(id));
    }
    @GetMapping("/{id}/history")
    public ApiResponse<List<PreHistoryResponse>> getHistoryPreCampaign(@PathVariable Integer id)
    {
     return new ApiResponse<>(productService.getPreHistoryResponse(id));
    }
    @GetMapping("/internal/{id}/skus")
    public ApiResponse<List<SkuResponse>> getSkus(@PathVariable Integer id) {
        return new ApiResponse<>(skuService.getSkusByProduct(id));
    }

}