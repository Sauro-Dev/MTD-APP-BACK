package com.makethediference.mtdapi.service.auth;

public interface AuthService {
    void authorizeRegisterUser();
    void authorizeRegisterPlaylist();
    void authorizeAdmin();
}
