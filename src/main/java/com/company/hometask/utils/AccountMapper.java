package com.company.hometask.utils;

import com.company.hometask.service.account.entity.AccountEntity;
import com.company.hometask.web.dto.Account;
import com.company.hometask.web.dto.AccountFullInfo;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountMapper {

    AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);

    AccountEntity dtoToEntity(Account dto);
    AccountFullInfo entityToDto(AccountEntity entity);
}
