package com.xorlev.gatekeeper.events;

import com.google.common.collect.Lists;
import com.xorlev.gatekeeper.data.Cluster;
import com.xorlev.gatekeeper.data.Location;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 2013-07-27
 *
 * @author Michael Rose <elementation@gmail.com>
 */
@Data
public class ConfigChangedEvent implements Serializable {
    List<Cluster> clusters = Lists.newArrayList();
    List<Location> locations = Lists.newArrayList();

    public ConfigChangedEvent(Cluster cluster) {
        this.clusters.add(cluster);
    }

    public ConfigChangedEvent(List<Cluster> clusters) {
        this.clusters.addAll(clusters);
    }
}
