package com.xorlev.gatekeeper.data;

import lombok.Data;

import java.io.Serializable;

/**
 * 2013-07-27
 *
 * @author Michael Rose <elementation@gmail.com>
 */
@Data
public class Server implements Serializable {
    String host;
    Integer port;

    public Server(String host, Integer port) {
        this.host = host;
        this.port = port;
    }
}
