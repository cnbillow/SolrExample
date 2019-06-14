package com.charles.solrexample.solr;


import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.impl.LBHttpSolrServer;
import org.apache.solr.client.solrj.response.*;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class SolrHandler {


    private SolrjQuery solrjQuery = new SolrjQuery();

    private static final Logger logger = LoggerFactory.getLogger(SolrHandler.class);


    /**
     * @param keyword    关键字
     * @param clazz      结果绑定类型
     * @param startIndex 开始索引
     * @param pageSize   每页数量
     * @param filterMap  过滤字段
     * @param sortMap    排序字段
     * @param <T>
     * @return
     */
    public <T> SearchResult<T> edismaxSearch(String keyword, Class<T> clazz, int startIndex,
                                             int pageSize, Map<String, String> filterMap,
                                             Map<String, SolrQuery.ORDER> sortMap) {
        LBHttpSolrServer solrServer = SolrConnection.getInstance().getSlaveServer();
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setRequestHandler("/courseQuery");
        solrQuery.set("q.alt", "*:*");
        if (StringUtils.isNotBlank(keyword)) {
            solrQuery.setQuery(keyword);
        }
        if (CollectionUtils.isNotEmpty(filterMap.entrySet())) {
            for (Map.Entry<String, String> entry : filterMap.entrySet()) {
                solrQuery.addFilterQuery(entry.getKey() + ":" + entry.getValue());
            }
        }
        if (CollectionUtils.isNotEmpty(sortMap.entrySet())) {
            for (Map.Entry<String, SolrQuery.ORDER> entry : sortMap.entrySet()) {
                solrQuery.addSort(entry.getKey(), entry.getValue());
            }
        }
        solrQuery.setStart(startIndex);
        solrQuery.setRows(pageSize);
        QueryResponse response = null;
        try {
            response = solrServer.query(solrQuery);
            SolrDocumentList documentList = response.getResults();
            DocumentObjectBinder binder = new DocumentObjectBinder();
            System.out.println("docmentList:" + documentList);
            //documentList.toString()
            return new SearchResult<T>(documentList.getNumFound(), binder.getBeans(clazz, documentList));

        } catch (SolrServerException e) {
            logger.error("solr query failed");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * suggestion 处理
     *
     * @param keyword
     * @return
     */
    public List<String> suggestHandler(String keyword) {
        LBHttpSolrServer solrServer = SolrConnection.getInstance().getSlaveServer();
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setRequestHandler("/suggest");
        solrQuery.setQuery(keyword);
        List<String> result = new ArrayList<String>();
        try {
            QueryResponse response = solrServer.query(solrQuery);
            SpellCheckResponse spellCheckResponse = response.getSpellCheckResponse();
            if (spellCheckResponse != null) {
                List<SpellCheckResponse.Suggestion> suggestionList = spellCheckResponse.getSuggestions();
                for (SpellCheckResponse.Suggestion suggestion : suggestionList) {
                    List<String> suggestedWordList = suggestion.getAlternatives();
                    result.addAll(suggestedWordList);
                }
            }
        } catch (SolrServerException e) {
            logger.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * @param clazz
     * @param propertyBean
     * @param sortBean
     * @param startIndex
     * @param pageSize
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> SearchResult<T> multiValueSearch(Class<T> clazz, Object propertyBean,
                                                Object sortBean, Long startIndex, Long pageSize)
            throws Exception {
        LBHttpSolrServer server = SolrConnection.getInstance().getSlaveServer();
        Map<String, String> propertyMap = new TreeMap<String, String>();
        //排序有顺序,使用TreeMap
        Map<String, String> sortMap = new TreeMap<String, String>();
        try {
            propertyMap = SolrjCommonUtil.getSearchProperty(propertyBean);
            sortMap = SolrjCommonUtil.getSearchSort(sortBean);
        } catch (Exception e) {
            e.printStackTrace();
        }
        SolrDocumentList solrDocumentList = solrjQuery.query(server, propertyBean, propertyMap, sortMap,
                startIndex, pageSize);
        DocumentObjectBinder binder = new DocumentObjectBinder();


        return new SearchResult<T>(solrDocumentList.getNumFound(), binder.getBeans(clazz, solrDocumentList));

    }


    /**
     * 添加 JavaEntity Bean，然后来完成添加操作（ps 程序会根据 Bean 中的相关的 Annotation 注解，把对应属性建立到 index 中）
     *
     * @param server
     * @param beans
     * @return
     * @throws Exception
     */
    public UpdateResponse addBean(HttpSolrServer server, Collection<?> beans) throws Exception {
        UpdateResponse response = server.addBeans(beans);
        server.commit();
        return response;
    }

    /**
     * 根据索引id删除对应的索引
     * <p/>
     * solr
     *
     * @param server
     * @param ids
     * @return
     * @throws Exception
     */
    public UpdateResponse remove(HttpSolrServer server, List<String> ids) throws Exception {
        UpdateResponse response = server.deleteById(ids);
        server.commit();
        return response;
    }


    /**
     * 根据删除所有索引
     * <p/>
     * solr
     *
     * @param server
     * @return
     * @throws Exception
     */
    public UpdateResponse removeAll(HttpSolrServer server) throws Exception {
        UpdateResponse response = server.deleteByQuery("*:*");
        server.commit();
        return response;
    }


}
