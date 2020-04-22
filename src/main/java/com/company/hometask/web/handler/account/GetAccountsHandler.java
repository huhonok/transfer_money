package com.company.hometask.web.handler.account;

import com.company.hometask.service.account.AccountService;
import com.company.hometask.service.account.entity.AccountEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.company.hometask.web.dto.AccountFullInfo;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.util.List;
import java.util.stream.Collectors;

import static io.undertow.util.StatusCodes.NO_CONTENT;
import static io.undertow.util.StatusCodes.OK;

public class GetAccountsHandler implements HttpHandler {
    private final AccountService accountService;
    private final ObjectMapper objectMapper;

    public GetAccountsHandler(AccountService accountService,
                              ObjectMapper objectMapper) {
        this.accountService = accountService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        final List<AccountEntity> accountEntities = accountService.getAccounts();
        final var accounts = accountEntities
                .stream()
                .map(entity -> new AccountFullInfo(entity.getAmount(), entity.getId()))
                .collect(Collectors.toList());

        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        if (accounts.isEmpty()) {
            exchange.setStatusCode(NO_CONTENT);
        } else {
            exchange.setStatusCode(OK);
            exchange.getResponseSender().send(objectMapper.writeValueAsString(accounts));
        }
    }
}
