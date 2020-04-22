package com.company.hometask.service.transfer;

import com.company.hometask.service.transfer.entity.TransferEntity;
import com.company.hometask.service.account.AccountService;
import com.company.hometask.service.account.entity.AccountEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransferServiceImpl implements TransferService {
    private final AccountService accountService;
    private static final Logger log = LoggerFactory.getLogger(TransferServiceImpl.class);

    public TransferServiceImpl(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public TransferStatus transfer(TransferEntity transferEntity) {
        final String senderAccountId = transferEntity.getSenderAccountId();
        final String receiverAccountId = transferEntity.getReceiverAccountId();
        final Long amount = transferEntity.getAmount();
        final AccountEntity senderEntity = accountService.getAccount(senderAccountId);
        final AccountEntity receiverEntity = accountService.getAccount(receiverAccountId);

        if (senderEntity == null) {
            log.error("Account with id: {} was not found", senderAccountId);
            return TransferStatus.SENDER_NOT_FOUND;
        }
        if (receiverEntity == null) {
            log.error("Account with id: {} was not found", receiverAccountId);
            return TransferStatus.RECEIVER_NOT_FOUND;
        }

        AccountEntity first = senderEntity;
        AccountEntity second = receiverEntity;

        //sort entities by ID
        if (first.compareTo(second) < 0) {
            first = receiverEntity;
            second = senderEntity;
        }
        //always lock by first ID
        //for avoid a deadlock
        synchronized (first) {
            synchronized (second) {
                final Long senderBalance = senderEntity.getAmount();
                final Long receiverBalance = receiverEntity.getAmount();

                if (senderBalance - amount < 0) {
                    log.error("Sender does not have enough money for transfer");
                    return TransferStatus.NOT_ENOUGH_MONEY;
                }
                senderEntity.setAmount(senderBalance - amount);
                receiverEntity.setAmount(receiverBalance + amount);
                return TransferStatus.COMPLETED;
            }
        }

    }

}
