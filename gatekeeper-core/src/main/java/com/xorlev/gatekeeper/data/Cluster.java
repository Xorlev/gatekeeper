package com.xorlev.gatekeeper.data;

import com.google.common.collect.Sets;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * 2013-07-27
 *
 * @author Michael Rose <elementation@gmail.com>
 */
@Data
public class Cluster implements Serializable {
    String protocol = "http";
    String clusterName;
    Integer port = 80;
    Set<Server> servers = Sets.newHashSet();

    public Cluster(String clusterName) {
        this.clusterName = clusterName;
    }

    public Set<Server> getServers() {
        return servers;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getProtocol() {
        return protocol;
    }

    public Integer getPort() {
        return port;
    }
}
