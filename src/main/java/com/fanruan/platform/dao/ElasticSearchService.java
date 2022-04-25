package com.fanruan.platform.dao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fanruan.platform.bean.Company;
import com.fanruan.platform.util.RestClientFactory;
import com.google.common.collect.Lists;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.document.DocumentField;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

@Service
public class ElasticSearchService {

    @Value("${platform.es.host}")
    private String host;
    @Value("${platform.es.port}")
    private int port;
    @Value("${platform.es.schema}")
    private String schema;

    public Integer saveCompanyList(List<Company> companyList){
        RestHighLevelClient client = RestClientFactory.getHighLevelClient(host,port,schema);

        for(Company company:companyList){
            Integer code = saveCompany(client, company);
            if(code!=0){
                System.out.println("建立es索引异常");
            }
        }

        return 0;
    }


    private Integer saveCompany(RestHighLevelClient client, Company company) {
        Map<String,Object> jsonMap = JSONObject.parseObject(JSON.toJSONString(company));
        IndexRequest indexRequest = new IndexRequest("credit-platform")
                .id(String.valueOf(company.getCompanyId())).source(jsonMap);
        IndexResponse indexResponse = null;
        try {
             indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            return 1;
        }
        return 0;
    }

    public List<Company> QueryCompanyList(String keyword, Integer page) {
        RestHighLevelClient client = RestClientFactory.getHighLevelClient(host,port,schema);
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//        HighlightBuilder highlightBuilder = new HighlightBuilder();
//        highlightBuilder.field("operName");
//        highlightBuilder.field("companyName");
//        highlightBuilder.requireFieldMatch(false);
//        highlightBuilder.preTags("<span style=\"color:red\">");
//        highlightBuilder.postTags("</span>");
//        highlightBuilder.fragmentSize(800000);
//        highlightBuilder.numOfFragments(0);
//        searchSourceBuilder.highlighter(highlightBuilder);

        MatchQueryBuilder companyName = QueryBuilders.matchQuery("companyName", keyword);
        companyName.operator(Operator.AND);
        TermQueryBuilder creditCode = QueryBuilders.termQuery("creditCode", keyword);
        BoolQueryBuilder should = QueryBuilders.boolQuery().should(companyName).should(creditCode);
//        searchSourceBuilder.query( QueryBuilders.multiMatchQuery(keyword,"companyName","creditCode"));
        searchSourceBuilder.query(should);
        searchSourceBuilder.from((page-1)*20);
        searchSourceBuilder.size(20);
        searchRequest.source(searchSourceBuilder);

        SearchResponse response = null;
        try {
            response = client.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Long time = System.currentTimeMillis();
        List<Company> companyList = Lists.newArrayList();
        if(response!=null&&response.getHits()!=null&&response.getHits().getHits()!=null){
            SearchHit[] hits = response.getHits().getHits();
            for(SearchHit searchHit:hits){
                String id = searchHit.getId();
                Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
                String jsonStr = JSONObject.toJSONString(sourceAsMap);
//                Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
                Company company = JSONObject.parseObject(jsonStr, Company.class);
                Timestamp updateTime = company.getUpdateTime();
                //半年以内的数据为有效数据
                if(updateTime!=null&&(time-updateTime.getTime())<(180*24*60*60*1000L)){
                    companyList.add(company);

                }
//                if(updateTime!=null&&(time-updateTime.getTime())<(24*60*60*1000L)){
//                    companyList.add(company);
//                }
            }
        }

        //排重
        HashMap<Long, Company> companyMap = new HashMap<>();
        for(Company company : companyList){

            if(companyMap.containsKey(company.getId())
                    && company.getUpdateTime().before(companyMap.get(company.getId()).getUpdateTime()) ) continue;

            companyMap.put(company.getId(),company);
        }

        List<Company> companyListResult = new ArrayList<>();

        for(Company company : companyMap.values()){
            companyListResult.add(company);
        }

        return companyListResult;
    }
}
