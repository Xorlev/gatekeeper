package com.xorlev.gatekeeper.nginx;

import com.xorlev.gatekeeper.AppConfig;
import com.xorlev.gatekeeper.providers.discovery.ClustersUpdatedEvent;
import com.xorlev.gatekeeper.providers.output.PostConfigCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;

/**
 * 2014-03-01
 *
 * @author Michael Rose <michael@fullcontact.com>
 */
public class NginxReloaderCallback implements PostConfigCallback {
    private static final Logger log = LoggerFactory.getLogger(NginxReloaderCallback.class);

    @Override
    public void onConfigFinished(ClustersUpdatedEvent event) {
        try {
            NginxManager.reloadNginx(AppConfig.getString("nginx.pid-file"));
        } catch (InvalidPidException | FileNotFoundException e) {
            log.error("ERROR: Failed to reload NGINX", e);
        }
    }
}
