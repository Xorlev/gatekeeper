package com.xorlev.gatekeeper.providers;

import com.xorlev.gatekeeper.AppConfig;
import com.xorlev.gatekeeper.data.Cluster;
import com.xorlev.gatekeeper.data.ConfigContext;
import com.xorlev.gatekeeper.data.Location;
import com.xorlev.gatekeeper.manager.NginxManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class ConfigWritingClusterHandler implements ClusterHandler {
    private ConfigWriter configWriter;
    private static final Logger log = LoggerFactory.getLogger(ConfigWritingClusterHandler.class);

    public ConfigWritingClusterHandler(ConfigWriter configWriter) {
        this.configWriter = configWriter;
    }

    public void processClusters(ClustersUpdatedEvent event) {
        writeConfig(event.getClusters());

        try {
            NginxManager.reloadNginx(AppConfig.getString("nginx.pid-file"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void writeConfig(List<Cluster> clusters) {
        ConfigContext configContext = new ConfigContext(clusters);

        for (Cluster cluster : clusters) {
            for (String context : AppConfig.getStringList("cluster." + cluster.getClusterName() + ".context")) {
                configContext.getLocations().add(new Location(context, cluster));
            }
        }
        try {
            configWriter.writeConfig(configContext);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}