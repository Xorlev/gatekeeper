package com.xorlev.gatekeeper.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.xorlev.gatekeeper.data.Cluster;
import com.xorlev.gatekeeper.data.Location;
import lombok.Data;
import lombok.Value;

import java.io.Serializable;
import java.util.List;

/**
 * Routing Configuration for specifying config
 *
 * @author Michael Rose <elementation@gmail.com>
 */
@Value
public class RoutingConfiguration {
    private final ImmutableList<Cluster> clusters;
    private final ImmutableList<Location> locations;

    public RoutingConfiguration(List<Cluster> clusters, List<Location> locations) {
        this.clusters = ImmutableList.copyOf(clusters);
        this.locations = ImmutableList.copyOf(locations);
    }
}
