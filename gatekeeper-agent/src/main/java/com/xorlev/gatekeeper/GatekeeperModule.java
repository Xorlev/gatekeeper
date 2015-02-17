package com.xorlev.gatekeeper;

import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import com.xorlev.gatekeeper.nginx.NginxReloaderCallback;
import com.xorlev.gatekeeper.discovery.AbstractClusterDiscovery;
import com.xorlev.gatekeeper.handler.ConfigWriter;
import com.xorlev.gatekeeper.nginx.NginxConfigWriter;
import com.xorlev.gatekeeper.handler.PostConfigCallback;
import org.weakref.jmx.guice.ExportBinder;

import javax.management.MBeanServer;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.List;

/**
 * 2014-03-01
 *
 * @author Michael Rose <michael@fullcontact.com>
 */
public class GatekeeperModule extends AbstractModule {
    @Override
    protected void configure() {
        // MBeanModule expects an MBeanServer to be bound
        bind(MBeanServer.class).toInstance(ManagementFactory.getPlatformMBeanServer());
        ExportBinder exporter = ExportBinder.newExporter(binder());

        bind(ConfigWriter.class).to(NginxConfigWriter.class).asEagerSingleton();

        try {
            bind(AbstractClusterDiscovery.class).to((Class<? extends AbstractClusterDiscovery>) Class.forName(AppConfig.getString("discovery.impl"))).asEagerSingleton();
        } catch (ClassNotFoundException e) {
            throw new GatekeeperInitializationException("Failed to bind cluster discovery module", e);
        }

        setupEventBus();

        exporter.export(AbstractClusterDiscovery.class).withGeneratedName();
        exporter.export(NginxConfigWriter.class).withGeneratedName();
    }

    private void setupEventBus() {
        final EventBus eventBus = new EventBus();
        bind(EventBus.class).toInstance(eventBus);
        bindListener(
                Matchers.any(), new TypeListener() {
                         public <I> void hear(TypeLiteral<I> typeLiteral, TypeEncounter<I> typeEncounter) {
                             typeEncounter.register(new InjectionListener<I>() {
                                                        public void afterInjection(I i) {
                                                            eventBus.register(i);
                                                        }
                                                    });
                         }
                     });
    }

    @Provides
    @Singleton
    NginxConfigWriter providesNginxConfigWriter() throws IOException {
        return new NginxConfigWriter();
    }

    @Provides
    @Singleton
    List<PostConfigCallback> provideConfigCallbacks() throws FileNotFoundException {
//        Iterables.transform(AppConfig.getStringList("handler.post_config_callbacks"), new Function<String, Object>() {
//            @Override
//            public Class<?> apply(String className) {
//                try {
//                    return getClass.forName(className);
//                } catch (ClassNotFoundException e) {
//                    throw new GatekeeperInitializationException("Failed to bind PostConfigCallback", e);
//                }
//            }
//        });
            return Lists.newArrayList((PostConfigCallback)new NginxReloaderCallback());
    }

//    @Provides
//    @Singleton
//    AbstractClusterDiscovery provideClusterDiscovery() {
//
//    }
}
