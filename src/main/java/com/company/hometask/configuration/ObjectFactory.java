package com.company.hometask.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.company.hometask.repository.AccountRepository;
import com.company.hometask.repository.AccountRepositoryImpl;
import com.company.hometask.service.account.AccountService;
import com.company.hometask.service.account.AccountServiceImpl;
import com.company.hometask.service.transfer.TransferService;
import com.company.hometask.service.transfer.TransferServiceImpl;
import io.undertow.Undertow;

public class ObjectFactory {

    private AccountRepository accountRepository;
    private AccountService accountService;
    private TransferService transferService;
    private ObjectMapper objectMapper;

    private AccountRepository accountRepository() {
        if (accountRepository != null) {
            return accountRepository;
        }
        accountRepository = new AccountRepositoryImpl();
        return accountRepository;
    }

    private AccountService accountService() {
        if (accountService == null) {
            accountService = new AccountServiceImpl(accountRepository());
        }
        return accountService;
    }

    private TransferService transferService() {
        if (transferService == null) {
            transferService = new TransferServiceImpl(accountService());
        }
        return transferService;
    }

    private ObjectMapper objectMapper() {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
        }
        return objectMapper;
    }

    public Undertow httpServer(final String configFilePath) {
        final ApplicationPropertiesLoader properties = new ApplicationPropertiesLoader();
        final HttpServerConfiguration httpServerConfiguration = properties.getConfiguration(configFilePath);

        return Undertow.builder()
                .addHttpListener(httpServerConfiguration.getPort(), httpServerConfiguration.getHost())
                .setHandler(new HttpHandlerConfiguration(accountService(), transferService(), objectMapper()).getHttpHandler())
                .build();
    }
}
