package com.makethediference.mtdapi.web.controller;

import com.makethediference.mtdapi.domain.dto.volunteer.ApiResponse;
import com.makethediference.mtdapi.domain.dto.volunteer.ValidateVolunteer;
import com.makethediference.mtdapi.domain.dto.volunteer.VolunteerForm;
import com.makethediference.mtdapi.domain.dto.volunteer.VolunteerPending;
import com.makethediference.mtdapi.service.VolunteerService;
import com.makethediference.mtdapi.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/volunteers")
@RequiredArgsConstructor
public class VolunteerController {

    private final VolunteerService volunteerService;
    private final AuthService authService;

    @PostMapping("/form")
    @Transactional
    @PreAuthorize("isAnonymous()")
    public ResponseEntity<ApiResponse> createVolunteerRequest(@RequestBody @Valid VolunteerForm form) {
        volunteerService.submitVolunteerForm(form);
        return ResponseEntity.ok(new ApiResponse("Solicitud de voluntariado enviada correctamente."));
    }

    @GetMapping("/pending")
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getPendingVolunteers() {
        List<VolunteerPending> pendingList = volunteerService.getPendingVolunteers();
        if (pendingList.isEmpty()) {
            return ResponseEntity.ok(Collections.singletonMap("message", "No hay voluntarios pendientes"));
        }
        return ResponseEntity.ok(pendingList);
    }

    @GetMapping("/{id}") //  Nuevo endpoint para obtener un voluntario por ID
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VolunteerPending> getVolunteerById(@PathVariable Long id) {
        Optional<VolunteerPending> volunteer = volunteerService.getVolunteerById(id);
        return volunteer.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/validate")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<String> validateVolunteer(@RequestBody @Valid ValidateVolunteer dto) {
        authService.authorizeValidateVolunteer();
        volunteerService.validateRequest(dto);
        return ResponseEntity.ok("Solicitud procesada. " +
                (dto.approved() ? "Se creó el usuario." : "Se rechazó la solicitud."));
    }
}
