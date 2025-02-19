package com.makethediference.mtdapi.domain.dto.user;

import com.makethediference.mtdapi.domain.entity.Role;

import java.time.LocalDate;

public record MyProfile(
        Long userId,
        String username,
        Role role,
        String name,
        String surname,
        String dni,
        String email,
        int age,
        LocalDate birthdate,
        String phoneNumber,
        String country,
        String region,
        String motivation
) {
}
