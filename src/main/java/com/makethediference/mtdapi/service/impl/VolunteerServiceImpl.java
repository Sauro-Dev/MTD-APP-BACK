package com.makethediference.mtdapi.service.impl;

import com.makethediference.mtdapi.domain.dto.user.ValidateVolunteer;
import com.makethediference.mtdapi.domain.dto.user.VolunteerForm;
import com.makethediference.mtdapi.domain.entity.User;
import com.makethediference.mtdapi.domain.entity.Volunteer;
import com.makethediference.mtdapi.domain.entity.VolunteerStatus;
import com.makethediference.mtdapi.infra.mapper.UserMapper;
import com.makethediference.mtdapi.infra.mapper.VolunteerMapper;
import com.makethediference.mtdapi.infra.repository.UserRepository;
import com.makethediference.mtdapi.infra.repository.VolunteerRepository;
import com.makethediference.mtdapi.service.VolunteerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VolunteerServiceImpl implements VolunteerService {

    private final VolunteerRepository volunteerRepository;
    private final VolunteerMapper volunteerMapper;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserNameGeneratorServiceImpl userNameGeneratorServiceImpl;
    private static final String ALPHANUMERIC_CHARS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int PASSWORD_LENGTH = 8;

    @Override
    public void submitVolunteerForm(VolunteerForm form) {
        if (volunteerRepository.existsByEmail(form.email())) {
            throw new IllegalArgumentException("El email ya está en uso en voluntarios pendientes.");
        }
        if (volunteerRepository.existsByDni(form.dni())) {
            throw new IllegalArgumentException("El DNI ya está en uso en voluntarios pendientes.");
        }
        if (volunteerRepository.existsByPhoneNumber(form.phoneNumber())) {
            throw new IllegalArgumentException("El teléfono ya está en uso en voluntarios pendientes.");
        }
        if (userRepository.existsByEmail(form.email())) {
            throw new IllegalArgumentException("El email de la solicitud ya esta registrado en un usuario del sistema");
        }
        if (userRepository.existsByPhoneNumber(form.phoneNumber())) {
            throw new IllegalArgumentException("El numero de la solicitud ya esta registrado en un usuario del sistema");
        }
        if (userRepository.existsByDni(form.dni())) {
            throw new IllegalArgumentException("El DNI de la solicitud ya esta  registrado en un usuario del sistema");
        }

        Volunteer request = volunteerMapper.toEntity(form);
        volunteerRepository.save(request);
    }

    @Override
    public List<Volunteer> getPendingVolunteers() {
        return volunteerRepository.findByStatus(VolunteerStatus.PENDING);
    }

    @Override
    public void validateRequest(ValidateVolunteer dto) {
        Volunteer request = volunteerRepository.findById(dto.requestId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró la solicitud con id=" + dto.requestId()));

        // Si la solicitud ya está APROBADA o RECHAZADA, decide si prohíbes la modificación
        if (request.getStatus() != VolunteerStatus.PENDING) {
            throw new IllegalStateException(
                    "La solicitud ya no está pendiente. Estado actual: " + request.getStatus());
        }

        request.setAdminComments(dto.adminComments());

        if (dto.approved()) {
            // 1) Cambia estado a APPROVED
            request.setStatus(VolunteerStatus.APPROVED);

            // 2) Crea un User con los datos del request
            createUserFromRequest(request);

        } else {
            // RECHAZADO
            request.setStatus(VolunteerStatus.REJECTED);
            // No creamos user
        }

        volunteerRepository.save(request);
    }

    private void createUserFromRequest(Volunteer req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Ya existe un user con el email " + req.getEmail());
        }
        if (userRepository.existsByDni(req.getDni())) {
            throw new IllegalArgumentException("Ya existe un user con el DNI " + req.getDni());
        }
        if (userRepository.existsByPhoneNumber(req.getPhoneNumber())) {
            throw new IllegalArgumentException("Ya existe un user con el teléfono " + req.getPhoneNumber());
        }

        User user = userMapper.fromVolunteerRequest(req);
        String autoUsername = userNameGeneratorServiceImpl.generateUsername(
                req.getName(),
                req.getPaternalSurname(),
                req.getMaternalSurname()
        );
        user.setUsername(autoUsername);
        String randomPassword = generateRandomPassword();

        user.setPassword(passwordEncoder.encode(randomPassword));
        user.setEnabled(true);
        user.setFirstLogin(true);

        userRepository.save(user);
    }

    private String generateRandomPassword() {
        StringBuilder sb = new StringBuilder();
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = random.nextInt(ALPHANUMERIC_CHARS.length());
            sb.append(ALPHANUMERIC_CHARS.charAt(index));
        }
        return sb.toString();
    }
}
