package com.company.hometask.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.company.hometask.service.account.AccountService;
import com.company.hometask.service.transfer.TransferService;
import com.company.hometask.web.handler.account.CreateAccountHandler;
import com.company.hometask.web.handler.account.DeleteAccountHandler;
import com.company.hometask.web.handler.account.GetAccountHandler;
import com.company.hometask.web.handler.account.GetAccountsHandler;
import com.company.hometask.web.handler.transfer.TransferMoneyHandler;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.RoutingHandler;
import io.undertow.server.handlers.BlockingHandler;

import static io.undertow.util.StatusCodes.NOT_FOUND;

class HttpHandlerConfiguration {

    private final AccountService accountService;
    private final TransferService transferService;
    private final ObjectMapper objectMapper;

    HttpHandlerConfiguration(AccountService accountService,
                             TransferService transferService,
                             ObjectMapper objectMapper) {
        this.accountService = accountService;
        this.transferService = transferService;
        this.objectMapper = objectMapper;
    }

    HttpHandler getHttpHandler() {
        final RoutingHandler mainHandler = new RoutingHandler()
                .addAll(getAccountCrudHandler())
                .addAll(getTransferMoneyHandler())
                .setFallbackHandler(this::notFoundHandler);
        return new BlockingHandler(mainHandler);
    }

    private RoutingHandler getAccountCrudHandler() {
        return new RoutingHandler()
                .post("/accounts", exchange ->
                        new CreateAccountHandler(accountService, objectMapper).handleRequest(exchange))
                .delete("/accounts/{id}", exchange ->
                        new DeleteAccountHandler(accountService, objectMapper).handleRequest(exchange))
                .get("/accounts/{id}", exchange ->
                        new GetAccountHandler(accountService, objectMapper).handleRequest(exchange))
                .get("/accounts", exchange ->
                        new GetAccountsHandler(accountService, objectMapper).handleRequest(exchange))
                .get("/", exchange -> exchange.getResponseSender().send("It works!"));
    }

    private RoutingHandler getTransferMoneyHandler() {
        return new RoutingHandler()
                .post("/transfers", exchange ->
                        new TransferMoneyHandler(transferService, objectMapper).handleRequest(exchange));
    }

    private void notFoundHandler(HttpServerExchange exchange) {
        exchange.setStatusCode(NOT_FOUND);
        exchange.getResponseSender().send("Method is unsupported!");
    }
}
