package com.makethediference.mtdapi.service;

import com.makethediference.mtdapi.domain.entity.*;

public class UserFactory {
    public static User createUser(Role role) {
        User user = switch (role) {
            case ADMIN -> new Admin();
            case MAKER -> new Maker();
            case COUNCIL -> new Council();
            case COORDINATOR -> new Coordinator();
            default -> throw new IllegalArgumentException("Rol no soportado: " + role);
        };

        user.setRole(role);
        return user;
    }
}

