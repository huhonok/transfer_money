package com.company.hometask.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.company.hometask.configuration.ObjectFactory;
import com.company.hometask.web.dto.Account;
import com.company.hometask.web.dto.AccountFullInfo;
import io.undertow.Undertow;
import io.undertow.util.StatusCodes;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static io.undertow.util.StatusCodes.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GetAccountsTest {
    private static Undertow HTTP_SERVER;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final URI getAccountsUri = URI.create("http://localhost:8080/accounts");
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpResponse.BodyHandler<String> responseBodyHandler = HttpResponse.BodyHandlers.ofString();

    @BeforeAll
    static void prepare() {
        final ObjectFactory objectFactory = new ObjectFactory();
        HTTP_SERVER = objectFactory.httpServer("config.properties");
        HTTP_SERVER.start();
    }

    @AfterAll
    static void after() {
        HTTP_SERVER.stop();
    }

    @Test
    void getEmptyAccounts() throws IOException, InterruptedException {
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(getAccountsUri)
                .build();
        final HttpResponse result = httpClient.send(request, responseBodyHandler);
        assertEquals(StatusCodes.NO_CONTENT, result.statusCode());
    }

    @Test
    void getNonEmptyAccounts() throws IOException, InterruptedException {
        //create account
        final Account firstAccountDto = new Account(100L);
        final HttpRequest creationFirstAccountRequest = HttpRequest.newBuilder()
                .uri(getAccountsUri)
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(firstAccountDto)))
                .build();
        httpClient.send(creationFirstAccountRequest, responseBodyHandler);

        final Account secondAccountDto = new Account(100L);
        final HttpRequest creationSecondAccountRequest = HttpRequest.newBuilder()
                .uri(getAccountsUri)
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(secondAccountDto)))
                .build();
        httpClient.send(creationSecondAccountRequest, responseBodyHandler);

        //get accounts
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(getAccountsUri)
                .build();
        final HttpResponse result = httpClient.send(request, responseBodyHandler);
        final AccountFullInfo[] accountFullInfos = objectMapper.readValue(result.body().toString(), AccountFullInfo[].class);

        assertEquals(StatusCodes.OK, result.statusCode());
        assertEquals(2, accountFullInfos.length);
    }
}
