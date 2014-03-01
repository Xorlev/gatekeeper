package com.xorlev.gatekeeper.handler;

import com.xorlev.gatekeeper.data.ConfigContext;

import java.io.IOException;

/**
 * 2014-03-01
 *
 * @author Michael Rose <michael@fullcontact.com>
 */
public interface ConfigWriter {
    void writeConfig(ConfigContext configContext) throws IOException;
}
