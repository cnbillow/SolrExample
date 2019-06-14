using SolrNet.Attributes;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SolrNetExample.Web.Document
{
    public class PostDoc
    {
        [SolrUniqueKey("id")]
        public int Id { get; set; }

        [SolrField("title")]
        public string Title { get; set; }

        [SolrField("excerpt")]
        public DateTime Excerpt { get; set; }

        [SolrField("content")]
        public string Content { get; set; }

        [SolrField("updatetime")]
        public DateTime UpdateTime { get; set; }

    }
}
