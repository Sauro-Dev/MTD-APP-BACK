package com.makethediference.mtdapi.domain.dto.user;

import com.makethediference.mtdapi.domain.entity.Role;

public record ListUser(
        Long id,
        String username,
        Role role,
        String name,
        String surname,
        String dni,
        String email,
        int age,
        String phoneNumber,
        String country,
        String region,
        String motivation
) {
}
