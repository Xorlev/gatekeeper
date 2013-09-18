package com.xorlev.gatekeeper.web.managed;

import com.xorlev.gatekeeper.providers.ZookeeperClusterProvider;
import com.yammer.dropwizard.lifecycle.Managed;

/**
 * 2013-09-18
 *
 * @author Michael Rose <michael@fullcontact.com>
 */
public class ZookeeperClusterProviderManager implements Managed {
    private final ZookeeperClusterProvider zookeeperClusterProvider;

    public ZookeeperClusterProviderManager(ZookeeperClusterProvider zookeeperClusterProvider) {
        this.zookeeperClusterProvider = zookeeperClusterProvider;
    }

    public void start() throws Exception {
        zookeeperClusterProvider.startUp();
    }

    public void stop() throws Exception {
        zookeeperClusterProvider.shutDown();
    }
}
