package com.ztp.ishop.mappers;

import com.ztp.ishop.dto.SignUpDto;
import com.ztp.ishop.dto.UserDTO;
import com.ztp.ishop.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toUserDto(User user);

    @Mapping(target = "password", ignore = true)
    User signUpToUser(SignUpDto signUpDto);

}