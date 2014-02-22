package com.xorlev.gatekeeper.providers.output;

import com.xorlev.gatekeeper.providers.discovery.ClustersUpdatedEvent;

/**
 * 2013-09-18
 *
 * @author Michael Rose <elementation@gmail.com>
 */
public interface ClusterHandler {
    void processClusters(ClustersUpdatedEvent event);
}
