package com.xorlev.gatekeeper.handler;

import com.xorlev.gatekeeper.events.ClustersUpdatedEvent;

/**
 * 2013-09-18
 *
 * @author Michael Rose <elementation@gmail.com>
 */
public interface ClusterHandler {
    void processClusters(ClustersUpdatedEvent event);
}
