package com.xorlev.gatekeeper.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * cat /opt/nginx/logs/nginx.pid | sudo xargs -I{} kill -HUP {}
 *
 * @author Michael Rose <elementation@gmail.com>
 */
public class NginxManager {
    private static final Logger log = LoggerFactory.getLogger(NginxManager.class);

    public static int reloadNginx(String filename) throws FileNotFoundException {
        return reloadNginx(new PidReader(filename).getPid());
    }

    public static int reloadNginx(int pid) {
        log.info("Sending HUP to NGINX on {}", pid);
        try {
            int code = new ProcessBuilder().command(
                    "sudo", "kill", "-HUP", Integer.toString(pid)
            ).start().waitFor();

            log.info("HUP returned with {}", code);

            return code;
        } catch (Exception e) {
            log.error("Error attempting to restart NGINX: " + e.getMessage(), e);
            return 1;
        }
    }

    public static void main(String[] args) throws IOException {
        NginxManager.reloadNginx(31935);
    }
}
