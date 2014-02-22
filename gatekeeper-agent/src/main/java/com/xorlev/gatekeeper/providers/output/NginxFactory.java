package com.xorlev.gatekeeper.providers.output;

import com.xorlev.gatekeeper.AppConfig;

import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * 2013-09-18
 *
 * @author Michael Rose <elementation@gmail.com>
 */
public class NginxFactory {
    public static ClusterHandler clusterHandler() throws IOException {
        return new NginxConfigWritingClusterHandler(configWriter());
    }

    private static ConfigWriter configWriter() throws IOException {
        String filename = AppConfig.getString("nginx.config-file");

        ConfigWriter configWriter = null;
        if (filename.isEmpty()) {
            configWriter = new ConfigWriter(new OutputStreamWriter(System.out));
        } else {
            configWriter = new ConfigWriter(new FileWriter(filename));
        }
        return configWriter;
    }
}
