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
import com.netflix.curator.x.discovery.details.ServiceCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class ZkWatcher {
    private static Logger log = LoggerFactory.getLogger(ZkWatcher.class);
    private CuratorFramework zk;

    private ServiceDiscovery<Void> dsc;
    private List<ServiceCache<Void>> serviceCache = Lists.newArrayList();

    private ClusterHandler clusterHandler;

    private transient boolean run = true;
    private ExecutorService executorService = Executors.newCachedThreadPool();

    public static void main(String[] args) throws Exception {
        AppConfig.initializeConfiguration("production");
        new ZkWatcher().start();
    }

    private void start() throws Exception {
        Signal.handle(new Signal("TERM"), new SignalHandler() {
            public void handle(Signal signal) {
                stop();
            }
        });


        setupZookeeper();
        setupServiceDiscovery();
        clusterHandler = new ClusterHandler<Void>(serviceCache);

        clusterHandler.processClusters();

        while (run) {
            Thread.sleep(5000);
        }

        stop();
    }

    private void setupZookeeper() {
        String quorum = AppConfig.getString("zookeeper.quorum");
        log.info("Starting Zookeeper with connectString={}", quorum);
        zk = CuratorFrameworkFactory.builder()
                .connectString(quorum)
                .connectionTimeoutMs(2000)
                .retryPolicy(new RetryNTimes(6, 1000))
                .namespace("discovery")
                .build();

        zk.start();
    }

    private void setupServiceDiscovery() throws Exception {
        if (zk != null && zk.getState() == CuratorFrameworkState.STARTED) {

            dsc = ServiceDiscoveryBuilder.builder(Void.class)
                    .basePath("/turbine-aggregate")
                    .client(zk)
                    .build();

            dsc.start();

            initializeServiceCaches();
        }
    }

    private void initializeServiceCaches() throws Exception {
        for (final String c : AppConfig.getStringList("clusters")) {
            ServiceCache<Void> cache = dsc.serviceCacheBuilder()
                    .name(c)
                    .build();

            cache.addListener(new ServiceCacheListener() {
                public void cacheChanged() {
                    log.info("Service {} modified, rewriting config", c);
                    clusterHandler.processClusters();
                }

                public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {
                }
            }, executorService);

            cache.start();
            serviceCache.add(cache);
        }
    }

    private void stop() {
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
