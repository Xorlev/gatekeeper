package com.xorlev.gatekeeper.events;

import com.google.common.collect.ImmutableList;
import com.xorlev.gatekeeper.data.Cluster;
import lombok.Data;
import lombok.Value;

import java.util.List;

/**
 * Event emitted on cluster updates
 *
 * @author Michael Rose <elementation@gmail.com>
 */
@Value
public class ClustersUpdatedEvent {
    private ImmutableList<Cluster> clusters;

    public ClustersUpdatedEvent(List<Cluster> clusters) {
        this.clusters = ImmutableList.copyOf(clusters);
    }
}
