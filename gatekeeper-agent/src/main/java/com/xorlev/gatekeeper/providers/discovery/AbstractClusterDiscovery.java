package com.xorlev.gatekeeper.providers.discovery;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.AbstractIdleService;
import com.xorlev.gatekeeper.data.Cluster;
import com.xorlev.gatekeeper.providers.output.ClusterHandler;
import com.xorlev.gatekeeper.providers.ClustersUpdatedEvent;
import com.xorlev.gatekeeper.providers.discovery.ZookeeperClusterDiscovery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 2013-07-28
 *
 * @author Michael Rose <elementation@gmail.com>
 */
public abstract class AbstractClusterDiscovery extends AbstractIdleService {
    protected static Logger log = LoggerFactory.getLogger(ZookeeperClusterDiscovery.class);
    protected List<ClusterHandler> clusterHandlers = Lists.newArrayList();

    public void registerHandler(ClusterHandler clusterHandler) {
        clusterHandlers.add(clusterHandler);
    }

    public abstract void startUp() throws Exception;

    public abstract void shutDown() throws Exception;

    protected void updateInstances() {
        List<Cluster> clusterList = clusters();

        for (ClusterHandler clusterHandler : clusterHandlers) {
            clusterHandler.processClusters(new ClustersUpdatedEvent(clusterList));
        }
    }

    public abstract List<Cluster> clusters();

    public List<ClusterHandler> handlers() {
        return ImmutableList.copyOf(clusterHandlers);
    }

}
