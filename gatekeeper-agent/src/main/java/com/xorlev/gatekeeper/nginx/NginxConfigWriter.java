package com.xorlev.gatekeeper.nginx;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.xorlev.gatekeeper.AppConfig;
import com.xorlev.gatekeeper.data.ConfigContext;
import com.xorlev.gatekeeper.handler.ConfigWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.weakref.jmx.Managed;

import javax.inject.Inject;
import java.io.*;
import java.util.Date;

public class NginxConfigWriter implements ConfigWriter {
    private static final Logger log = LoggerFactory.getLogger(NginxConfigWriter.class);
    private final Mustache mustache;
    private Writer writer = new OutputStreamWriter(System.out);
    int timesWritten;
    Date lastWritten;

    @Inject
    public NginxConfigWriter(Writer writer) throws FileNotFoundException {
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
        this.writer = writer;
    }

    @Override
    public void writeConfig(ConfigContext configContext) throws IOException {
        String filename = AppConfig.getString("nginx.config-file");

        log.info("Flushing NGINX config to {}", filename);

        writer = new FileWriter(filename);
        mustache.execute(writer, configContext).flush();

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