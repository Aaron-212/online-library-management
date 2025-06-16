package com.aaron212.onlinelibrarymanagement.backend.mapper;

import com.aaron212.onlinelibrarymanagement.backend.dto.UserDto;
import com.aaron212.onlinelibrarymanagement.backend.dto.UserFullDto;
import com.aaron212.onlinelibrarymanagement.backend.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.sql.Timestamp;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDto toUserRecord(User user);

    UserFullDto toUserFullRecord(User user);

    default Long map(Timestamp value) {
        return value != null ? value.getTime() : null;
    }
}
