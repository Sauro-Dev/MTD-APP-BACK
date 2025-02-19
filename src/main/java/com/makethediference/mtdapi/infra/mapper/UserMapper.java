package com.makethediference.mtdapi.infra.mapper;

import com.makethediference.mtdapi.domain.dto.user.ListUser;
import com.makethediference.mtdapi.domain.dto.user.RegisterUser;
import com.makethediference.mtdapi.domain.entity.User;
import com.makethediference.mtdapi.service.UserFactory;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(RegisterUser dto) {

        User user = UserFactory.createUser(dto.role());

        user.setUserId(null);
        user.setUsername(dto.username());
        user.setPassword(dto.password());
        user.setRole(dto.role());
        user.setName(dto.name());
        user.setSurname(dto.surname());
        user.setDni(dto.dni());
        user.setEmail(dto.email());
        user.setAge(dto.age());
        user.setPhoneNumber(dto.phoneNumber());
        user.setCountry(dto.country());
        user.setRegion(dto.region());
        user.setMotivation(dto.motivation());
        return user;
    }

    public ListUser toDto(User user) {

        return new ListUser(
                user.getUserId(),
                user.getUsername(),
                user.getRole(),
                user.getName(),
                user.getSurname(),
                user.getDni(),
                user.getEmail(),
                user.getAge(),
                user.getPhoneNumber(),
                user.getCountry(),
                user.getRegion(),
                user.getMotivation()
        );
    }
}
