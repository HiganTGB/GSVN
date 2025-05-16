package com.tgb.gsvnbackend.service.search;

import com.tgb.gsvnbackend.model.entity.ProductDocument;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ProductService {
    private final ElasticsearchProductProxy<ProductDocument> client;
    @Autowired
    public ProductService(ElasticsearchProductProxy<ProductDocument> client) {
        this.client = client;
    }
    public List<ProductDocument> search(String title,
                                        List<Integer> brandIds, List<Integer> categoryIds, List<Integer> fandomIds,
                                        Double minPrice,Double maxPrice,int page,int size,String sortBy) {
        return client.search(
                title,
                brandIds,categoryIds,fandomIds,minPrice,maxPrice,page,size,sortBy,
                ProductDocument.class
        );
    }
}
