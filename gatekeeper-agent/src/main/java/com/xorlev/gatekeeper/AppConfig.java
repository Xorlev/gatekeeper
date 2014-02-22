package com.xorlev.gatekeeper;

import com.netflix.config.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class AppConfig {
    protected static final Logger log = LoggerFactory.getLogger(AppConfig.class);
    protected static boolean initialized = false;

    /**
     * Initializes Archaius using the proper gatekeeper-{environment}.properties file
     *
     * @param environment
     */
    public static void initializeConfiguration(String environment) throws IOException {
        log.info("Initializing cascaded config with environment=[{}]", environment);

        if (!initialized) {
            synchronized (AppConfig.class) {
                initialized = true;
//                DynamicPropertyFactory.getInstance();

                ConfigurationManager.loadCascadedPropertiesFromResources("gatekeeper");
            }
        }
    }

    public static Integer getInt(String property) {
        final DynamicIntProperty intProperty = DynamicPropertyFactory.getInstance().getIntProperty(property, 0);

        return intProperty.get();
    }

    public static Long getLong(String property) {
        final DynamicLongProperty longProperty = DynamicPropertyFactory.getInstance().getLongProperty(property, 0);

        return longProperty.get();
    }

    public static Boolean getBoolean(String property) {
        final DynamicBooleanProperty booleanProperty = DynamicPropertyFactory.getInstance().getBooleanProperty(property, false);

        return booleanProperty.get();
    }

    public static Double getDouble(String property) {
        final DynamicDoubleProperty doubleProperty = DynamicPropertyFactory.getInstance().getDoubleProperty(property, 0.0);

        return doubleProperty.get();
    }

    public static String getString(String property) {
        final DynamicStringProperty stringProperty = DynamicPropertyFactory.getInstance().getStringProperty(property, "");

        return stringProperty.get();
    }

    public static List<String> getStringList(String property) {
        final DynamicListProperty<String> listProperty = new DynamicStringListProperty(property, "");

        return listProperty.get();
    }

    public static void addCallback(String property, Runnable callback) {
        final DynamicProperty dynamicProperty = DynamicProperty.getInstance(property);

        dynamicProperty.addCallback(callback);
    }
}