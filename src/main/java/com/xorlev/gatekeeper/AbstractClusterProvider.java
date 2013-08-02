package com.xorlev.gatekeeper;

import com.xorlev.gatekeeper.data.Cluster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 2013-07-28
 *
 * @author Michael Rose <elementation@gmail.com>
 */
public abstract class AbstractClusterProvider {
    protected static Logger log = LoggerFactory.getLogger(ZookeeperClusterProvider.class);
    protected ClusterHandler clusterHandler;

    public AbstractClusterProvider(ClusterHandler clusterHandler) {
        this.clusterHandler = clusterHandler;
    }

    public abstract void start() throws Exception;

    public abstract void stop() throws Exception;

    protected void updateInstances() {
        List<Cluster> clusterList = provideClusters();

        clusterHandler.processClusters(clusterList);
    }

    protected abstract List<Cluster> provideClusters();
}
