package com.xorlev.gatekeeper.data;

import lombok.Data;

/**
 * 2013-07-27
 *
 * @author Michael Rose <elementation@gmail.com>
 */
@Data
public class Server {
    String host;
    Integer port;

    public Server(String host, Integer port) {
        this.host = host;
        this.port = port;
    }
}
