package com.makethediference.mtdapi.web.controller;

import com.makethediference.mtdapi.domain.dto.volunteer.ValidateVolunteer;
import com.makethediference.mtdapi.domain.dto.volunteer.VolunteerForm;
import com.makethediference.mtdapi.domain.entity.Volunteer;
import com.makethediference.mtdapi.service.VolunteerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/volunteers")
@RequiredArgsConstructor
public class VolunteerController {

    private final VolunteerService volunteerService;

    @PostMapping("/form")
    @Transactional
    @PreAuthorize("isAnonymous()")
    public ResponseEntity<String> createVolunteerRequest(@RequestBody @Valid VolunteerForm form) {
        volunteerService.submitVolunteerForm(form);
        return ResponseEntity.ok("Solicitud de voluntariado enviada correctamente.");
    }

    @GetMapping("/pending")
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Volunteer>> getPendingVolunteers() {
        List<Volunteer> pendingList = volunteerService.getPendingVolunteers();
        return ResponseEntity.ok(pendingList);
    }

    @PutMapping("/validate")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<String> validateVolunteer(@RequestBody @Valid ValidateVolunteer dto) {
        volunteerService.validateRequest(dto);
        return ResponseEntity.ok("Solicitud procesada. " +
                (dto.approved() ? "Se creó el usuario." : "Se rechazó la solicitud."));
    }
}
