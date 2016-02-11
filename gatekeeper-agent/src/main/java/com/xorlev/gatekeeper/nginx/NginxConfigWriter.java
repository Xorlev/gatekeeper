package com.xorlev.gatekeeper.nginx;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.common.eventbus.Subscribe;
import com.xorlev.gatekeeper.AppConfig;
import com.xorlev.gatekeeper.data.RoutingConfiguration;
import com.xorlev.gatekeeper.handler.ConfigWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.weakref.jmx.Managed;

import java.io.*;
import java.util.Date;

public class NginxConfigWriter implements ConfigWriter {
    private static final Logger log = LoggerFactory.getLogger(NginxConfigWriter.class);
    private final Mustache mustache;
    private int timesWritten;
    private Date lastWritten;

    public NginxConfigWriter() throws FileNotFoundException {
        MustacheFactory mf = new DefaultMustacheFactory();
        Reader reader;
        try {
            reader = new FileReader(new File(AppConfig.getString("nginx.template-file")));
        } catch (FileNotFoundException e) {
            InputStream is = this.getClass().getResourceAsStream("nginx.conf.mustache");

            if (is == null) throw e;

            reader = new InputStreamReader(is);
        }

        this.mustache = mf.compile(reader, "nginx_conf");
    }

    @Subscribe
    @Override
    public void writeConfig(RoutingConfiguration routingConfiguration) throws IOException {
        String filename = AppConfig.getString("nginx.config-file");

        log.info("Flushing NGINX config to {}", filename);

        mustache.execute(new FileWriter(filename), routingConfiguration).flush();

        timesWritten++;
        lastWritten = new Date();
    }

    @Managed
    public int getTimesWritten() {
        return timesWritten;
    }

    @Managed
    public Date getLastWritten() {
        return lastWritten;
    }
}
