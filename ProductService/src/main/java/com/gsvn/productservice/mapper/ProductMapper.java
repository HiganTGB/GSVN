package com.gsvn.productservice.mapper;

import com.gsvn.productservice.model.dto.ProductCardDTO;
import com.gsvn.productservice.model.entity.Product;
import com.gsvn.productservice.model.entity.ProductPreHistory;
import com.gsvn.productservice.model.entity.SaleStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Mapper
public interface ProductMapper {

    // Vùng 0: Tạo mới
    int insert(Product product);

    // Vùng 1: Thông tin cơ bản
    int updateBasic(Product product);

    // Vùng 3: Campaign
    int updatePreOrder(Product product);

    // Vùng 5: Cập nhật Ảnh (Dùng cho cả avatar và gallery)
    // Truyền @Param để khớp với Service: productMapper.updateImages(productId, imageUrl, galleryJson)
    int updateImages(@Param("id") Integer id,
                     @Param("imageUrl") String imageUrl,
                     @Param("galleryImages") List<String> galleryImages);

    // Tìm kiếm và Chi tiết
    Optional<Product> findById(Integer id);

    // Kiểm tra tồn tại
    boolean existsById(Integer id);

    int deletePreOrder(@Param("id") Integer id);

    int delete(@Param("id") Integer id);

    void insertPreOrderHistory(ProductPreHistory history);
    List<ProductPreHistory> findPreHistoryByProduct(@Param("productId") Integer productId);
    List<Product> findPage(
            @Param("keyword") String keyword,
            @Param("brandId") Integer brandId,
            @Param("categoryId") Integer categoryId,
            @Param("saleStatus") SaleStatus saleStatus,
            @Param("isActive") Boolean isActive,
            @Param("offset") int offset,
            @Param("limit") int limit,
            @Param("sortField") String sortField,
            @Param("sortOrder") String sortOrder
    );

    long countSearch(
            @Param("keyword") String keyword,
            @Param("brandId") Integer brandId,
            @Param("categoryId") Integer categoryId,
            @Param("saleStatus") SaleStatus saleStatus,
                 @Param("isActive") Boolean isActive
    );

    int toggleActiveCampaign( @Param("id") Integer id,@Param("keyword") Boolean isActive);
    int toggleActive( @Param("id") Integer id,@Param("keyword") Boolean isActive);

    int updateProductStatusForCron();


    List<ProductCardDTO> searchProducts(
            @Param("keyword") String keyword,
            @Param("brandIds") List<Integer> brandIds,
            @Param("categoryIds") List<Integer> categoryIds,
            @Param("minPriceFilter") BigDecimal minPriceFilter,
            @Param("maxPriceFilter") BigDecimal maxPriceFilter,
            @Param("saleStatuses") List<SaleStatus> saleStatuses,
            @Param("lastCreatedAt") OffsetDateTime lastCreatedAt,
            @Param("sortBy") String sortBy,
            @Param("direction") String direction,
            @Param("lastId") Integer lastId,
            @Param("limit") Integer limit
    );



}