package com.xorlev.gatekeeper;

import com.google.common.collect.Lists;
import com.netflix.curator.framework.CuratorFramework;
import com.netflix.curator.framework.CuratorFrameworkFactory;
import com.netflix.curator.framework.imps.CuratorFrameworkState;
import com.netflix.curator.framework.state.ConnectionState;
import com.netflix.curator.retry.RetryNTimes;
import com.netflix.curator.x.discovery.ServiceCache;
import com.netflix.curator.x.discovery.ServiceDiscovery;
import com.netflix.curator.x.discovery.ServiceDiscoveryBuilder;
import com.netflix.curator.x.discovery.ServiceInstance;
import com.netflix.curator.x.discovery.details.ServiceCacheListener;
import com.xorlev.gatekeeper.data.Cluster;
import com.xorlev.gatekeeper.data.Server;
import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 2013-07-27
 *
 * @author Michael Rose <michael@fullcontact.com>
 */
public class ZookeeperClusterProvider extends AbstractClusterProvider {
    private CuratorFramework zk;

    private ServiceDiscovery<Void> dsc;
    private List<ServiceCache<Void>> serviceCacheList = Lists.newArrayList();

    private transient boolean run = true;
    private ExecutorService executorService = Executors.newCachedThreadPool();

    public ZookeeperClusterProvider(ClusterHandler clusterHandler) {
        super(clusterHandler);
    }

    @Override
    public void start() throws Exception {
        Signal.handle(new Signal("TERM"), new SignalHandler() {
            public void handle(Signal signal) {
                try {
                    stop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        setupZookeeper();
        setupServiceDiscovery();
        updateInstances();

        while (run) {
            Thread.sleep(5000);
        }

        stop();
    }

    private void setupZookeeper() {
        String quorum = AppConfig.getString(Constants.ConfigKeys.ZK_QUORUM);
        String namespace = AppConfig.getString(Constants.ConfigKeys.ZK_NAMESPACE);
        log.info("Starting Zookeeper with connectString={}", quorum);
        zk = CuratorFrameworkFactory.builder()
                .connectString(quorum)
                .connectionTimeoutMs(2000)
                .retryPolicy(new RetryNTimes(6, 1000))
                .namespace(namespace.isEmpty() ? null : namespace)
                .build();

        zk.start();
    }

    private void setupServiceDiscovery() throws Exception {
        if (zk != null && zk.getState() == CuratorFrameworkState.STARTED) {

            dsc = ServiceDiscoveryBuilder.builder(Void.class)
                    .basePath(AppConfig.getString("discoveryPath"))
                    .client(zk)
                    .build();

            dsc.start();

            initializeServiceCaches();
        }
    }

    private void initializeServiceCaches() throws Exception {
        for (ServiceCache<Void> cache : serviceCacheList) {
            cache.close();
            serviceCacheList.remove(cache);
        }

        for (final String c : AppConfig.getStringList("clusters")) {
            ServiceCache<Void> cache = dsc.serviceCacheBuilder()
                    .name(c)
                    .build();

            cache.addListener(new ServiceCacheListener() {
                public void cacheChanged() {
                    log.info("Service {} modified, rewriting config", c);
                    updateInstances();
                }

                public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {
                }
            }, executorService);

            cache.start();
            serviceCacheList.add(cache);

        }
        AppConfig.addCallback("clusters", new Runnable() {
            public void run() {
                try {
                    initializeServiceCaches();
                    updateInstances();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected List<Cluster> provideClusters() {
        List<Cluster> clusterList = Lists.newArrayListWithExpectedSize(serviceCacheList.size());
        for (ServiceCache<Void> cache : serviceCacheList) {
            System.out.println(cache.getInstances());
            if (!cache.getInstances().isEmpty()) {
                Cluster cluster = buildCluster(cache.getInstances().get(0));

                for (ServiceInstance<Void> instance : cache.getInstances()) {
                    cluster.getServers().add(convertInstance(instance));
                }

                clusterList.add(cluster);
            }
        }
        return clusterList;
    }

    private Cluster buildCluster(ServiceInstance<Void> instance) {
        Cluster cluster =  new Cluster(instance.getName());
        if (instance.getSslPort() != null) {
            cluster.setProtocol("https");
        }

        return cluster;
    }

    protected Server convertInstance(ServiceInstance<Void> instance) {
        Integer port = instance.getSslPort() != null ? instance.getSslPort() : instance.getPort();
        return new Server(instance.getAddress(), port);
    }

    @Override
    public void stop() throws Exception {
        try {
            log.info("Shutting down...");
            run = false;
            executorService.shutdown();
            if (dsc != null) dsc.close();
            if (zk != null) zk.close();
        } catch (Exception e) {
            log.warn("Exception while shutting down", e);
        }
     }

}
