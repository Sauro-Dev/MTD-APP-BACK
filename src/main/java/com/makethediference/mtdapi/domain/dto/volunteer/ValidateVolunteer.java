package com.makethediference.mtdapi.domain.dto.volunteer;

import com.makethediference.mtdapi.domain.entity.Area;
import jakarta.validation.constraints.Null;

public record ValidateVolunteer(
        Long userId,
        boolean approved,
        String adminComments,
        Area appliedArea
) {
}
