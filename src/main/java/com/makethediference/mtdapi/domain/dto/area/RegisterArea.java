package com.makethediference.mtdapi.domain.dto.area;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record RegisterArea(
        @NotNull @Length(max = 50) String name,
        @NotNull @Length(max = 7) String color
) {
}
