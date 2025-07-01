package com.makethediference.mtdapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.makethediference.mtdapi.domain.dto.volunteer.*;
import com.makethediference.mtdapi.domain.entity.EstimatedHours;
import com.makethediference.mtdapi.infra.security.JwtAuthFilter;
import com.makethediference.mtdapi.service.VolunteerService;
import com.makethediference.mtdapi.web.controller.VolunteerController;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(VolunteerController.class)
class VolunteerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private VolunteerService volunteerService;

    @MockBean
    private com.makethediference.mtdapi.service.auth.AuthService authService;

    // Mocks de seguridad requeridos por tu config
    @MockBean
    private JwtAuthFilter jwtAuthFilter;
    @MockBean
    private AuthenticationProvider authenticationProvider;
    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void testCreateVolunteerRequest() throws Exception {
        System.out.println("\n[TEST] testCreateVolunteerRequest: Debe crear una solicitud de voluntariado correctamente...");
        VolunteerForm form = new VolunteerForm(
                "Juan",
                "Pérez",
                "García",
                "12345678",
                "juan@mail.com",
                LocalDate.of(2000, 1, 1),
                "987654321",
                "+51",
                "Peru",
                "Lima",
                "Quiero ayudar",
                EstimatedHours.PLUS_TEN,
                1L
        );
        Mockito.doNothing().when(volunteerService).submitVolunteerForm(any(VolunteerForm.class));

        mockMvc.perform(post("/api/v1/volunteers/form")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isOk());
        System.out.println("[OK] testCreateVolunteerRequest pasó correctamente.");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetPendingVolunteers() throws Exception {
        System.out.println("\n[TEST] testGetPendingVolunteers: Debe listar solicitudes de voluntariado pendientes...");
        VolunteerPending pending = Mockito.mock(VolunteerPending.class);
        when(volunteerService.getPendingVolunteers()).thenReturn(List.of(pending));

        mockMvc.perform(get("/api/v1/volunteers/pending"))
                .andExpect(status().isOk());
        System.out.println("[OK] testGetPendingVolunteers pasó correctamente.");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetVolunteerByIdFound() throws Exception {
        System.out.println("\n[TEST] testGetVolunteerByIdFound: Debe obtener solicitud de voluntario por ID correctamente...");
        VolunteerPending pending = Mockito.mock(VolunteerPending.class);
        when(volunteerService.getVolunteerById(anyLong())).thenReturn(Optional.of(pending));

        mockMvc.perform(get("/api/v1/volunteers/pending/1"))
                .andExpect(status().isOk());
        System.out.println("[OK] testGetVolunteerByIdFound pasó correctamente.");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testValidateVolunteer() throws Exception {
        System.out.println("\n[TEST] testValidateVolunteer: Debe validar (aprobar/rechazar) una solicitud de voluntariado...");
        ValidateVolunteer dto = new ValidateVolunteer(1L, true, "Comentario");
        Mockito.doNothing().when(volunteerService).validateRequest(any(ValidateVolunteer.class));

        mockMvc.perform(put("/api/v1/volunteers/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
        System.out.println("[OK] testValidateVolunteer pasó correctamente.");
    }
}
