package com.charles.solrexample.solr;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.queryparser.classic.QueryParser;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


public class SolrjCommonUtil {

    /**
     * 把搜索的条件封装到实体对象中，对象通过此方法解析为<K,V>结构的Map
     * 对象的解析是利用反射原理，将实体对象中不为空的值，以映射的方式，转化为一个Map，其中排序对象在转化的过程中，使用TreeMap，保证其顺序性。
     *
     * @param model
     * @return
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws java.lang.reflect.InvocationTargetException
     *
     */
    public static Map<String, String> getSearchProperty(Object model)
            throws NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        Map<String, String> resultMap = new TreeMap<String, String>();

        try {
            // 获取实体类的所有属性，返回Field数组
            Field[] field = model.getClass().getDeclaredFields();
            for (int i = 0; i < field.length; i++) { // 遍历所有属性
                String name = field[i].getName(); // 获取属性的名字
                // 获取属性的类型
                String type = field[i].getGenericType().toString();
                //String 类型
                if (type.equals("class java.lang.String")) { // 如果type是类类型，则前面包含"class "，后面跟类名
                    Method m = model.getClass().getMethod(
                            "get" + UpperCaseField(name));
                    String value = (String) m.invoke(model); // 调用getter方法获取属性值
                    if (StringUtils.isNotBlank(value)) {
                        if (!name.endsWith("Time") && !name.equalsIgnoreCase("age")) {    //表示进行范围查询
                            value = QueryParser.escape(value);
                        }
                        resultMap.put(name, value);
                    }
                }
                //int 类型
                if (type.equals("int")) {
                    Method m = model.getClass().getMethod(
                            "get" + UpperCaseField(name));
                    Integer value = (Integer) m.invoke(model); // 调用getter方法获取属性值
                    if (value != null && value > 0) {
                        resultMap.put(name, value.toString());
                    }
                }
                //SET类型
                if (type.equals("java.util.Set<java.lang.Integer>")) {
                    Method m = model.getClass().getMethod(
                            "get" + UpperCaseField(name));
                    Set<Integer> values = (Set<Integer>) m.invoke(model); // 调用getter方法获取属性值
                    StringBuilder sb = new StringBuilder();
                    boolean first = true;
                    if (CollectionUtils.isNotEmpty(values)) {
                        for (Integer val : values) {
                            if (!first) {
                                sb.append(",");
                            }
                            sb.append(val);
                            first = false;
                        }
                        String value = sb.toString();
                        if (StringUtils.isNotBlank(value)) {
                            resultMap.put(name, value);
                        }
                    }
                }
                if (type.equals("java.util.Set<java.lang.String>")) {
                    Method m = model.getClass().getMethod(
                            "get" + UpperCaseField(name));
                    Set<String> values = (Set<String>) m.invoke(model); // 调用getter方法获取属性值
                    if (CollectionUtils.isNotEmpty(values)) {
                        StringBuilder sb = new StringBuilder();
                        boolean first = true;
                        for (String val : values) {
                            if (!first) {
                                sb.append(",");
                            }
                            sb.append(val);
                            first = false;
                        }
                        String value = sb.toString();
                        if (StringUtils.isNotBlank(value)) {
                            resultMap.put(name, value);
                        }
                    }
                }
            }
            //如果MAP为空,设置为搜索全部的条件
            if (resultMap.size() == 0) {
                resultMap.put("*", "*");
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return resultMap;
    }

    /**
     * 对象通过此方法解析为<K,V>结构的Map,如果Map为空，不设置为搜索全部的条件
     *
     * @param model
     * @return
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws java.lang.reflect.InvocationTargetException
     *
     */

    public static Map<String, String> getSearchSort(Object model)
            throws NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        Map<String, String> resultMap = new TreeMap<String, String>();
        if (model == null) {
            return null;
        }
        // 获取实体类的所有属性，返回Field数组
        Field[] field = model.getClass().getDeclaredFields();
        for (int i = 0; i < field.length; i++) { // 遍历所有属性
            String name = field[i].getName(); // 获取属性的名字
            // 获取属性的类型
            String type = field[i].getGenericType().toString();
            //String 类型
            if (type.equals("class java.lang.String")) { // 如果type是类类型，则前面包含"class "，后面跟类名
                Method m = model.getClass().getMethod(
                        "get" + UpperCaseField(name));
                String value = (String) m.invoke(model); // 调用getter方法获取属性值
                if (value != null) {
                    if (!name.endsWith("Time")) {
                        value = QueryParser.escape(value);
                    }
                    resultMap.put(name, value);
                }
            }
            //int 类型
            if (type.equals("int")) {
                Method m = model.getClass().getMethod(
                        "get" + UpperCaseField(name));
                Integer value = (Integer) m.invoke(model); // 调用getter方法获取属性值
                if (value != null && value > 0) {
                    resultMap.put(name, value.toString());
                }
            }
        }
        return resultMap;
    }

    /**
     * 转化字段首字母为大写
     *
     * @param fieldName
     * @return
     */
    private static String UpperCaseField(String fieldName) {
        fieldName = fieldName.replaceFirst(fieldName.substring(0, 1), fieldName
                .substring(0, 1).toUpperCase());
        return fieldName;
    }
}
