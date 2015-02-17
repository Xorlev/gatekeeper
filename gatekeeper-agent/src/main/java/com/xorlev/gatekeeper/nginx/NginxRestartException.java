package com.xorlev.gatekeeper.nginx;

/**
 * 2013-09-18
 *
 * @author Michael Rose <elementation@gmail.com>
 */
public class NginxRestartException extends RuntimeException {

	private static final long serialVersionUID = 6086536272104191176L;

	public NginxRestartException(String message) {
        super(message);
    }

    public NginxRestartException(String message, Throwable cause) {
        super(message, cause);
    }

    public NginxRestartException(Throwable cause) {
        super(cause);
    }
}
