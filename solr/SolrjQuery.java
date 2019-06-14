package com.charles.solrexample.solr;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.LBHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;


public class SolrjQuery {
    /**
     * 静态常量 *
     */
    private static final String ASC = "asc";
    private static final String AND = " AND ";
    private static final String OR = " OR ";


    /**
     * 搜索直接调用solr客户端solrj，基本逻辑为循环两个解析之后的TreeMap，设置到SolrQuery当中，最后直接调用solrj的API，获得搜索结果。
     *
     * @param server
     * @param propertyMap
     * @param sortMap
     * @param startIndex
     * @param pageSize
     * @return
     * @throws Exception
     */
    public SolrDocumentList query(LBHttpSolrServer server, Object propertyBean, Map<String, String> propertyMap,
                                  Map<String, String> sortMap, Long startIndex, Long pageSize)
            throws Exception {
        SolrQuery query = new SolrQuery();
        // 设置搜索字段
        if (null == propertyMap) {
            throw new Exception("搜索字段不可为空!");
        } else {
            boolean firstFlag = true;
            String queryString = "";
            //组合OR条件查询
            queryString = composeOrStr(propertyBean, propertyMap, queryString);
            //组合单个域，多个值，采用OR隔开情况
            queryString = composeOneFiledOrStr(propertyBean, propertyMap, queryString);
            for (Object o : propertyMap.keySet()) {
                StringBuffer sb = new StringBuffer();
                if (firstFlag) {
                    firstFlag = false;
                } else {
                    sb.append(AND);
                }

                sb.append(o.toString()).append(":");
                sb.append(propertyMap.get(o));
                queryString = queryString + addBlank2Expression(sb.toString());
            }
            query.setQuery(queryString);
        }
        // 设置排序条件
        if (null != sortMap) {
            for (Object co : sortMap.keySet()) {
                if (ASC == sortMap.get(co)
                        || ASC.equals(sortMap.get(co))) {
                    query.addSort(co.toString(), SolrQuery.ORDER.asc);
                } else {
                    query.addSort(co.toString(), SolrQuery.ORDER.desc);

                }
            }
        }

        if (null != startIndex) {
            query.setStart(Integer.parseInt(String.valueOf(startIndex)));
        }
        if (null != pageSize && 0L != pageSize.longValue()) {
            query.setRows(Integer.parseInt(String.valueOf(pageSize)));
        }
        try {
            QueryResponse qrsp = server.query(query);
            SolrDocumentList docs = qrsp.getResults();
            return docs;
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    private String composeOrStr(Object propertyBean, Map<String, String> propertyMap, String queryString) {
        //如果加注解了，清除，相同则用or连接
        Field[] fields = propertyBean.getClass().getDeclaredFields();
        Map<Integer, String> strMap = new HashMap<Integer, String>();
        for (Field field : fields) {
            boolean hasAnnotation = field.isAnnotationPresent(OrRelation.class);
            if (hasAnnotation) {
                //先从map中排除
                String key = field.getName();
                OrRelation orRelation = field.getAnnotation(OrRelation.class);
                //处理每个注解值，相同的使用（name:1 OR alias:3）格式
                Integer id = orRelation.value();
                //值不能为null
                if(propertyMap.get(key) != null ){
                    if (strMap.containsKey(id)) {
                        String str = strMap.get(id);
                        str += new StringBuilder().append(OR).
                                append(key).append(":").append(propertyMap.get(key)).toString();
                        strMap.put(id, str);
                    } else {
                        strMap.put(id, new StringBuilder().append('(')
                                .append(key).append(":").append(propertyMap.get(key)).toString());
                    }
                }
                //从map中移除or条件的
                propertyMap.remove(key);
            }
        }
        //组合所有OR条件
        for(Map.Entry entry : strMap.entrySet()){
            queryString += new StringBuilder().append(entry.getValue()).append(')').append(AND);
        }
        if(propertyMap.isEmpty() && queryString.lastIndexOf(AND) > 0){
            queryString = queryString.substring(0, queryString.lastIndexOf(AND));
        }
        return queryString;
    }

    private String composeOneFiledOrStr(Object propertyBean, Map<String, String> propertyMap, String queryString) {
        String old = queryString;
        //如果加注解了，清除，相同则用or连接
        Field[] fields = propertyBean.getClass().getDeclaredFields();
        for (Field field : fields) {
            boolean hasAnnotation = field.isAnnotationPresent(MutiValue.class);
            if (hasAnnotation) {
                //先从map中排除
                String key = field.getName();
                //值不能为null
                String val = propertyMap.get(key);
                if(val != null ){
                    String[] v = val.split(",");
                    StringBuilder sb = new StringBuilder("(");
                    for(String value : v){
                        if(StringUtils.isNotBlank(value)){
                            sb.append(key).append(':').append(value).append(OR);
                        }
                    }
                    queryString +=  sb.substring(0,sb.lastIndexOf(OR));
                    queryString += ")"+AND;
                }
                //从map中移除or条件的
                propertyMap.remove(key);
            }
        }
        //查询串有更新!old.equals(queryString)，否则没更新，会删掉部分串，导致出错
        if(propertyMap.isEmpty() && !old.equals(queryString) && queryString.lastIndexOf(AND) > 0){
            queryString = queryString.substring(0, queryString.lastIndexOf(AND));
        }
        return queryString;
    }

    /**
     * 规范查询的连接
     *
     * @param oldExpression
     * @return
     */
    private String addBlank2Expression(String oldExpression) {
        String lastExpression;
        lastExpression = oldExpression.replace("AND", " AND ").replace("NOT",
                " NOT ").replace("OR", " OR ");
        return lastExpression;
    }




}
