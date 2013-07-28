package com.xorlev.gatekeeper.data;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

/**
 * 2013-07-27
 *
 * @author Michael Rose <michael@fullcontact.com>
 */
@Data
public class ConfigContext {
    List<Cluster> clusters = Lists.newArrayList();
    List<Location> locations = Lists.newArrayList();

    public ConfigContext(Cluster cluster) {
        this.clusters.add(cluster);
    }

    public ConfigContext(List<Cluster> clusters) {
        this.clusters.addAll(clusters);
    }
}
