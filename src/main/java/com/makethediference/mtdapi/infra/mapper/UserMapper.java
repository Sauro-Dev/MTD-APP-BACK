package com.makethediference.mtdapi.infra.mapper;

import com.makethediference.mtdapi.domain.dto.user.ListUser;
import com.makethediference.mtdapi.domain.dto.user.RegisterUser;
import com.makethediference.mtdapi.domain.entity.User;
import com.makethediference.mtdapi.service.UserFactory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring", uses = {UserFactory.class})
public interface UserMapper {
    @Mapping(source = "userId", target = "id")
    ListUser toDto(User user);

    default User toEntity(RegisterUser dto) {
        User user = UserFactory.createUser(dto.role());

        user.setUsername(dto.username());
        user.setPassword(dto.password());
        user.setName(dto.name());
        user.setSurname(dto.surname());
        user.setDni(dto.dni());
        user.setEmail(dto.email());
        user.setBirthday(dto.birthday());
        user.setPhoneNumber(dto.phoneNumber());
        user.setCountry(dto.country());
        user.setRegion(dto.region());
        user.setMotivation(dto.motivation());

        return user;
    }
}