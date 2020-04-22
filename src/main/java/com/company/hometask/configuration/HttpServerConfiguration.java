package com.company.hometask.configuration;

public class HttpServerConfiguration {
    private String host;
    private int port;

    public HttpServerConfiguration(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

}
