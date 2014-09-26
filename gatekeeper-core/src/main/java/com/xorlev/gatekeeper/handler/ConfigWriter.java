package com.xorlev.gatekeeper.handler;

import com.xorlev.gatekeeper.events.ConfigChangedEvent;

import java.io.IOException;

/**
 * 2014-03-01
 *
 * @author Michael Rose <michael@fullcontact.com>
 */
public interface ConfigWriter {
    void writeConfig(ConfigChangedEvent configChangedEvent) throws IOException;
}
