package com.makethediference.mtdapi.service;

import com.makethediference.mtdapi.domain.dto.user.ListUser;
import com.makethediference.mtdapi.domain.dto.user.RegisterUser;
import com.makethediference.mtdapi.infra.security.LoginRequest;
import com.makethediference.mtdapi.infra.security.TokenResponse;

import java.util.List;

public interface UserService {
    TokenResponse login(LoginRequest request);
    TokenResponse addUser(RegisterUser usuario);
    List<ListUser> getAllUsers();
    ListUser getUserById(Long id);
}
