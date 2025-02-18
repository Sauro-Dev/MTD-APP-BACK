package com.makethediference.mtdapi.service.impl;

import com.makethediference.mtdapi.domain.dto.RegisterUser;
import com.makethediference.mtdapi.domain.entity.User;
import com.makethediference.mtdapi.infra.repository.UserRepository;
import com.makethediference.mtdapi.infra.security.JwtService;
import com.makethediference.mtdapi.infra.security.LoginRequest;
import com.makethediference.mtdapi.infra.security.TokenResponse;
import com.makethediference.mtdapi.service.UserFactory;
import com.makethediference.mtdapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    public TokenResponse login(LoginRequest request) {
        SecurityContextHolder.clearContext();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con username: " + request.getUsername()));

        if (!user.isEnabled()) {
            throw new DisabledException("Este usuario ha sido deshabilitado.");
        }

        String token = jwtService.getToken((UserDetails) authentication.getPrincipal(), user);


        boolean isFirstLogin = user.isFirstLogin();

        if (isFirstLogin) {
            user.setFirstLogin(false);
            userRepository.save(user);
        }

        return TokenResponse.builder()
                .token(token)
                .firstLogin(isFirstLogin)
                .build();
    }

    @Override
    public TokenResponse addUser(RegisterUser data) {
        if (userRepository.existsByUsername(data.username())) {
            throw new IllegalArgumentException("El username ya está en uso.");
        }
        if (userRepository.existsByEmail(data.email())) {
            throw new IllegalArgumentException("El email ya está en uso.");
        }
        if (userRepository.existsByDni(data.dni())) {
            throw new IllegalArgumentException("El DNI ya está en uso.");
        }

        User user = UserFactory.createUser(data.role());

        user.setUsername(data.username());
        user.setPassword(passwordEncoder.encode(data.password()));
        user.setRole(data.role());
        user.setName(data.name());
        user.setSurname(data.surname());
        user.setDni(data.dni());
        user.setEmail(data.email());
        user.setAge(data.age());
        user.setPhoneNumber(data.phoneNumber());
        user.setCountry(data.country());
        user.setRegion(data.region());
        user.setMotivation(data.motivation());

        userRepository.save(user);

        String token = jwtService.getToken(user, user);
        return TokenResponse.builder()
                .token(token)
                .build();
    }
}
