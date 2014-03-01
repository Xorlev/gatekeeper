package com.xorlev.gatekeeper.providers.discovery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Polling Impl
 *
 * @author Michael Rose <elementation@gmail.com>
 */
public abstract class AbstractPollingClusterDiscovery extends AbstractClusterDiscovery {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    private ScheduledExecutorService scheduler;

    public void startUp() throws Exception {
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(new Runnable() {
            public void run() {
                updateInstances(false);
            }
        }, 1000, 10000, TimeUnit.MILLISECONDS);
    }

    public void shutDown() throws Exception {
        scheduler.shutdown();
    }
}
