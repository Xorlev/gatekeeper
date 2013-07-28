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
    private final Collection<ServiceCache<T>> serviceCaches;
    private final ConfigWriter configWriter = new ConfigWriter();
    private static final Logger log = LoggerFactory.getLogger(ClusterHandler.class);

    public ClusterHandler(Collection<ServiceCache<T>> clusters) {
        this.serviceCaches = clusters;
    }

    void processClusters() {
        List<Cluster> clusters = Lists.newArrayListWithExpectedSize(serviceCaches.size());
        for (ServiceCache<T> serviceCache : serviceCaches) {
            List<ServiceInstance<T>> instances = serviceCache.getInstances();

            log.debug("Processing {}", instances);

            if (!instances.isEmpty()) {
                Cluster cluster = new Cluster(instances.get(0).getName());

                if (instances.get(0).getSslPort() != null) {
                    cluster.setProtocol("https");
                }

                for (ServiceInstance<T> instance : instances) {
                    cluster.getServers().add(convertInstance(instance));
                }

                clusters.add(cluster);
            }

        }

        writeConfig(clusters);
    }

    protected Server convertInstance(ServiceInstance<T> instance) {
        Integer port = instance.getSslPort() != null ? instance.getSslPort() : instance.getPort();
        return new Server(instance.getAddress(), port);
    }

    private void writeConfig(List<Cluster> clusters) {
        ConfigContext configContext = new ConfigContext(clusters);

        for (Cluster cluster: clusters) {
            configContext.getLocations().add(new Location("/", cluster));
        }
        try {
            configWriter.writeConfig(configContext);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}