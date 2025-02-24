package com.makethediference.mtdapi.web.controller;

import com.makethediference.mtdapi.domain.dto.area.ListArea;
import com.makethediference.mtdapi.domain.dto.area.RegisterArea;
import com.makethediference.mtdapi.domain.entity.Area;
import com.makethediference.mtdapi.service.AreaService;
import com.makethediference.mtdapi.service.auth.AdminAuthService;
import com.makethediference.mtdapi.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/v1/areas")
@RequiredArgsConstructor
public class AreaController {

    private final AreaService areaService;
    private final AdminAuthService adminAuthService;
    private final AuthService authService;

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Area> registerArea(@Valid @RequestBody RegisterArea dto) {
        authService.authorizeRegisterArea();
        adminAuthService.authorizeAdmin();
        Area area = areaService.registerArea(dto);
        return ResponseEntity.ok(area);
    }

    @GetMapping("/all")
    @PreAuthorize("isAnonymous()")
    public ResponseEntity<List<ListArea>> getAllAreas() {
        return ResponseEntity.ok(areaService.getAllAreas());
    }
}
