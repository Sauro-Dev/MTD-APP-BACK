package com.makethediference.mtdapi.service;

import com.makethediference.mtdapi.domain.dto.RegisterUser;
import com.makethediference.mtdapi.infra.security.LoginRequest;
import com.makethediference.mtdapi.infra.security.TokenResponse;

public interface UserService {
    TokenResponse login(LoginRequest request);
    TokenResponse addUser(RegisterUser usuario);
}
