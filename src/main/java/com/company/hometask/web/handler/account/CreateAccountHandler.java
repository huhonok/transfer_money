package com.company.hometask.web.handler.account;

import com.company.hometask.service.account.AccountService;
import com.company.hometask.service.account.entity.AccountEntity;
import com.company.hometask.utils.AccountMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.company.hometask.web.dto.Account;
import com.company.hometask.web.dto.AccountFullInfo;
import com.company.hometask.web.dto.ErrorResponse;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderMap;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

import static io.undertow.util.StatusCodes.BAD_REQUEST;


public class CreateAccountHandler implements HttpHandler {
    private final AccountService accountService;
    private final ObjectMapper objectMapper;
    private static final Logger log = LoggerFactory.getLogger(CreateAccountHandler.class);

    public CreateAccountHandler(AccountService accountService,
                                ObjectMapper objectMapper) {
        this.accountService = accountService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        final InputStream inputStream = exchange.getInputStream();
        if (inputStream.available() == 0) {
            log.error("Error in handle http POST /accounts: body is empty");
            final ErrorResponse response = new ErrorResponse(BAD_REQUEST, "Body is empty");
            exchange.setStatusCode(BAD_REQUEST);
            exchange.getResponseSender().send(objectMapper.writeValueAsString(response));
            return;
        }

        final Account account;
        try {
            account = objectMapper.readValue(inputStream, Account.class);
        } catch (IOException e) {
            log.error("Error in handle http POST /accounts, cause: ", e);
            final ErrorResponse response = new ErrorResponse(BAD_REQUEST, "Incorrect body");
            exchange.setStatusCode(BAD_REQUEST);
            exchange.getResponseSender().send(objectMapper.writeValueAsString(response));
            return;
        }

        if (account.getAmount() <= 0) {
            log.error("Error in handle http POST /accounts: Amount must be greater 0!");
            final ErrorResponse response = new ErrorResponse(BAD_REQUEST, "Amount must be greater 0!");
            exchange.setStatusCode(BAD_REQUEST);
            exchange.getResponseSender().send(objectMapper.writeValueAsString(response));
            return;
        }
        final AccountEntity accountEntity = AccountMapper.INSTANCE.dtoToEntity(account);
        final AccountEntity createdAccount = accountService.createAccount(accountEntity);
        final AccountFullInfo accountFullInfoDto = AccountMapper.INSTANCE.entityToDto(createdAccount);

        final HeaderMap responseHeaders = exchange.getResponseHeaders();
        responseHeaders.put(Headers.CONTENT_TYPE, "application/json");
        responseHeaders.put(Headers.LOCATION, "/accounts/" + accountFullInfoDto.getId());

        exchange.setStatusCode(StatusCodes.CREATED);
        exchange.getResponseSender().send(objectMapper.writeValueAsString(accountFullInfoDto));

    }

}
