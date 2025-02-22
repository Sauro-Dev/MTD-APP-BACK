package com.makethediference.mtdapi.domain.dto.volunteer;

public record ValidateVolunteer(
        Long requestId,
        boolean approved,
        String adminComments
) {
}
