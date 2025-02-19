package com.makethediference.mtdapi.web.controller;

import com.makethediference.mtdapi.domain.dto.user.ListUser;
import com.makethediference.mtdapi.domain.dto.user.RegisterUser;
import com.makethediference.mtdapi.infra.security.LoginRequest;
import com.makethediference.mtdapi.infra.security.TokenResponse;
import com.makethediference.mtdapi.service.UserService;
import com.makethediference.mtdapi.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/login")
    @Transactional
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register")
    @jakarta.transaction.Transactional
    public ResponseEntity<TokenResponse> addUser(@RequestBody @Valid RegisterUser data) {
        authService.authorizeRegisterUser();
        return ResponseEntity.ok(userService.addUser(data));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ListUser>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/select/{id}")
    public ResponseEntity<ListUser> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }
}
