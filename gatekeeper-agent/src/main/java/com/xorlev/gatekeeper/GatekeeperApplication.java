package com.xorlev.gatekeeper;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.google.common.util.concurrent.ServiceManager;
import com.xorlev.gatekeeper.providers.discovery.ClusterDiscoveryFactory;
import com.xorlev.gatekeeper.providers.discovery.AbstractClusterDiscovery;
import com.xorlev.gatekeeper.providers.output.NginxFactory;
import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.util.Collections;

/**
 * 2013-07-28
 *
 * @author Michael Rose <elementation@gmail.com>
 */
public class GatekeeperApplication {
    @Parameter(names = {"--environment", "-e"}, description = "Environment config to load")
    private String environment = "production";

    private ServiceManager manager;

    public GatekeeperApplication() throws Exception {
        AppConfig.initializeConfiguration(environment);

        AbstractClusterDiscovery clusterProvider = ClusterDiscoveryFactory
                .providerFor(AppConfig.getString("cluster_provider.impl"));
        clusterProvider.registerHandler(NginxFactory.clusterHandler());

        manager = new ServiceManager(Collections.singleton(clusterProvider));

        registerSignalHandler();

        manager.startAsync();
        manager.awaitStopped();
    }

    private void registerSignalHandler() {
        Signal.handle(new Signal("TERM"), new SignalHandler() {
            public void handle(Signal signal) {
                try {
                    manager.stopAsync();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void main(String[] args) throws Exception {
        GatekeeperApplication application = new GatekeeperApplication();
        JCommander jCommander = new JCommander(application, args);

        jCommander.setProgramName(Constants.APP_NAME);
    }
}
