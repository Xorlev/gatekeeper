package com.xorlev.gatekeeper;

import com.google.common.collect.Lists;
import com.netflix.curator.x.discovery.ServiceCache;
import com.netflix.curator.x.discovery.ServiceInstance;
import com.xorlev.gatekeeper.data.Cluster;
import com.xorlev.gatekeeper.data.ConfigContext;
import com.xorlev.gatekeeper.data.Location;
import com.xorlev.gatekeeper.data.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class ClusterHandler<T> {
    private final ConfigWriter configWriter = new ConfigWriter();
    private static final Logger log = LoggerFactory.getLogger(ClusterHandler.class);

    public ClusterHandler() {
    }

    void processClusters(List<Cluster> clusterList) {

        writeConfig(clusterList);
    }

    protected Server convertInstance(ServiceInstance<T> instance) {
        Integer port = instance.getSslPort() != null ? instance.getSslPort() : instance.getPort();
        return new Server(instance.getAddress(), port);
    }

    private void writeConfig(List<Cluster> clusters) {
        ConfigContext configContext = new ConfigContext(clusters);

        for (Cluster cluster: clusters) {
            for (String context : AppConfig.getStringList("cluster."+cluster.getClusterName()+".context")) {
                configContext.getLocations().add(new Location(context, cluster));
            }
        }
        try {
            configWriter.writeConfig(configContext);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}