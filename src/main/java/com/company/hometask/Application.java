package com.company.hometask;

import com.company.hometask.configuration.ApplicationPropertiesLoader;
import com.company.hometask.configuration.ObjectFactory;
import io.undertow.Undertow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {
    static {
        System.setProperty("org.jboss.logging.provider", "slf4j");
    }

    private static final Logger log = LoggerFactory.getLogger(ApplicationPropertiesLoader.class);

    public static void main(String[] args) {
        final String configPathFromArgs = args.length != 0 ? args[0] : null;
        final ObjectFactory objectFactory = new ObjectFactory();
        final Undertow httpServer = objectFactory.httpServer(configPathFromArgs);
        httpServer.start();
        log.info("---------------HTTP SERVER was configured and started SUCCESSFULLY---------------");
    }
}
