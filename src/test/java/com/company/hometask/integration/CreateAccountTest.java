package com.company.hometask.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.company.hometask.configuration.ObjectFactory;
import com.company.hometask.web.dto.Account;
import com.company.hometask.web.dto.ErrorResponse;
import io.undertow.Undertow;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static io.undertow.util.StatusCodes.BAD_REQUEST;
import static org.junit.jupiter.api.Assertions.*;

class CreateAccountTest {

    private static Undertow HTTP_SERVER;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final HttpResponse.BodyHandler<String> responseBodyHandler = HttpResponse.BodyHandlers.ofString();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final URI createAccountUri = URI.create("http://localhost:8080/accounts");

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
    void createAccountSuccessful() throws IOException, InterruptedException {
        final Account accountDto = new Account(100L);
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(createAccountUri)
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(accountDto)))
                .build();
        final HttpResponse result = httpClient.send(request, responseBodyHandler);
        assertEquals(StatusCodes.CREATED, result.statusCode());
        assertNotNull(result.body());
        assertNotNull(result.headers().map().get(Headers.LOCATION.toString()));
    }

    @Test
    void createAccountBadJson() throws IOException, InterruptedException {
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(createAccountUri)
                .POST(HttpRequest.BodyPublishers.ofString("bad json"))
                .build();
        final HttpResponse result = httpClient.send(request, responseBodyHandler);
        assertEquals(StatusCodes.BAD_REQUEST, result.statusCode());
        assertNotNull(result.body());
        final ErrorResponse errorResponse = objectMapper.readValue(result.body().toString(), ErrorResponse.class);
        assertEquals(errorResponse.getStatusCode(), BAD_REQUEST);

    }

    @Test
    void createAccountBadAmount() throws IOException, InterruptedException {
        final Account accountDto = new Account(-100L);
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(createAccountUri)
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(accountDto)))
                .build();
        final HttpResponse result = httpClient.send(request, responseBodyHandler);
        assertEquals(StatusCodes.BAD_REQUEST, result.statusCode());
        assertNotNull(result.body());
        final ErrorResponse errorResponse = objectMapper.readValue(result.body().toString(), ErrorResponse.class);
        assertEquals(errorResponse.getStatusCode(), BAD_REQUEST);
    }
}
