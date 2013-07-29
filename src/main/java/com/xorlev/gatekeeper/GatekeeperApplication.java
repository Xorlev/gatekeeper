package com.xorlev.gatekeeper;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

/**
 * 2013-07-28
 *
 * @author Michael Rose <michael@fullcontact.com>
 */
public class GatekeeperApplication {
    @Parameter(names = {"--environment", "-e"}, description = "Environment config to load")
    private String environment = "production";


    @Parameter(names = {"--outputPath", "-o"}, description = "NGINX config file (e.x. /etc/nginx/nginx.conf)")
    private String nginxConfigPath;


    @Parameter(names = {"--inputPath", "-i"}, description = "Template for NGINX config")
    private String nginxTemplatePath;

    @Parameter(names = {"--pidPath", "-p"}, description = "Path to PID file")
    private String pidPath;

    public GatekeeperApplication() throws Exception {
        AppConfig.initializeConfiguration(environment);

        AbstractClusterProvider clusterProvider =
                (AbstractClusterProvider) Class.forName(AppConfig.getString("cluster_provider.impl"))
                        .getConstructors()[0].newInstance(new ClusterHandler());
        clusterProvider.start();

        // spin

        clusterProvider.stop();
    }

    public static void main(String[] args) throws Exception {
        GatekeeperApplication application = new GatekeeperApplication();
        JCommander jCommander = new JCommander(application, args);

        jCommander.setProgramName(Constants.APP_NAME);
    }
}
