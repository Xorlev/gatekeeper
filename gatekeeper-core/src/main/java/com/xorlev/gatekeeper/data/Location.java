package com.xorlev.gatekeeper.data;

import com.google.common.collect.Maps;
import lombok.Data;
import lombok.NonNull;

import java.io.Serializable;
import java.util.Map;

/**
 * 2013-07-27
 *
 * @author Michael Rose <elementation@gmail.com>
 */
@Data
public class Location implements Serializable {
    @NonNull String context;
    @NonNull Cluster upstream;
    Map<String,String> attributes = Maps.newHashMap();

    public Location(String context, Cluster cluster) {
        this.context = context;
        this.upstream = cluster;
    }
}
