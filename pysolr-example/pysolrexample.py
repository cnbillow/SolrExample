# -*- coding: utf-8 -*-
import pysolr

SOLR_URL = 'http://localhost:8983/solr/posts'


def add():
    """
    添加
    """
    solr = pysolr.Solr(SOLR_URL)
    result = solr.add([
        {
            'id': '10000',
            'post_title': 'test-title',
            'post_name': 'test-name',
            'post_excerpt': 'test-excerpt',
            'post_content': 'test-content',
            'post_date': '2019-06-18 14:56:55',
        }
    ])
    print(result)

def query():
    """
    查询
    """
    solr = pysolr.Solr(SOLR_URL)
    results = solr.search('苹果')
    print(results.docs)


if __name__ == "__main__":
    add()
    query()
