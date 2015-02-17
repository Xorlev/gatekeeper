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

	private static final long serialVersionUID = 4143914026124243782L;
	
	String host;
    Integer port;

    public Server(String host, Integer port) {
        this.host = host;
        this.port = port;
    }
}
