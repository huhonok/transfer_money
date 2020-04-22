package com.company.hometask.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ApplicationPropertiesLoader {
    private static final Logger log = LoggerFactory.getLogger(ApplicationPropertiesLoader.class);

    public HttpServerConfiguration getConfiguration(final String configFilePath) {
        HttpServerConfiguration httpServerConfiguration = null;
        if (configFilePath != null) {
            httpServerConfiguration = getConfigurationFromFile(configFilePath);
        }
        if (httpServerConfiguration != null) {
            return httpServerConfiguration;
        }
        try (final InputStream resource = this.getClass().getClassLoader().getResourceAsStream("config.properties")) {
            return getConfigurationFromProperties(resource);
        } catch (Exception e) {
            log.error("Error to read http server configuration from default config.properties", e);
            throw new RuntimeException("Unable to read http server configuration");
        }
    }

    private HttpServerConfiguration getConfigurationFromFile(final String configFilePath) {
        try (final InputStream fileInputStream = new FileInputStream(configFilePath)) {
            return getConfigurationFromProperties(fileInputStream);
        } catch (IOException e) {
            log.error("Error to read http server configuration from user file, " +
                    "try to get it from default config.properties", e);
            return null;
        }
    }

    private HttpServerConfiguration getConfigurationFromProperties(final InputStream inputStream) throws IOException {
        final Properties prop = new Properties();
        prop.load(inputStream);
        final String host = prop.getProperty("server.host");
        final int port = Integer.parseInt(prop.getProperty("server.port"));
        return new HttpServerConfiguration(host, port);
    }
}
