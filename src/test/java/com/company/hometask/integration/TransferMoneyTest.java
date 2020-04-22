package com.company.hometask.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.company.hometask.configuration.ObjectFactory;
import com.company.hometask.web.dto.Account;
import com.company.hometask.web.dto.AccountFullInfo;
import com.company.hometask.web.dto.ErrorResponse;
import com.company.hometask.web.dto.MoneyTransfer;
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

import static io.undertow.util.StatusCodes.BAD_REQUEST;
import static io.undertow.util.StatusCodes.NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TransferMoneyTest {
    private static Undertow HTTP_SERVER;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final HttpResponse.BodyHandler<String> responseBodyHandler = HttpResponse.BodyHandlers.ofString();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final URI transferUri = URI.create("http://localhost:8080/transfers");
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
    void transferMoneyBadJson() throws IOException, InterruptedException {
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(transferUri)
                .POST(HttpRequest.BodyPublishers.ofString("{\"bad\": \"json\"}"))
                .build();
        final HttpResponse result = httpClient.send(request, responseBodyHandler);
        assertEquals(StatusCodes.BAD_REQUEST, result.statusCode());
        assertNotNull(result.body());
        final ErrorResponse errorResponse = objectMapper.readValue(result.body().toString(), ErrorResponse.class);
        assertEquals(errorResponse.getStatusCode(), BAD_REQUEST);
    }

    @Test
    void transferMoneyEmptyReceiverAccountId() throws IOException, InterruptedException {
        final MoneyTransfer moneyTransferDto = new MoneyTransfer("1", null, 50L);

        final HttpRequest request = HttpRequest.newBuilder()
                .uri(transferUri)
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(moneyTransferDto)))
                .build();
        final HttpResponse result = httpClient.send(request, responseBodyHandler);
        assertEquals(StatusCodes.BAD_REQUEST, result.statusCode());
        assertNotNull(result.body());
        final ErrorResponse errorResponse = objectMapper.readValue(result.body().toString(), ErrorResponse.class);
        assertEquals(errorResponse.getStatusCode(), BAD_REQUEST);
    }

    @Test
    void transferMoneyEmptySenderAccountId() throws IOException, InterruptedException {
        final MoneyTransfer moneyTransferDto = new MoneyTransfer(null, "2", 50L);

        final HttpRequest request = HttpRequest.newBuilder()
                .uri(transferUri)
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(moneyTransferDto)))
                .build();
        final HttpResponse result = httpClient.send(request, responseBodyHandler);
        assertEquals(StatusCodes.BAD_REQUEST, result.statusCode());
        assertNotNull(result.body());
        final ErrorResponse errorResponse = objectMapper.readValue(result.body().toString(), ErrorResponse.class);
        assertEquals(errorResponse.getStatusCode(), BAD_REQUEST);
    }

    @Test
    void transferMoneySenderAccountIdEqualReceiver() throws IOException, InterruptedException {
        final MoneyTransfer moneyTransferDto = new MoneyTransfer("1", "1", 50L);

        final HttpRequest request = HttpRequest.newBuilder()
                .uri(transferUri)
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(moneyTransferDto)))
                .build();

        final HttpResponse result = httpClient.send(request, responseBodyHandler);
        assertEquals(StatusCodes.BAD_REQUEST, result.statusCode());
        assertNotNull(result.body());
        final ErrorResponse errorResponse = objectMapper.readValue(result.body().toString(), ErrorResponse.class);
        assertEquals(errorResponse.getStatusCode(), BAD_REQUEST);
    }

    @Test
    void transferMoneyZeroAmount() throws IOException, InterruptedException {
        final MoneyTransfer moneyTransferDto = new MoneyTransfer("1", "2", 0L);

        final HttpRequest request = HttpRequest.newBuilder()
                .uri(transferUri)
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(moneyTransferDto)))
                .build();

        final HttpResponse result = httpClient.send(request, responseBodyHandler);
        assertEquals(StatusCodes.BAD_REQUEST, result.statusCode());
        assertNotNull(result.body());
        final ErrorResponse errorResponse = objectMapper.readValue(result.body().toString(), ErrorResponse.class);
        assertEquals(errorResponse.getStatusCode(), BAD_REQUEST);
    }

    @Test
    void transferMoneySuccessful() throws IOException, InterruptedException {
        //create accounts
        final Account senderAccountDto = new Account(100L);
        final HttpRequest creationSenderRequest = HttpRequest.newBuilder()
                .uri(createAccountUri)
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(senderAccountDto)))
                .build();
        final HttpResponse creationSenderResponse = httpClient.send(creationSenderRequest, responseBodyHandler);
        final AccountFullInfo senderAccount = objectMapper.readValue(creationSenderResponse.body().toString(), AccountFullInfo.class);

        final Account receiverAccountDto = new Account(200L);
        final HttpRequest creationReceiverRequest = HttpRequest.newBuilder()
                .uri(createAccountUri)
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(receiverAccountDto)))
                .build();
        final HttpResponse creationReceiverResponse = httpClient.send(creationReceiverRequest, responseBodyHandler);
        final AccountFullInfo receiverAccount = objectMapper.readValue(creationReceiverResponse.body().toString(), AccountFullInfo.class);

        final MoneyTransfer moneyTransferDto = new MoneyTransfer(senderAccount.getId(), receiverAccount.getId(), 50L);
        //transfer money
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(transferUri)
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(moneyTransferDto)))
                .build();
        final HttpResponse result = httpClient.send(request, responseBodyHandler);
        assertEquals(StatusCodes.OK, result.statusCode());
    }

    @Test
    void transferSenderNotFound() throws IOException, InterruptedException {
        //create account
        final Account receiverAccountDto = new Account(200L);
        final HttpRequest creationReceiverRequest = HttpRequest.newBuilder()
                .uri(createAccountUri)
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(receiverAccountDto)))
                .build();
        final HttpResponse creationReceiverResponse = httpClient.send(creationReceiverRequest, responseBodyHandler);
        final AccountFullInfo receiverAccount = objectMapper.readValue(creationReceiverResponse.body().toString(), AccountFullInfo.class);

        final MoneyTransfer moneyTransferDto = new MoneyTransfer("1", receiverAccount.getId(), 50L);
        //transfer money
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(transferUri)
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(moneyTransferDto)))
                .build();
        final HttpResponse result = httpClient.send(request, responseBodyHandler);
        assertEquals(StatusCodes.NOT_FOUND, result.statusCode());
        assertNotNull(result.body());
        final ErrorResponse errorResponse = objectMapper.readValue(result.body().toString(), ErrorResponse.class);
        assertEquals(errorResponse.getStatusCode(), NOT_FOUND);
    }

    @Test
    void transferReceiverNotFound() throws IOException, InterruptedException {
        //create accounts
        final Account senderAccountDto = new Account(100L);
        final HttpRequest creationSenderRequest = HttpRequest.newBuilder()
                .uri(createAccountUri)
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(senderAccountDto)))
                .build();
        final HttpResponse creationSenderResponse = httpClient.send(creationSenderRequest, responseBodyHandler);
        final AccountFullInfo senderAccount = objectMapper.readValue(creationSenderResponse.body().toString(), AccountFullInfo.class);

        final MoneyTransfer moneyTransferDto = new MoneyTransfer(senderAccount.getId(), "1", 50L);
        //transfer money
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(transferUri)
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(moneyTransferDto)))
                .build();
        final HttpResponse result = httpClient.send(request, responseBodyHandler);
        assertEquals(StatusCodes.NOT_FOUND, result.statusCode());
        assertNotNull(result.body());
        final ErrorResponse errorResponse = objectMapper.readValue(result.body().toString(), ErrorResponse.class);
        assertEquals(errorResponse.getStatusCode(), NOT_FOUND);

    }

    @Test
    void transferNotEnoughMoney() throws IOException, InterruptedException {
        //create accounts
        final Account senderAccountDto = new Account(100L);
        final HttpRequest creationSenderRequest = HttpRequest.newBuilder()
                .uri(createAccountUri)
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(senderAccountDto)))
                .build();
        final HttpResponse creationSenderResponse = httpClient.send(creationSenderRequest, responseBodyHandler);
        final AccountFullInfo senderAccount = objectMapper.readValue(creationSenderResponse.body().toString(), AccountFullInfo.class);

        final Account receiverAccountDto = new Account(100L);
        final HttpRequest creationReceiverRequest = HttpRequest.newBuilder()
                .uri(createAccountUri)
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(receiverAccountDto)))
                .build();
        final HttpResponse creationReceiverResponse = httpClient.send(creationReceiverRequest, responseBodyHandler);
        final AccountFullInfo receiverAccount = objectMapper.readValue(creationReceiverResponse.body().toString(), AccountFullInfo.class);

        final MoneyTransfer moneyTransferDto = new MoneyTransfer(senderAccount.getId(), receiverAccount.getId(), 150L);
        //transfer money
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(transferUri)
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(moneyTransferDto)))
                .build();
        final HttpResponse result = httpClient.send(request, responseBodyHandler);
        assertEquals(StatusCodes.BAD_REQUEST, result.statusCode());
        assertNotNull(result.body());
        final ErrorResponse errorResponse = objectMapper.readValue(result.body().toString(), ErrorResponse.class);
        assertEquals(errorResponse.getStatusCode(), BAD_REQUEST);
    }
}
