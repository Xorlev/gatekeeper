package com.xorlev.gatekeeper;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.google.common.util.concurrent.ServiceManager;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.xorlev.gatekeeper.discovery.AbstractClusterDiscovery;

import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.util.Collections;

/**
 * 2013-07-28
 *
 * @author Michael Rose <elementation@gmail.com>
 */
public class GatekeeperApplication {
    private final Injector injector;
    @Parameter(names = {"--config", "-c"}, description = "Config file to load")
    private String configurationFile = "gatekeeper.properties";

    private ServiceManager manager;
    private AbstractClusterDiscovery discovery;

    public GatekeeperApplication() throws Exception {
        AppConfig.initializeConfiguration(configurationFile);

        this.injector = Guice.createInjector(new GatekeeperModule());

        discovery = injector.getInstance(AbstractClusterDiscovery.class);

        manager = new ServiceManager(Collections.singleton(discovery));

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
        Signal.handle(new Signal("HUP"), new SignalHandler() {
            public void handle(Signal signal) {
                try {
                    discovery.forceUpdate();
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
