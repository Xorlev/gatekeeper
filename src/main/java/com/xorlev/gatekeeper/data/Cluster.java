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
public class Cluster {
    String protocol = "http";
    String clusterName;
    Integer port = 80;
    List<Server> servers = Lists.newArrayList();

    public Cluster(String clusterName) {
        this.clusterName = clusterName;
    }
}
