package com.xorlev.gatekeeper;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.xorlev.gatekeeper.nginx.NginxReloaderCallback;
import com.xorlev.gatekeeper.discovery.AbstractClusterDiscovery;
import com.xorlev.gatekeeper.handler.ConfigWriter;
import com.xorlev.gatekeeper.nginx.NginxConfigWriter;
import com.xorlev.gatekeeper.handler.PostConfigCallback;

import javax.annotation.Nullable;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * 2014-03-01
 *
 * @author Michael Rose <michael@fullcontact.com>
 */
public class GatekeeperModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ConfigWriter.class).to(NginxConfigWriter.class).asEagerSingleton();

        try {
            bind(AbstractClusterDiscovery.class).to((Class<? extends AbstractClusterDiscovery>) Class.forName(AppConfig.getString("discovery.impl"))).asEagerSingleton();
        } catch (ClassNotFoundException e) {
            throw new GatekeeperInitializationException("Failed to bind cluster discovery module", e);
        }
    }

    @Provides
    @Singleton
    NginxConfigWriter providesNginxConfigWriter() throws IOException {
        String filename = AppConfig.getString("nginx.config-file");
        return new NginxConfigWriter(new FileWriter(filename));
    }

    @Provides
    List<PostConfigCallback> provideConfigCallbacks() {
        return Lists.newArrayList((PostConfigCallback)new NginxReloaderCallback());
    }

//    @Provides
//    @Singleton
//    AbstractClusterDiscovery provideClusterDiscovery() {
//
//    }
}
