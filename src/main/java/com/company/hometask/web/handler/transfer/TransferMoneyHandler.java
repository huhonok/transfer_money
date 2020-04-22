package com.company.hometask.web.handler.transfer;

import com.company.hometask.service.transfer.TransferService;
import com.company.hometask.service.transfer.TransferStatus;
import com.company.hometask.service.transfer.entity.TransferEntity;
import com.company.hometask.utils.TransferMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.company.hometask.web.dto.ErrorResponse;
import com.company.hometask.web.dto.MoneyTransfer;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

import static io.undertow.util.StatusCodes.*;

public class TransferMoneyHandler implements HttpHandler {
    private final TransferService transferService;
    private final ObjectMapper objectMapper;
    private static final Logger log = LoggerFactory.getLogger(TransferMoneyHandler.class);

    public TransferMoneyHandler(TransferService transferService,
                                ObjectMapper objectMapper) {
        this.transferService = transferService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        final InputStream inputStream = exchange.getInputStream();
        if (inputStream.available() == 0) {
            final ErrorResponse response = new ErrorResponse(BAD_REQUEST, "Body is empty");
            exchange.setStatusCode(BAD_REQUEST);
            exchange.getResponseSender().send(objectMapper.writeValueAsString(response));
            return;
        }
        final MoneyTransfer moneyTransfer;
        try {
            moneyTransfer = objectMapper.readValue(inputStream, MoneyTransfer.class);
        } catch (IOException e) {
            log.error("Error in handle http POST /transfers, cause: ", e);
            final ErrorResponse response = new ErrorResponse(BAD_REQUEST, "Error in parsing json");
            exchange.setStatusCode(BAD_REQUEST);
            exchange.getResponseSender().send(objectMapper.writeValueAsString(response));
            return;
        }
        final String receiverAccountId = moneyTransfer.getReceiverAccountId();
        final String senderAccountId = moneyTransfer.getSenderAccountId();
        final Long amount = moneyTransfer.getAmount();
        if (receiverAccountId == null || receiverAccountId.isEmpty()) {
            log.error("Error in handle http POST /transfers: receiverAccountId must be non empty!");
            final ErrorResponse response = new ErrorResponse(BAD_REQUEST, "ReceiverAccountId must be non empty");
            exchange.setStatusCode(BAD_REQUEST);
            exchange.getResponseSender().send(objectMapper.writeValueAsString(response));
            return;
        }
        if (senderAccountId == null || senderAccountId.isEmpty()) {
            log.error("Error in handle http POST /transfers: senderAccountId must be non empty!");
            final ErrorResponse response = new ErrorResponse(BAD_REQUEST, "SenderAccountId must be non empty");
            exchange.setStatusCode(BAD_REQUEST);
            exchange.getResponseSender().send(objectMapper.writeValueAsString(response));
            return;
        }
        if (receiverAccountId.equals(senderAccountId)) {
            log.error("Error in handle http POST /transfers: senderAccountId must be differed from receiverAccountId!");
            final ErrorResponse response =
                    new ErrorResponse(BAD_REQUEST, "SenderAccountId must be differed from receiverAccountId");
            exchange.setStatusCode(BAD_REQUEST);
            exchange.getResponseSender().send(objectMapper.writeValueAsString(response));
            return;
        }
        if (amount <= 0) {
            log.error("Error in handle http POST /transfers: amount must be greater 0!");
            final ErrorResponse response = new ErrorResponse(BAD_REQUEST, "Amount must be greater 0");
            exchange.setStatusCode(BAD_REQUEST);
            exchange.getResponseSender().send(objectMapper.writeValueAsString(response));
            return;
        }
        final TransferEntity transferEntity = TransferMapper.INSTANCE.dtoToEntity(moneyTransfer);
        final TransferStatus statusCode = transferService.transfer(transferEntity);
        statusCodeMapper(exchange, statusCode);
    }

    private void statusCodeMapper(final HttpServerExchange exchange,
                                  final TransferStatus statusCode) throws JsonProcessingException {
        ErrorResponse response;
        switch (statusCode) {
            case COMPLETED:
                exchange.setStatusCode(OK);
                return;
            case NOT_ENOUGH_MONEY:
                response = new ErrorResponse(BAD_REQUEST, "Sender does not have enough money for transfer");
                exchange.setStatusCode(BAD_REQUEST);
                exchange.getResponseSender().send(objectMapper.writeValueAsString(response));
                return;
            case SENDER_NOT_FOUND:
                response = new ErrorResponse(NOT_FOUND, "Sender was not found");
                exchange.setStatusCode(NOT_FOUND);
                exchange.getResponseSender().send(objectMapper.writeValueAsString(response));
                return;
            case RECEIVER_NOT_FOUND:
                response = new ErrorResponse(NOT_FOUND, "Receiver was not found");
                exchange.setStatusCode(NOT_FOUND);
                exchange.getResponseSender().send(objectMapper.writeValueAsString(response));
        }
    }
}
