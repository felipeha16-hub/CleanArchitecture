package com.example.base.user.infrastructure.persistence.mapper;

import com.example.base.user.domain.model.User;
import com.example.base.user.infrastructure.persistence.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IUserMapper {

    // converts from entity to domain (to findById, findAll)
    User toDomain(UserEntity entity);

    // converts from domain to entity (to save)
    UserEntity toEntity(User user);
}