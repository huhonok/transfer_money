package com.company.hometask.service.transfer;

import com.company.hometask.service.transfer.entity.TransferEntity;

public interface TransferService {
    TransferStatus transfer(TransferEntity transferEntity);
}
