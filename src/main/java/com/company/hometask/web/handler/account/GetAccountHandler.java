package com.company.hometask.web.handler.account;

import com.company.hometask.service.account.AccountService;
import com.company.hometask.service.account.entity.AccountEntity;
import com.company.hometask.utils.AccountMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.company.hometask.web.dto.AccountFullInfo;
import com.company.hometask.web.dto.ErrorResponse;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Deque;
import java.util.Optional;

import static io.undertow.util.StatusCodes.*;

public class GetAccountHandler implements HttpHandler {
    private final AccountService accountService;
    private final ObjectMapper objectMapper;
    private static final Logger log = LoggerFactory.getLogger(GetAccountHandler.class);

    public GetAccountHandler(AccountService accountService,
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
            log.error("Error in handle http GET /accounts/{id}: accountId is empty!");
            final ErrorResponse response = new ErrorResponse(BAD_REQUEST, "AccountId is empty!");
            exchange.setStatusCode(BAD_REQUEST);
            exchange.getResponseSender().send(objectMapper.writeValueAsString(response));
            return;
        }
        final AccountEntity accountEntity = accountService.getAccount(accountId);

        if (accountEntity == null) {
            log.error("Error in handle http GET /accounts/{id}: account was not found!");
            final ErrorResponse response = new ErrorResponse(NOT_FOUND, "Account was not found!");
            exchange.setStatusCode(NOT_FOUND);
            exchange.getResponseSender().send(objectMapper.writeValueAsString(response));
            return;
        } else {
            exchange.setStatusCode(OK);
        }
        final AccountFullInfo accountFullInfoDto = AccountMapper.INSTANCE.entityToDto(accountEntity);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(objectMapper.writeValueAsString(accountFullInfoDto));
    }
}
