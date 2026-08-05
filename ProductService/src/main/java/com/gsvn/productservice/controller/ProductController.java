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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Product Management", description = "Endpoints for managing products, Pre-Order campaigns, media galleries, and internal SKU lookups")
public class ProductController {

    private final ProductService productService;
    private final SkuService skuService;

    @Operation(summary = "Create product", description = "Creates a new master product entry in the catalog.")
    @PostMapping
    @PreAuthorize("hasAuthority('all') or hasAuthority('product_create')")
    public ApiResponse<Integer> create(@RequestBody @Valid ProductCreateRequest request) {
        return new ApiResponse<>(productService.createProduct(request));
    }

    @Operation(summary = "Update basic product details", description = "Updates basic information for an existing product by ID.")
    @PutMapping("/{id}/basic")
    @PreAuthorize("hasAuthority('all') or hasAuthority('product_update')")
    public ApiResponse<ProductBasicResponse> updateBasic(
            @Parameter(description = "ID of the product") @PathVariable Integer id,
            @RequestBody @Valid ProductBasicUpdateRequest request) {
        return new ApiResponse<>(productService.updateBasic(request, id));
    }

    @Operation(summary = "Delete product", description = "Deletes or soft-deletes a product from the catalog by ID.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('all') or hasAuthority('product_delete')")
    public ApiResponse<Void> delete(
            @Parameter(description = "ID of the product to delete") @PathVariable Integer id) {
        productService.deleteProduct(id);
        return new ApiResponse<>();
    }

    @Operation(summary = "Update Pre-Order campaign", description = "Configures or updates Pre-Order campaign settings and timeline for a product.")
    @PutMapping("/{id}/pre")
    @PreAuthorize("hasAuthority('all') or hasAuthority('product_update')")
    public ApiResponse<ProductPreCampaignResponse> updatePreCampaign(
            @Parameter(description = "ID of the product") @PathVariable Integer id,
            @RequestBody @Valid ProductPreOrderUpdateRequest request) {
        return new ApiResponse<>(productService.updatePreCampaign(request, id));
    }

    @Operation(summary = "Reset/Delete Pre-Order campaign", description = "Removes active Pre-Order campaign configuration from a product.")
    @DeleteMapping("/{id}/pre")
    @PreAuthorize("hasAuthority('all') or hasAuthority('product_update')")
    public ApiResponse<ProductPreCampaignResponse> resetCampaign(
            @Parameter(description = "ID of the product") @PathVariable Integer id) {
        return new ApiResponse<>(productService.deletePreCampaign(id));
    }

    @Operation(summary = "Upload main product image", description = "Uploads a main showcase image for the product using multipart form-data.")
    @PreAuthorize("hasAuthority('all') or hasAuthority('product_update')")
    @PostMapping(value = "/{id}/main-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> uploadMainImage(
            @Parameter(description = "ID of the product") @PathVariable Integer id,
            @Parameter(
                    description = "Image file to upload (JPEG, PNG)",
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE, schema = @Schema(type = "string", format = "binary"))
            )
            @RequestPart("file") MultipartFile file) {
        return new ApiResponse<>(productService.uploadMainImage(id, file));
    }

    @Operation(summary = "Get product gallery images", description = "Retrieves all gallery image object names and URLs associated with a product.")
    @GetMapping(value = "/{id}/gallery")
    public ApiResponse<Map<String, String>> getGalleryImages(
            @Parameter(description = "ID of the product") @PathVariable Integer id) {
        return new ApiResponse<>(productService.getGallery(id));
    }

    @Operation(summary = "Add image to product gallery", description = "Uploads an additional image to the product gallery.")
    @PostMapping(value = "/{id}/gallery", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<List<String>> addGalleryImage(
            @Parameter(description = "ID of the product") @PathVariable Integer id,
            @Parameter(
                    description = "Gallery image file to upload",
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE, schema = @Schema(type = "string", format = "binary"))
            )
            @RequestPart("file") MultipartFile file) {
        return new ApiResponse<>(productService.addGalleryImage(id, file));
    }

    @Operation(summary = "Delete image from product gallery", description = "Removes a specific gallery image by its storage object name.")
    @DeleteMapping("/{id}/gallery")
    public ApiResponse<Void> deleteGalleryImage(
            @Parameter(description = "ID of the product") @PathVariable Integer id,
            @Parameter(description = "Storage object name of the image to remove") @RequestParam String objectName) {
        productService.deleteGalleryImage(id, objectName);
        return new ApiResponse<>();
    }

    @Operation(summary = "Search products with pagination", description = "Retrieves a paginated list of products filtered by keyword, brand, category, sale status, and active state.")
    @GetMapping("/search")
    public ApiResponse<PageResponse<ProductBasicResponse>> search(
            @Parameter(description = "Keyword to search by product name or code") @RequestParam(required = false) String keyword,
            @Parameter(description = "Filter by brand ID") @RequestParam(required = false) Integer brandId,
            @Parameter(description = "Filter by category ID") @RequestParam(required = false) Integer categoryId,
            @Parameter(description = "Filter by sale status (e.g., NORMAL, PRE_ORDER)") @RequestParam(required = false) SaleStatus saleStatus,
            @Parameter(description = "Filter by active state (true for active, false for inactive)") @RequestParam(required = false, defaultValue = "true") Boolean isActive,
            @Parameter(description = "Field name to sort results by") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sorting direction: 'ASC' or 'DESC'") @RequestParam(defaultValue = "DESC") String direction,
            @Parameter(description = "Page number (1-based index)") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size) {

        return new ApiResponse<>(productService.getPage(
                keyword, brandId, categoryId, saleStatus, isActive,
                sortBy, direction, page, size
        ));
    }

    @Operation(summary = "Get basic product details", description = "Retrieves basic profile information for a specific product by ID.")
    @GetMapping("/{id}/basic")
    public ApiResponse<ProductBasicResponse> getBasic(
            @Parameter(description = "ID of the product") @PathVariable Integer id) {
        return new ApiResponse<>(productService.getBasic(id));
    }

    @Operation(summary = "Get Pre-Order campaign details", description = "Retrieves active Pre-Order campaign information for a specific product.")
    @GetMapping("/{id}/pre")
    public ApiResponse<ProductPreCampaignResponse> getPreCampaign(
            @Parameter(description = "ID of the product") @PathVariable Integer id) {
        return new ApiResponse<>(productService.getPreResponse(id));
    }

    @Operation(summary = "Get Pre-Order campaign history", description = "Retrieves historical logs of Pre-Order campaigns for a specific product.")
    @PreAuthorize("hasAuthority('all') or hasAuthority('product_read')")
    @GetMapping("/{id}/history")
    public ApiResponse<List<PreHistoryResponse>> getHistoryPreCampaign(
            @Parameter(description = "ID of the product") @PathVariable Integer id) {
        return new ApiResponse<>(productService.getPreHistoryResponse(id));
    }

    @Operation(summary = "Get SKUs by product ID (Internal)", description = "Internal endpoint to retrieve all SKU variants under a specific master product.")
    @GetMapping("/internal/{id}/skus")
    public ApiResponse<List<SkuResponse>> getSkus(
            @Parameter(description = "ID of the product") @PathVariable Integer id) {
        return new ApiResponse<>(skuService.getSkusByProduct(id));
    }
}