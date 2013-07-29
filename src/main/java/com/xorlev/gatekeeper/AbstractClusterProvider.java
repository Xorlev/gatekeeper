package com.xorlev.gatekeeper;

import com.xorlev.gatekeeper.data.Cluster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 2013-07-28
 *
 * @author Michael Rose <michael@fullcontact.com>
 */
public abstract class AbstractClusterProvider {
    protected static Logger log = LoggerFactory.getLogger(ZookeeperClusterProvider.class);
    protected ClusterHandler<Void> clusterHandler;

    public AbstractClusterProvider(ClusterHandler<Void> clusterHandler) {
        this.clusterHandler = clusterHandler;
    }

    protected void updateInstances() {
        List<Cluster> clusterList = provideClusters();

        clusterHandler.processClusters(clusterList);
    }

    protected abstract List<Cluster> provideClusters();
}
