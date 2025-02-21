package com.makethediference.mtdapi.domain.dto.user;

public record ValidateVolunteer(
        Long requestId,
        boolean approved,
        String adminComments
) {
}
