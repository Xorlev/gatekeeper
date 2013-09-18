package com.xorlev.gatekeeper.providers;

/**
 * 2013-09-18
 *
 * @author Michael Rose <michael@fullcontact.com>
 */
public interface ClusterHandler {
    void processClusters(ClustersUpdatedEvent event);
}
