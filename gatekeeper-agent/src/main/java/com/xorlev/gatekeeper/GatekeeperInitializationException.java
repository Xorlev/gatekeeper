package com.xorlev.gatekeeper;

/**
 * 2014-03-01
 *
 * @author Michael Rose <michael@fullcontact.com>
 */
public class GatekeeperInitializationException extends RuntimeException {

	private static final long serialVersionUID = -2572438987587452102L;

	public GatekeeperInitializationException(String message) {
        super(message);
    }

    public GatekeeperInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
