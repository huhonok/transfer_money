package com.company.hometask.utils;

import com.company.hometask.service.transfer.entity.TransferEntity;
import com.company.hometask.web.dto.MoneyTransfer;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TransferMapper {

    TransferMapper INSTANCE = Mappers.getMapper(TransferMapper.class);

    TransferEntity dtoToEntity(MoneyTransfer dto);
}
