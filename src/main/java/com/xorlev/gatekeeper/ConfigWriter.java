package com.xorlev.gatekeeper;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.xorlev.gatekeeper.data.ConfigContext;

import java.io.*;

public class ConfigWriter {
    private final Mustache mustache;
    private Writer writer = new OutputStreamWriter(System.out);

    public ConfigWriter(Writer writer) {
        MustacheFactory mf = new DefaultMustacheFactory();
        Reader reader = null;
        try {
            reader = new FileReader(new File(AppConfig.getString("nginx.template-file")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        this.mustache = mf.compile(reader, "nginx_conf");
        this.writer = writer;
    }

    void writeConfig(ConfigContext configContext) throws IOException {
        System.out.println(configContext);
        String filename = AppConfig.getString("nginx.config-file");
        writer = new FileWriter(filename);
        mustache.execute(writer, configContext).flush();
    }
}