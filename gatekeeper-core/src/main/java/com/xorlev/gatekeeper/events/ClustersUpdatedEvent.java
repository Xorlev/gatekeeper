package com.xorlev.gatekeeper.events;

import com.google.common.collect.ImmutableList;
import com.xorlev.gatekeeper.data.Cluster;

import java.util.List;

/**
 * 2013-09-18
 *
 * @author Michael Rose <elementation@gmail.com>
 */
public class ClustersUpdatedEvent {
    private List<Cluster> clusters;

    public ClustersUpdatedEvent(List<Cluster> clusters) {
        this.clusters = ImmutableList.copyOf(clusters);
    }

    public List<Cluster> getClusters() {
        return clusters;
    }
}
