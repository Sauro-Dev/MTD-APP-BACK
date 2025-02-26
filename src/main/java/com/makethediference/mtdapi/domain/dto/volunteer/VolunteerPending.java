package com.makethediference.mtdapi.domain.dto.volunteer;

import com.makethediference.mtdapi.domain.entity.VolunteerStatus;

public record VolunteerPending(
        Long userId,
        String name,
        String paternalSurname,
        String maternalSurname,
        String email,
        VolunteerStatus status
) {
}
