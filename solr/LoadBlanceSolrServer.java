package com.charles.solrexample.solr;

import org.apache.solr.client.solrj.impl.LBHttpSolrServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;

public class LoadBlanceSolrServer {

    private static LBHttpSolrServer lbHttpSolrServer;

    private static Logger logger = LoggerFactory.getLogger(LoadBlanceSolrServer.class);

    static {
        String[] solrServers = DynamicProperties.getString("solr.servers").split(",");
        try {
            lbHttpSolrServer = createSolrServer(solrServers);
        } catch (MalformedURLException e) {
            logger.error("LoadBlanceSolrServer create error!!!");
            e.printStackTrace();
        }
    }

    private static LBHttpSolrServer createSolrServer(String[] serverUrls) throws MalformedURLException {
        if (serverUrls != null && serverUrls.length > 0) {
            LBHttpSolrServer solr = new LBHttpSolrServer(serverUrls);
            return solr;
        }
        return null;
    }
}
