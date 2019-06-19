﻿using System;
using System.Configuration;
using System.IO;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading.Tasks;
using System.Web;
using CommonServiceLocator;
using Microsoft.AspNetCore.Mvc;
using SolrNet;
using SolrNet.Commands.Parameters;
using SolrNet.Exceptions;
using SolrNet.Impl;
using SolrNetExample.Web.Common;
using SolrNetExample.Web.Document;

namespace SolrNetExample.Web.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class SearchController : ControllerBase
    {

        private ISolrOperations<PostDoc> solr;


        public SearchController()
        {
            solr = ServiceLocator.Current.GetInstance<ISolrOperations<PostDoc>>();
        }

        /// <summary>
        /// 增加索引。
        /// </summary>
        /// <returns></returns>
        [HttpGet("add")]
        public async Task<ResponseResult> Add()
        {
            // 同步添加文档
            solr.Add(
                new PostDoc()
                {
                    Id = 30001,
                    Name = "This SolrNet Name",
                    Title = "This SolrNet Title",
                    Excerpt = "This SolrNet Excerpt",
                    Content = "This SolrNet Content 30001",
                    PostDate = DateTime.Now
                }
            );
            // 异步添加文档
            await solr.AddAsync(
                new PostDoc()
                {
                    Id = 30002,
                    Name = "This SolrNet Name",
                    Title = "This SolrNet Title",
                    Excerpt = "This SolrNet Excerpt",
                    Content = "This SolrNet Content 30002",
                    PostDate = DateTime.Now
                }
            );

            ResponseHeader responseHeader = await solr.CommitAsync();
            ResponseResult response = new ResponseResult();
            if (responseHeader.Status == 0)
            {
                response.Status = ResponseStatus.SUCCEED;
            }
            return response;
        }


        /// <summary>
        /// 删除索引。
        /// </summary>
        /// <returns></returns>
        public async Task<ResponseResult> Delete()
        {
            // 使用文档 Id 删除
            await solr.DeleteAsync("300001");
            // 直接删除文档
            await solr.DeleteAsync(new PostDoc()
            {
                Id = 30002,
                Name = "This SolrNet Name",
                Title = "This SolrNet Title",
                Excerpt = "This SolrNet Excerpt",
                Content = "This SolrNet Content 30002",
                PostDate = DateTime.Now
            });
            // 提交
            ResponseHeader responseHeader = await solr.CommitAsync();
            ResponseResult response = new ResponseResult();
            if (responseHeader.Status == 0)
            {
                response.Status = ResponseStatus.SUCCEED;
            }
            return response;
        }

        /// <summary>
        /// 查询索引。
        /// </summary>
        /// <returns></returns>
        [HttpGet("query")]
        public async Task<ResponseResult> Query()
        {
            // 直接传入查询条件
            SolrQueryResults<PostDoc> postDocs = solr.Query("id:30000");

            SolrQuery solrQuery = new SolrQuery("苹果");

            QueryOptions queryOptions = new QueryOptions();
            //queryOptions.AddFields("苹果", "手机");
            queryOptions.Start = 0;
            queryOptions.Rows = 10;
            SolrQueryResults<PostDoc> docs = await solr.QueryAsync(solrQuery, queryOptions);
            ResponseResult<SolrQueryResults<PostDoc>> response = new ResponseResult<SolrQueryResults<PostDoc>>(ResponseStatus.SUCCEED, string.Empty, docs);
            //if (responseHeader.Status == 0)
            //{
            //    response.Status = ResponseStatus.SUCCEED;
            //}
            return response;
        }
    }
}