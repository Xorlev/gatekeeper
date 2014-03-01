package com.xorlev.gatekeeper.handler;

import com.xorlev.gatekeeper.AppConfig;
import com.xorlev.gatekeeper.data.Cluster;
import com.xorlev.gatekeeper.data.ConfigContext;
import com.xorlev.gatekeeper.data.Location;
import com.xorlev.gatekeeper.discovery.ClustersUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

public class ConfigWriterClusterHandler implements ClusterHandler {
    private static final Logger log = LoggerFactory.getLogger(ConfigWriterClusterHandler.class);
    private final ConfigWriter configWriter;
    private final List<PostConfigCallback> postConfigCallbacks;

    @Inject
    public ConfigWriterClusterHandler(ConfigWriter configWriter, List<PostConfigCallback> postConfigCallbacks) {
        this.configWriter = configWriter;
        this.postConfigCallbacks = postConfigCallbacks;
    }

    public void processClusters(ClustersUpdatedEvent event) {
        writeConfig(event.getClusters());
        runPostConfigCallbacks(event);
    }

    private void writeConfig(List<Cluster> clusters) {
        ConfigContext configContext = buildConfigContext(clusters);

        try {
            configWriter.writeConfig(configContext);
        } catch (IOException e) {
            log.error("Failed to write config, {}", e.getMessage(), e);
        }
    }

    private void runPostConfigCallbacks(ClustersUpdatedEvent event) {
        for (PostConfigCallback postConfigCallback : postConfigCallbacks) {
            try {
                postConfigCallback.onConfigFinished(event);
            } catch (Exception e) {
                log.error("Failed to run post-config callback, {}", postConfigCallback.getClass().getSimpleName(), e);
            }
        }
    }

    private ConfigContext buildConfigContext(List<Cluster> clusters) {
        ConfigContext configContext = new ConfigContext(clusters);

        for (Cluster cluster : clusters) {
            log.info("({}) Locating contexts for {}://{}:{}, {} servers in group",
                    cluster.getClusterName(), cluster.getProtocol(), cluster.getClusterName(), cluster.getPort(),
                    cluster.getServers().size());

            for (String context : AppConfig.getStringList("cluster." + cluster.getClusterName() + ".context")) {
                log.info("({}) Adding context=[{}]", cluster.getClusterName(), context);
                configContext.getLocations().add(new Location(context, cluster));
            }
        }
        return configContext;
    }
}