package com.xorlev.gatekeeper;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * 2013-07-28
 *
 * @author Michael Rose <michael@fullcontact.com>
 */
public class GatekeeperApplication {
    @Parameter(names = {"--environment", "-e"}, description = "Environment config to load")
    private String environment = "production";

    public GatekeeperApplication() throws Exception {
        AppConfig.initializeConfiguration(environment);


        ConfigWriter configWriter = getConfigWriter();

        AbstractClusterProvider clusterProvider =
                (AbstractClusterProvider) Class.forName(AppConfig.getString("cluster_provider.impl"))
                        .getConstructors()[0].newInstance(new ClusterHandler(configWriter));
        clusterProvider.start();

        // spin

        clusterProvider.stop();
    }

    private ConfigWriter getConfigWriter() throws IOException {
        String filename = AppConfig.getString("nginx.config-file");

        ConfigWriter configWriter = null;
        if (filename.isEmpty()) {
            configWriter = new ConfigWriter(new OutputStreamWriter(System.out));
        } else {
            configWriter = new ConfigWriter(new FileWriter(filename));
        }
        return configWriter;
    }

    public static void main(String[] args) throws Exception {
        GatekeeperApplication application = new GatekeeperApplication();
        JCommander jCommander = new JCommander(application, args);

        jCommander.setProgramName(Constants.APP_NAME);
    }
}
