package com.xorlev.gatekeeper.manager;

import com.google.common.io.CharStreams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

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
            Process process = new ProcessBuilder().command("kill", "-HUP", Integer.toString(pid)).start();

            int code = process.waitFor();
            String output = CharStreams.toString(new InputStreamReader(process.getInputStream()));

            log.info("HUP returned with {}", code);

            if (code != 0) throw new NginxRestartException("Process restart command returned code " + code);

            return code;
        } catch (Exception e) {
            log.error("Error attempting to restart NGINX: " + e.getMessage(), e);
            return 1;
        }
    }
}
