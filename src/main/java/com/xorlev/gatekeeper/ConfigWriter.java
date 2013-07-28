package com.xorlev.gatekeeper;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.xorlev.gatekeeper.data.ConfigContext;

import java.io.*;

public class ConfigWriter {
    private final Mustache mustache;
    private final Writer writer;

    public ConfigWriter() {
        this(new OutputStreamWriter(System.out));
    }

    public ConfigWriter(Writer writer) {
        MustacheFactory mf = new DefaultMustacheFactory();
        Reader reader = new InputStreamReader(ConfigWriter.class.getClassLoader().getResourceAsStream("nginx.conf.mustache"));

        this.mustache = mf.compile(reader, "nginx_conf");
        this.writer = writer;
    }

    void writeConfig(ConfigContext configContext) throws IOException {
        System.out.println(configContext);
        mustache.execute(writer, configContext).flush();
    }
}