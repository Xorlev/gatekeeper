package com.xorlev.gatekeeper.providers.discovery;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * 2013-09-18
 *
 * @author Michael Rose <elementation@gmail.com>
 */
public class ClusterDiscoveryFactory {
    public static AbstractClusterDiscovery providerFor(String className)
            throws ClassNotFoundException, IllegalAccessException, InvocationTargetException,
            InstantiationException, IOException, NoSuchMethodException {
        return (AbstractClusterDiscovery) Class.forName(className)
                .getConstructor()
                .newInstance();
    }
}
