package com.xorlev.gatekeeper.nginx;

import com.google.common.eventbus.Subscribe;
import com.xorlev.gatekeeper.AppConfig;
import com.xorlev.gatekeeper.events.ClustersUpdatedEvent;
import com.xorlev.gatekeeper.handler.PostConfigCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;

/**
 * Handles reloading nginx's config file after it's been written
 *
 * @author Michael Rose <michael@fullcontact.com>
 */
public class NginxReloaderCallback implements PostConfigCallback {
    private static final Logger log = LoggerFactory.getLogger(NginxReloaderCallback.class);

    private final NginxManager nginxManager;

    public NginxReloaderCallback() throws FileNotFoundException {
        this.nginxManager = new NginxManager(AppConfig.getString("nginx.pid-file"));
    }

    @Subscribe
    @Override
    public void onConfigFinished(ClustersUpdatedEvent event) {
        try {
            nginxManager.reloadNginx();
        } catch (InvalidPidException | FileNotFoundException e) {
            log.error("ERROR: Failed to reload NGINX", e);
        }
    }
}
