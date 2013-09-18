package com.xorlev.gatekeeper.providers;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * 2013-09-18
 *
 * @author Michael Rose <michael@fullcontact.com>
 */
public class ClusterProviderFactory {
    public static AbstractClusterProvider providerFor(String className) throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, InstantiationException, IOException {
        return (AbstractClusterProvider) Class.forName(className)
                .getConstructors()[0]
                .newInstance();
    }
}
