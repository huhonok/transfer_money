package com.company.hometask.service.transfer;

import com.company.hometask.service.account.AccountService;
import com.company.hometask.service.account.entity.AccountEntity;
import com.company.hometask.service.transfer.entity.TransferEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.company.hometask.service.transfer.TransferStatus.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {
    @Mock
    private AccountService accountService;
    @InjectMocks
    private TransferServiceImpl transferService;

    @BeforeEach
    void prepare() {
        Mockito.reset(accountService);
    }

    @Test
    void transferCompleted() {
        final TransferEntity transferEntity = new TransferEntity("id1", "id2", 100L);

        final AccountEntity senderEntity = new AccountEntity();
        senderEntity.setId("id1");
        senderEntity.setAmount(500L);

        final AccountEntity receiverEntity = new AccountEntity();
        receiverEntity.setId("id2");
        receiverEntity.setAmount(200L);

        when(accountService.getAccount("id1")).thenReturn(senderEntity);
        when(accountService.getAccount("id2")).thenReturn(receiverEntity);

        final TransferStatus status = transferService.transfer(transferEntity);
        assertNotNull(status);
        assertEquals(status, COMPLETED);
    }

    @Test
    void transferSenderAccountNotFound() {
        final TransferEntity transferEntity = new TransferEntity("id1", "id2", 100L);

        final AccountEntity receiverEntity = new AccountEntity();
        receiverEntity.setId("id2");
        receiverEntity.setAmount(200L);

        when(accountService.getAccount("id1")).thenReturn(null);
        when(accountService.getAccount("id2")).thenReturn(receiverEntity);

        final TransferStatus status = transferService.transfer(transferEntity);
        assertNotNull(status);
        assertEquals(status, SENDER_NOT_FOUND);
    }

    @Test
    void transferReceiverAccountNotFound() {
        final TransferEntity transferEntity = new TransferEntity("id1", "id2", 100L);

        final AccountEntity senderEntity = new AccountEntity();
        senderEntity.setId("id1");
        senderEntity.setAmount(500L);

        when(accountService.getAccount("id1")).thenReturn(senderEntity);
        when(accountService.getAccount("id2")).thenReturn(null);

        final TransferStatus status = transferService.transfer(transferEntity);
        assertNotNull(status);
        assertEquals(status, RECEIVER_NOT_FOUND);
    }

    @Test
    void transferSenderNotHasEnoughMoney() {
        final TransferEntity transferEntity = new TransferEntity("id1", "id2", 100L);
        final AccountEntity senderEntity = new AccountEntity();
        senderEntity.setId("id1");
        senderEntity.setAmount(50L);

        final AccountEntity receiverEntity = new AccountEntity();
        receiverEntity.setId("id2");
        receiverEntity.setAmount(200L);

        when(accountService.getAccount("id1")).thenReturn(senderEntity);
        when(accountService.getAccount("id2")).thenReturn(receiverEntity);
        final TransferStatus status = transferService.transfer(transferEntity);
        assertNotNull(status);
        assertEquals(status, NOT_ENOUGH_MONEY);
    }
}
