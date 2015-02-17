package com.xorlev.gatekeeper.nginx;

/**
 * 2014-02-28
 *
 * @author Michael Rose <michael@fullcontact.com>
 */
public class InvalidPidException extends RuntimeException {

	private static final long serialVersionUID = 6389211532274680422L;

	public InvalidPidException(String message, Throwable cause) {
        super(message, cause);
    }
}
