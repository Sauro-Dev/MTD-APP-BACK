package com.makethediference.mtdapi.infra.security;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenResponse {
    private String token;
    private boolean firstLogin;
}
