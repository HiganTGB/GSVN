package com.tgb.gsvnbackend.service.index;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.transport.endpoints.BooleanResponse;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.data.elasticsearch.annotations.Document;

import org.springframework.util.CollectionUtils;


import org.springframework.core.io.Resource;

import org.springframework.data.elasticsearch.annotations.Mapping;

import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@Slf4j
public class IndexService {
    private final ElasticsearchClient client;
    private final ResourceLoader resourceLoader;

    public IndexService(ElasticsearchClient client, ResourceLoader resourceLoader) {
        this.client = client;
        this.resourceLoader = resourceLoader;
    }

    public void createIndices() {
        final List<IndexInfo> indexInformation = getIndexInformation();
        if (CollectionUtils.isEmpty(indexInformation)) {
            return;
        }

        for (final IndexInfo indexInfo : indexInformation) {
            delete(indexInfo);
            create(indexInfo);
        }
    }

    private void create(IndexInfo indexInfo) {
        try {
            client.indices()
                    .create(c -> c.index(indexInfo.name())
                            .mappings(t -> t.withJson(getMappings(indexInfo.mappingPath()))));
        } catch (Exception e) {
            log.error("{}", e.getMessage(), e);
        }
    }

    private void delete(IndexInfo indexInfo) {
        try {
            final BooleanResponse exists = client.indices().exists(e -> e.index(indexInfo.name()));
            if (!exists.value()) {
                return;
            }

            client.indices().delete(d -> d.index(indexInfo.name()));
        } catch (Exception e) {
            log.error("{}", e.getMessage(), e);
        }
    }

    private List<IndexInfo> getIndexInformation() {
        final var scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Document.class));

        final Set<BeanDefinition> beanDefinitions = scanner.findCandidateComponents(
                "com.lilium.register"
        );

        return beanDefinitions.stream()
                .map(IndexService::getIndexInfo)
                .filter(Objects::nonNull)
                .toList();
    }

    private static IndexInfo getIndexInfo(final BeanDefinition definition) {
        try {
            final Class<?> documentClass = Class.forName(definition.getBeanClassName());

            final Document annotation = documentClass.getAnnotation(Document.class);
            return new IndexInfo(
                    getIndexName(documentClass),
                    getIndexMappingPath(documentClass)
            );
        } catch (final ClassNotFoundException e) {
            log.error("{}", e.getMessage(), e);
            return null;
        }
    }
    private static String getIndexName(final Class<?> documentClass) {
        final Document annotation = documentClass.getAnnotation(Document.class);
        return annotation.indexName();
    }

    private static String getIndexMappingPath(final Class<?> documentClass) {
        final Mapping annotation = documentClass.getAnnotation(Mapping.class);
        return annotation.mappingPath();
    }

    private InputStream getMappings(final String mappingPath) {
        try {
            Resource resource = resourceLoader.getResource("classpath:" + mappingPath);
            return resource.getInputStream();
        } catch (Exception e) {
            log.error("{}", e.getMessage(), e);
            return null;
        }
    }
}
