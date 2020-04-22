package com.company.hometask.web.handler.account;

import com.company.hometask.service.account.AccountService;
import com.company.hometask.service.account.DeleteStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.company.hometask.web.dto.ErrorResponse;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Deque;
import java.util.Optional;

import static io.undertow.util.StatusCodes.*;

public class DeleteAccountHandler implements HttpHandler {
    private final AccountService accountService;
    private final ObjectMapper objectMapper;
    private static final Logger log = LoggerFactory.getLogger(DeleteAccountHandler.class);

    public DeleteAccountHandler(AccountService accountService,
                                ObjectMapper objectMapper) {
        this.accountService = accountService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {

        final String accountId = Optional
                .ofNullable(exchange.getQueryParameters().get("id"))
                .map(Deque::getFirst)
                .orElse(null);

        if (accountId == null || accountId.isEmpty()) {
            log.error("Error in handle http DELETE /accounts: accountId is empty!");
            final ErrorResponse response = new ErrorResponse(BAD_REQUEST, "AccountId is empty!");
            exchange.setStatusCode(BAD_REQUEST);
            exchange.getResponseSender().send(objectMapper.writeValueAsString(response));
            return;
        }
        final DeleteStatus statusCode = accountService.deleteAccount(accountId);
        statusCodeMapper(exchange, statusCode);

    }

    private void statusCodeMapper(final HttpServerExchange exchange,
                                  final DeleteStatus statusCode) throws JsonProcessingException {
        switch (statusCode) {
            case COMPLETED:
                exchange.setStatusCode(OK);
                return;
            case NOT_FOUND:
                final ErrorResponse response = new ErrorResponse(NOT_FOUND, "Account not found");
                exchange.setStatusCode(NOT_FOUND);
                exchange.getResponseSender().send(objectMapper.writeValueAsString(response));
        }

    }

}
