package com.makethediference.mtdapi.service.impl;

import com.makethediference.mtdapi.domain.dto.user.*;
import com.makethediference.mtdapi.domain.entity.User;
import com.makethediference.mtdapi.infra.mapper.UserMapper;
import com.makethediference.mtdapi.infra.repository.UserRepository;
import com.makethediference.mtdapi.infra.security.JwtService;
import com.makethediference.mtdapi.infra.security.LoginRequest;
import com.makethediference.mtdapi.infra.security.TokenResponse;
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

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    public TokenResponse login(LoginRequest request) {
        SecurityContextHolder.clearContext();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + request.getEmail()));

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
        if (userRepository.existsByPhoneNumber(data.phoneNumber())) {
            throw new IllegalArgumentException("El teléfono ya está en uso.");
        }
        if (userRepository.existsByEmail(data.email())) {
            throw new IllegalArgumentException("El email ya está en uso.");
        }
        if (userRepository.existsByDni(data.dni())) {
            throw new IllegalArgumentException("El DNI ya está en uso.");
        }

        User user = userMapper.toEntity(data, data.role());
        String autoUsername = generateBaseUsername(
                data.name(),
                data.paternalSurname(),
                data.maternalSurname()
        );

        user.setUsername(autoUsername);
        user.setPassword(passwordEncoder.encode(data.password()));
        user.setFirstLogin(true);
        userRepository.save(user);

        String token = jwtService.getToken(user, user);
        return TokenResponse.builder()
                .token(token)
                .build();
    }

    @Override
    public List<ListUser> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ListUser getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
        return userMapper.toDto(user);
    }

    @Override
    public MyProfile getMyProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con username: " + email));
        return userMapper.toMyProfile(user);
    }

    @Override
    public UpdateProfileResponse updateMyProfile(String email, UpdateProfile updateProfileDto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));

        // Guardamos el email viejo para comparar luego
        String oldEmail = user.getEmail();

        // 2) Validar colisiones si cambiaron phone, email o dni
        if (!updateProfileDto.phoneNumber().equals(user.getPhoneNumber())) {
            if (userRepository.existsByPhoneNumber(updateProfileDto.phoneNumber())) {
                throw new IllegalArgumentException("El teléfono ya está en uso.");
            }
        }
        if (!updateProfileDto.email().equals(user.getEmail())) {
            if (userRepository.existsByEmail(updateProfileDto.email())) {
                throw new IllegalArgumentException("El email ya está en uso.");
            }
        }
        if (!updateProfileDto.dni().equals(user.getDni())) {
            if (userRepository.existsByDni(updateProfileDto.dni())) {
                throw new IllegalArgumentException("El DNI ya está en uso.");
            }
        }

        // 3) Actualiza la entidad y guarda
        userMapper.updateFromProfile(updateProfileDto, user);
        userRepository.save(user);

        // 4) Si el email cambió, generamos token nuevo
        boolean emailChanged = !oldEmail.equals(user.getEmail());
        String newToken = null;
        if (emailChanged) {
            // Generar token con el nuevo email
            newToken = jwtService.getToken(user, user);
        }

        // 5) Retornar la respuesta con UpdateProfile final y el token (si corresponde)
        UpdateProfile updatedDto = userMapper.toUpdateProfile(user);

        return new UpdateProfileResponse(updatedDto, newToken);
    }

    private String buildUsernameBase(String name, String paternalSurname, String maternalSurname) {
        if (name == null || name.isBlank()) {
            name = "X";
        }
        if (paternalSurname == null || paternalSurname.isBlank()) {
            paternalSurname = "Apellido";
        }
        if (maternalSurname == null || maternalSurname.isBlank()) {
            maternalSurname = "Apellido";
        }

        // Tomar la primera palabra del apellido paterno
        String[] paternoPartes = paternalSurname.trim().split("\\s+");
        String primerPaterno = paternoPartes[0];

        // Tomar la primera palabra del apellido materno
        String[] maternoPartes = maternalSurname.trim().split("\\s+");
        String primerMaterno = maternoPartes[0];  // "Yupanqui"

        // Inicial del nombre
        String inicialNombre = name.substring(0, 1);

        // Inicial del primerMaterno
        String inicialMaterno = primerMaterno.substring(0, 1);

        return (inicialNombre + primerPaterno + inicialMaterno).toLowerCase();
    }

    private String generateBaseUsername(String name, String paternalSurname, String maternalSurname) {
        // Construir la base
        String base = buildUsernameBase(name, paternalSurname, maternalSurname);

        // Iniciamos en 1
        int suffix = 1;
        String candidate;

        // Bucle para buscar un sufijo disponible
        do {
            candidate = base + suffix;
            if (!userRepository.existsByUsername(candidate)) {
                break;
            }
            suffix++;
        } while (true);

        return candidate;
    }
}
