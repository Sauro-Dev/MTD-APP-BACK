package com.makethediference.mtdapi.domain.dto.user;

import java.time.LocalDate;

public record UpdateProfile(
        String name,
        String paternalSurname,
        String maternalSurname,
        String dni,
        String email,
        LocalDate birthdate,
        String phoneNumber,
        String country,
        String region,
        String motivation
) {
}
