package com.company.hometask.configuration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationPropertiesLoaderTest {
    private ApplicationPropertiesLoader propertiesLoader = new ApplicationPropertiesLoader();

    @Test
    void getConfigurationInvalidPortExist() {
        assertThrows(RuntimeException.class, () -> propertiesLoader.getConfiguration("src/test/resources/config_not_valid_port.properties"));
    }

    @Test
    void getConfigurationPropertiesFileExist() {
        final HttpServerConfiguration configuration = propertiesLoader.getConfiguration(null);
        assertNotNull(configuration);
        assertEquals(configuration.getHost(), "localhost");
        assertEquals(configuration.getPort(), 8080);
    }
}
