package com.xorlev.gatekeeper.providers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.AbstractIdleService;
import com.xorlev.gatekeeper.data.Cluster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 2013-07-28
 *
 * @author Michael Rose <elementation@gmail.com>
 */
public abstract class AbstractClusterProvider extends AbstractIdleService {
    protected static Logger log = LoggerFactory.getLogger(ZookeeperClusterProvider.class);
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
