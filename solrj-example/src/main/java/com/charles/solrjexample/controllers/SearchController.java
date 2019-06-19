package com.charles.solrjexample.controllers;

import com.charles.solrjexample.common.PageList;
import com.charles.solrjexample.common.Result;
import com.charles.solrjexample.common.ResultCodeEnum;
import com.charles.solrjexample.domain.Post;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.MapSolrParams;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("search")
public class SearchController {

    private final String solrUrl = "http://localhost:8983/solr";
    private final SolrClient client = getSolrClient();

    /**
     * 添加和更新
     */
    @GetMapping("/add")
    public Result add() throws IOException, SolrServerException {
        Result result = new Result();

        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("id", "10000");
        doc.addField("post_title", "test-title");
        doc.addField("post_name", "test-name");
        doc.addField("post_excerpt", "test-excerpt");
        doc.addField("post_content", "test-content");
        doc.addField("post_date", "2019-06-18 14:56:55");
        client.add("posts", doc);

        Post post = new Post();
        post.setId(10001);
        post.setPost_title("test-title-10001");
        post.setPost_name("test-name");
        post.setPost_excerpt("test-excerpt");
        post.setPost_content("test-content");
        post.setPost_date(new Date());
        client.addBean("posts", post);

        UpdateResponse updateResponse = client.commit("posts");
        int status = updateResponse.getStatus();
        if (status != 0) {
            result.setMsg("添加或更新索引失败！");
            result.setCode(ResultCodeEnum.ERROR.getCode());
        }
        return result;
    }

    /**
     * 删除
     */
    @GetMapping("/delete")
    public Result delete() throws IOException, SolrServerException {
        Result result = new Result();
        // 通过查询条件删除
        client.deleteByQuery("posts", "id:10000");
        // 通过 id 删除
        client.deleteById("posts", "10001");
        UpdateResponse updateResponse = client.commit("posts");
        int status = updateResponse.getStatus();
        if (status != 0) {
            result.setMsg("添加或更新索引失败！");
            result.setCode(ResultCodeEnum.ERROR.getCode());
        }
        return result;
    }

    /**
     * 查询
     */
    @GetMapping("/query")
    public <T> PageList<T> query(String keyword, Integer pageIndex, Integer pageSize) throws IOException, SolrServerException {
        if (keyword == null) {
            keyword = "";
        }
        if (pageIndex == null) {
            pageIndex = 0;
        }
        if (pageSize == null) {
            pageSize = 10;
        }
        PageList<T> pageList = new PageList<T>();
        Map<String, String> queryParamMap = new HashMap<String, String>();
        queryParamMap.put("q", "*:*");
        queryParamMap.put("fq", keyword);
        queryParamMap.put("start", pageIndex.toString());
        queryParamMap.put("rows", pageSize.toString());
//        SolrQuery query = new SolrQuery("*:*");
        MapSolrParams queryParams = new MapSolrParams(queryParamMap);
        QueryResponse queryResponse = client.query("posts", queryParams);
        SolrDocumentList results = queryResponse.getResults();
        pageList.setPageIndex((int) results.getStart());
        pageList.setPageSize(results.size());
        pageList.setCount((int) results.getNumFound());
        pageList.setList((List<T>) results);
        return pageList;
    }

    /**
     * 获取 Solr Client。
     *
     * @return
     */
    private SolrClient getSolrClient() {
        return new HttpSolrClient.Builder(solrUrl).build();
    }
}
