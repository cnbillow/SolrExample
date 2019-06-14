using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using CommonServiceLocator;
using Microsoft.AspNetCore.Mvc;
using SolrNet;
using SolrNetExample.Web.Models;

namespace SolrNetExample.Web.Controllers
{
    public class SearchController : Controller
    {
        public IActionResult Index()
        {
            return View();
        }

        /// <summary>
        /// 增加索引。
        /// </summary>
        /// <returns></returns>
        public IActionResult Add()
        {
            ISolrOperations<PostModel> solr = ServiceLocator.Current.GetInstance<ISolrOperations<PostModel>>();
            return View();
        }

        /// <summary>
        /// 删除索引。
        /// </summary>
        /// <returns></returns>
        public IActionResult Delete()
        {
            return View();
        }

        /// <summary>
        /// 修改索引。
        /// </summary>
        /// <returns></returns>
        public IActionResult Update()
        {
            return View();
        }

        /// <summary>
        /// 查询索引。
        /// </summary>
        /// <returns></returns>
        public IActionResult Query()
        {
            return View();
        }
    }
}