package com.xorlev.gatekeeper.web;

import com.xorlev.gatekeeper.providers.ZookeeperClusterProvider;
import com.xorlev.gatekeeper.web.managed.ZookeeperClusterProviderManager;
import com.xorlev.gatekeeper.web.resources.ClusterResource;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Configuration;
import com.yammer.dropwizard.config.Environment;

/**
 * 2013-09-18
 *
 * @author Michael Rose <michael@fullcontact.com>
 */
public class GatekeeperService extends Service<Configuration> {
    @Override
    public void initialize(Bootstrap<Configuration> configurationBootstrap) {
    }

    @Override
    public void run(Configuration configuration, Environment environment) throws Exception {
        ZookeeperClusterProvider zookeeperClusterProvider = new ZookeeperClusterProvider();

        environment.manage(new ZookeeperClusterProviderManager(zookeeperClusterProvider));

        environment.addResource(new ClusterResource(zookeeperClusterProvider));
    }

    public static void main(String[] args) throws Exception {
        new GatekeeperService().run(args);
    }
}
