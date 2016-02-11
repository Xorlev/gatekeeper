package com.xorlev.gatekeeper.handler;

import com.xorlev.gatekeeper.data.RoutingConfiguration;

import java.io.IOException;

/**
 * 2014-03-01
 *
 * @author Michael Rose <michael@fullcontact.com>
 */
public interface ConfigWriter {
    void writeConfig(RoutingConfiguration routingConfiguration) throws IOException;
}
