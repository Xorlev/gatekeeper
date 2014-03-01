package com.xorlev.gatekeeper.discovery;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.AbstractIdleService;
import com.xorlev.gatekeeper.data.Cluster;
import com.xorlev.gatekeeper.handler.ClusterHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.weakref.jmx.Managed;

import java.util.Collections;
import java.util.List;

/**
 * 2013-07-28
 *
 * @author Michael Rose <elementation@gmail.com>
 */
public abstract class AbstractClusterDiscovery extends AbstractIdleService {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    protected List<ClusterHandler> clusterHandlers = Lists.newArrayList();
    protected List<Cluster> previousClusterList = Collections.emptyList();

    public void registerHandler(ClusterHandler clusterHandler) {
        clusterHandlers.add(clusterHandler);
    }

    public abstract void startUp() throws Exception;

    public abstract void shutDown() throws Exception;

    @Managed(description = "Rewrite NGINX configuration")
    public void forceUpdate() {
        updateInstances(true);
    }

    protected void updateInstances(boolean force) {
        List<Cluster> clusterList = getClusters();

        if (force || previousClusterList != clusterList) {
            for (ClusterHandler clusterHandler : clusterHandlers) {
                clusterHandler.processClusters(new ClustersUpdatedEvent(clusterList));
            }
        }
    }

    @Managed
    public abstract List<Cluster> getClusters();

    @Managed
    public List<ClusterHandler> getHandlers() {
        return ImmutableList.copyOf(clusterHandlers);
    }

}
