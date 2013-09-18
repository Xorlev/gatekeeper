package com.xorlev.gatekeeper.data;

import com.beust.jcommander.internal.Sets;
import lombok.Data;

import java.util.Set;

/**
 * 2013-07-27
 *
 * @author Michael Rose <elementation@gmail.com>
 */
@Data
public class Cluster {
    String protocol = "http";
    String clusterName;
    Integer port = 80;
    Set<Server> servers = Sets.newHashSet();

    public Cluster(String clusterName) {
        this.clusterName = clusterName;
    }
}
