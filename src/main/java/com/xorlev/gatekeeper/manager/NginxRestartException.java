package com.xorlev.gatekeeper.manager;

/**
 * 2013-09-18
 *
 * @author Michael Rose <michael@fullcontact.com>
 */
public class NginxRestartException extends RuntimeException {
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
