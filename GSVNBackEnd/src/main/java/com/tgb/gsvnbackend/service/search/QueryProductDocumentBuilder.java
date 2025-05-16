package com.tgb.gsvnbackend.service.search;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.TermsQueryField;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class QueryProductDocumentBuilder {
    private QueryProductDocumentBuilder() {
    }
    public static SearchRequest buildSearchRequest(String title,
            List<Integer> brandIds, List<Integer> categoryIds, List<Integer> fandomIds,
                                                   Double minPrice,Double maxPrice,int page,int size,String sortBy)
    {
        SearchRequest.Builder builder = new SearchRequest.Builder();
        builder.index("product");
        Query.Builder queryBuilder = new Query.Builder();

        BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();
        if(title!=null&&!title.isEmpty())
        {
            boolQueryBuilder.must(Query.of(q->q.fuzzy(f->f.field("title").value(title).fuzziness("AUTO"))));
        }
        if (brandIds != null && !brandIds.isEmpty()) {
            TermsQueryField brandField = new TermsQueryField.Builder()
                    .value(Stream.of(brandIds).map(FieldValue::of).collect(Collectors.toList()))
                    .build();
            boolQueryBuilder.should(QueryBuilders.terms(term->term.field("brandId").terms(brandField)));
        }
        if (categoryIds != null && !categoryIds.isEmpty()) {
            TermsQueryField categoryField = new TermsQueryField.Builder()
                    .value(Stream.of(brandIds).map(FieldValue::of).collect(Collectors.toList()))
                    .build();
            boolQueryBuilder.should(QueryBuilders.terms(term->term.field("categoryId").terms(categoryField)));
        }
        if (fandomIds != null && !fandomIds.isEmpty()) {
            TermsQueryField fandomField = new TermsQueryField.Builder()
                    .value(Stream.of(fandomIds).map(FieldValue::of).collect(Collectors.toList()))
                    .build();
            boolQueryBuilder.should(QueryBuilders.terms(term->term.field("fandomId").terms(fandomField)));
        }
        if(minPrice!=null)
        {
            RangeQuery priceRange=new RangeQuery.Builder().number(x->x.field("lowestPrice").gte(minPrice)).build();
           boolQueryBuilder.should(priceRange._toQuery());
        }
        if(maxPrice!=null)
        {
            RangeQuery priceRange=new RangeQuery.Builder().number(x->x.field("lowestPrice").gte(maxPrice)).build();
            boolQueryBuilder.should(priceRange._toQuery());
        }
        queryBuilder.bool(boolQueryBuilder.build());
        builder.query(queryBuilder.build());
        int offset = (page - 1) * size;
        builder.from(offset);
        builder.size(size);

        // Thêm sắp xếp
        if (sortBy != null && !sortBy.isEmpty()) {
            SortOptions.Builder sortBuilder = new SortOptions.Builder();
            switch (sortBy.toLowerCase()) {
                case "price-asc":
                    sortBuilder.field(f -> f.field("lowestPrice").order(SortOrder.Asc));
                    break;
                case "price-desc":
                    sortBuilder.field(f -> f.field("lowestPrice").order(SortOrder.Desc));
                    break;
                default:
                    sortBuilder.field(f -> f.field("spuId").order(SortOrder.Desc));
                    break;
            }
            builder.sort(sortBuilder.build());
        }
        return builder.build();
    }

}
