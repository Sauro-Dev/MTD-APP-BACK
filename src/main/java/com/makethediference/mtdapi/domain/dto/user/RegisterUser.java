package com.makethediference.mtdapi.domain.dto.user;

import com.makethediference.mtdapi.domain.entity.Role;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

public record RegisterUser(
        @NotBlank @Length(max = 50) String username,
        @NotBlank @Length(max = 50) String password,
        @NotNull Role role,
        @NotBlank @Length(max = 25) String name,
        @NotBlank @Length(max = 25) String surname,
        @NotNull @Pattern(regexp = "\\d{8}") String dni,
        @NotBlank @Email @Length(max = 50) String email,
        @NotNull int age,
        @NotNull @Size(max = 9) String phoneNumber,
        @NotNull @Length(max = 50) String country,
        @NotNull @Length(max = 50) String region,
        @Null @Length(max = 200) String motivation
        )
{
}
