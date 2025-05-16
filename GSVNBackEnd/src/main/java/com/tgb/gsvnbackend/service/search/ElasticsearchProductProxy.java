package com.tgb.gsvnbackend.service.search;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.tgb.gsvnbackend.model.entity.ProductDocument;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class ElasticsearchProductProxy<E extends ProductDocument> {


    private final ElasticsearchClient client;

    public ElasticsearchProductProxy(ElasticsearchClient client) {
        this.client = client;
    }

    public List<E> search(String title,
                          List<Integer> brandIds, List<Integer> categoryIds, List<Integer> fandomIds,
                          Double minPrice,Double maxPrice,int page,int size,String sortBy, final Class<E> documentClass) {
        try {
            SearchResponse<E> response = client.search(
                    QueryProductDocumentBuilder.buildSearchRequest(title,brandIds,categoryIds,fandomIds,minPrice,maxPrice,page,size,sortBy),
                    documentClass
            );

            List<E> documents = response.hits().hits().stream().map(Hit::source).toList();
            return documents;

        } catch (IOException e) {
            log.error("{}", e.getMessage(), e);
            return List.of();
        }
    }
}