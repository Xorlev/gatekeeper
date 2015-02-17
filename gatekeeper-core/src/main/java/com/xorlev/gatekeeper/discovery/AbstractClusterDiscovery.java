package com.xorlev.gatekeeper.discovery;

import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.AbstractIdleService;
import com.google.inject.Inject;
import com.xorlev.gatekeeper.data.Cluster;
import com.xorlev.gatekeeper.events.ClustersUpdatedEvent;
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
    protected List<Cluster> previousClusterList = Collections.emptyList();
    @Inject
    protected EventBus bus;

    public abstract void startUp() throws Exception;

    public abstract void shutDown() throws Exception;

    @Managed(description = "Rewrite NGINX configuration")
    public void forceUpdate() {
        updateInstances(true);
    }

    protected void updateInstances(boolean force) {
        List<Cluster> clusterList = getClusters();

        if (force || previousClusterList != clusterList) {
            bus.post(new ClustersUpdatedEvent(clusterList));
        }
    }

    @Managed
    public abstract List<Cluster> getClusters();
}
