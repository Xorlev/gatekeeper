package com.xorlev.gatekeeper.nginx;

import com.google.common.io.CharStreams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.InputStreamReader;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * cat /opt/nginx/logs/nginx.pid | sudo xargs -I{} kill -HUP {}
 *
 * @author Michael Rose <elementation@gmail.com>
 */
public class NginxManager {
    private static final Logger log = LoggerFactory.getLogger(NginxManager.class);
    private final String filename;

    public NginxManager(String filename) throws FileNotFoundException {
        this.filename = checkNotNull(filename);
        new PidReader(filename).getPid();
    }

    public int reloadNginx() throws FileNotFoundException {
        return reloadNginx(new PidReader(filename).getPid());
    }

    private int reloadNginx(int pid) {
        log.info("Sending HUP to NGINX on {}", pid);
        try {
            Process process = new ProcessBuilder().command("kill", "-HUP", Integer.toString(pid)).start();

            int code = process.waitFor();
            String output = CharStreams.toString(new InputStreamReader(process.getInputStream()));

            log.info("HUP returned with code {}  and output {}", code, output);

            if (code != 0) throw new NginxRestartException("Process restart command returned code " + code);

            return code;
        } catch (NginxRestartException e) {
            throw e;
        } catch (Exception e) {
            log.error("Uncaught error attempting to restart NGINX: " + e.getMessage(), e);
            return 1;
        }
    }
}
