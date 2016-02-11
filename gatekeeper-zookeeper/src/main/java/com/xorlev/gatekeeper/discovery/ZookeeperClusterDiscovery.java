package com.xorlev.gatekeeper.discovery;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.xorlev.gatekeeper.AppConfig;
import com.xorlev.gatekeeper.data.Cluster;
import com.xorlev.gatekeeper.data.Server;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.BoundedExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceCache;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.ServiceCacheListener;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Implements push-based service discovery with Zookeeper.
 *
 * @author Michael Rose <elementation@gmail.com>
 */
public class ZookeeperClusterDiscovery extends AbstractClusterDiscovery {
    private CuratorFramework zk;
    private ServiceDiscovery<?> dsc;

    private List<ServiceCache<?>> serviceCacheList = Lists.newArrayList();

    private ExecutorService executorService = Executors.newCachedThreadPool();

    @Override
    public void startUp() throws Exception {
        setupZookeeper();
        setupServiceDiscovery();
        updateInstances(false);
    }

    @Override
    public void shutDown() throws Exception {
        log.info("Shutting down...");

        executorService.shutdown();
        if (dsc != null) dsc.close();
        if (zk != null) zk.close();
    }

    private void setupZookeeper() {
        String quorum = AppConfig.getString("zookeeper.quorum");
        String namespace = AppConfig.getString("zookeeper.namespace");
        log.info("Starting Zookeeper with connectString={}", quorum);
        zk = CuratorFrameworkFactory.builder()
                .connectString(quorum)
                .connectionTimeoutMs(2000)
                .retryPolicy(new BoundedExponentialBackoffRetry(100, 10000, 100))
                .namespace(namespace.isEmpty() ? null : namespace)
                .build();

        zk.start();
    }

    private void setupServiceDiscovery() throws Exception {
        if (zk != null && zk.getState() == CuratorFrameworkState.STARTED) {

            dsc = ServiceDiscoveryBuilder.builder(Void.class)
                    .basePath(AppConfig.getString("zookeeper.discoveryPath"))
                    .client(zk)
                    .build();

            dsc.start();

            initializeServiceCaches();


            // If clusters change, re-init our service caches and config.
            AppConfig.addCallback("clusters", new ReInitRunnable());
        }
    }

    private void initializeServiceCaches() throws Exception {
        // Close any caches (if exists)
        for (ServiceCache<?> cache : serviceCacheList) {
            cache.close();
        }
        serviceCacheList = Lists.newArrayList();

        // Grab each cluster, build a service cache, and add listeners to update config file
        for (final String c : AppConfig.getStringList("clusters")) {
            ServiceCache<?> cache = dsc.serviceCacheBuilder().name(c).build();

            // Whenever a cluster is modified, notify handlers
            cache.addListener(new ServiceCacheListener() {
                public void cacheChanged() {
                    log.info("Service {} modified, rewriting config", c);
                    updateInstances(false);
                }

                public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {
                }
            }, executorService);

            cache.start();
            serviceCacheList.add(cache);

            // If context changes, rebuild config
            // TODO move cluster config to its own package & producer
            AppConfig.addCallback("cluster." + c + ".context", new ReInitRunnable());
        }
    }

    @Override
    public List<Cluster> getClusters() {
        List<Cluster> clusterList = Lists.newArrayListWithExpectedSize(serviceCacheList.size());
        for (ServiceCache<?> cache : serviceCacheList) {
            if (!cache.getInstances().isEmpty()) {
                Cluster cluster = clusterFromInstances(cache.getInstances());

                for (ServiceInstance<?> instance : cache.getInstances()) {
                    cluster.getServers().add(convertInstance(instance));
                }

                log.info("Discovery: cluster=[{}] has {} instances, {}...",
                        cluster.getClusterName(), cluster.getServers().size(), Iterables.limit(cluster.getServers(), 5));
                clusterList.add(cluster);
            }
        }
        return clusterList;
    }

    private <T> Cluster clusterFromInstances(List<ServiceInstance<T>> instances) {
        ImmutableList.Builder<Server> servers = ImmutableList.builder();
        for (ServiceInstance<?> instance : instances) {
            servers.add(convertInstance(instance));
        }

        ServiceInstance<?> instance = instances.get(0);

        // TODO 2016-02-10 -- support new Curator fields, e.g. ServiceType
        return Cluster
            .builder()
            .clusterName(instance.getName())
            .protocol(instance.getSslPort() != null ? "https" : "http")
            .port(instance.getPort())
            .servers(servers.build())
            .build();
    }

    protected Server convertInstance(ServiceInstance<?> instance) {
        Integer port = instance.getSslPort() != null ? instance.getSslPort() : instance.getPort();
        return new Server(instance.getAddress(), port);
    }

    private class ReInitRunnable implements Runnable {
        public void run() {
            try {
                updateInstances(false);
            } catch (Exception e) {
                log.error("Error re-initializing with new configuration, {}", e.getMessage(), e);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        CuratorFramework zk = CuratorFrameworkFactory.builder()
                                    .connectString("127.0.0.1")
                                    .connectionTimeoutMs(2000)
                                    .retryPolicy(new BoundedExponentialBackoffRetry(100, 10000, 100))
                                    .namespace("discovery")
                                    .build();

        zk.start();

        ServiceDiscovery<Void> dsc = ServiceDiscoveryBuilder.builder(Void.class)
                                     .basePath("turbine-aggregate")
                                     .client(zk)
                                     .build();

        dsc.start();

        dsc.registerService(ServiceInstance.<Void>builder().id("a").name("person-service-prod")
                                           .address
            ("128.0.0.1").port
            (6667).build());
    }
}
