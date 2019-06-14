package com.charles.solrexample.solr;

import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.impl.LBHttpSolrServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;


public class SolrConnection {
    private static SolrConnection solrConnection = null;
    //String[] solrServers = DynamicProperties.getString("solr.servers").split(",");
    private static String masterUrl = "http://192.168.1.172:8099/solr";
    private static String slaveUrl1 = "http://192.168.1.128:8099/solr";
    private static String slaveUrl2 = "http://192.168.1.173.8099/solr";
    private static HttpSolrServer masterServer = null;
    private static LBHttpSolrServer slaveServer = null;
    private static Logger logger = LoggerFactory.getLogger(SolrConnection.class);


    private SolrConnection() {
    }

    public static SolrConnection getInstance() {
        if (solrConnection == null) {
            synchronized (SolrConnection.class) {
                if (solrConnection == null) {
                    solrConnection = new SolrConnection();
                }
            }
        }
        return solrConnection;
    }

    /**
     * 主机用来创建索引
     *
     * @return
     */
    public HttpSolrServer getMasterServer() {
        if (masterServer == null) {
            masterServer = new HttpSolrServer(masterUrl);
            masterServer.setSoTimeout(10000);  // socket read timeout
            masterServer.setConnectionTimeout(10000);
            masterServer.setDefaultMaxConnectionsPerHost(100);
            masterServer.setMaxTotalConnections(100);
            masterServer.setFollowRedirects(false);  // defaults to false
            masterServer.setAllowCompression(true);
            masterServer.setMaxRetries(1); // defaults to 0.  > 1 not recommended.
        }
        return masterServer;
    }

    /**
     * 从 用来搜索服务
     *
     * @return
     */
    public LBHttpSolrServer getSlaveServer() {
        if (slaveServer == null) {
            try {
                slaveServer = new LBHttpSolrServer(slaveUrl1, slaveUrl2);
                slaveServer.setSoTimeout(10000);  // socket read timeout
                slaveServer.setConnectionTimeout(10000);
            } catch (MalformedURLException e) {
                logger.error("solr server connect failed");
                e.printStackTrace();
            }
        }
        return slaveServer;
    }


}
