package com.makethediference.mtdapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.makethediference.mtdapi.domain.dto.user.*;
import com.makethediference.mtdapi.domain.entity.EstimatedHours;
import com.makethediference.mtdapi.domain.entity.Role;
import com.makethediference.mtdapi.infra.security.JwtService;
import com.makethediference.mtdapi.infra.security.LoginRequest;
import com.makethediference.mtdapi.infra.security.TokenResponse;
import com.makethediference.mtdapi.service.UserService;
import com.makethediference.mtdapi.service.auth.AuthService;
import com.makethediference.mtdapi.web.controller.UserController;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtService jwtService;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public UserService userService() {
            return Mockito.mock(UserService.class);
        }
        @Bean
        public AuthService authService() {
            return Mockito.mock(AuthService.class);
        }
        @Bean
        public JwtService jwtService() {
            return Mockito.mock(JwtService.class);
        }
        @Bean
        public AuthenticationProvider authenticationProvider() {
            return Mockito.mock(AuthenticationProvider.class);
        }
        @Bean
        public UserDetailsService userDetailsService() {
            return Mockito.mock(UserDetailsService.class);
        }
    }

    @Test
    void testLogin() throws Exception {
        System.out.println("\n[TEST] testLogin: Verifica que login devuelva un token válido...");
        LoginRequest request = new LoginRequest("test@mail.com", "1234");
        TokenResponse response = TokenResponse.builder().token("tok").firstLogin(false).build();
        when(userService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("tok"));
        System.out.println("[OK] testLogin pasó correctamente.");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAddUser() throws Exception {
        System.out.println("\n[TEST] testAddUser: Registra un usuario y espera un token en la respuesta...");
        RegisterUser registerUser = new RegisterUser(
                "Juan", "Pérez", "García", "StrongPassword123", Role.MAKER, "12345678",
                "juan.perez@email.com", LocalDate.of(1995, 5, 15),
                "987654321", "+51", "Peru", "Lima", "Me motiva ayudar", EstimatedHours.PLUS_TEN
        );
        TokenResponse response = TokenResponse.builder().token("nuevoToken").build();
        when(userService.addUser(any(RegisterUser.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("nuevoToken"));
        System.out.println("[OK] testAddUser pasó correctamente.");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllUsers() throws Exception {
        System.out.println("\n[TEST] testGetAllUsers: Obtiene la lista de usuarios...");
        ListUser listUser = Mockito.mock(ListUser.class);
        when(userService.getAllUsers()).thenReturn(List.of(listUser));

        mockMvc.perform(get("/api/v1/users/all"))
                .andExpect(status().isOk());
        System.out.println("[OK] testGetAllUsers pasó correctamente.");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetUserById() throws Exception {
        System.out.println("\n[TEST] testGetUserById: Consulta usuario por ID...");
        ListUser user = Mockito.mock(ListUser.class);
        when(userService.getUserById(anyLong())).thenReturn(user);

        mockMvc.perform(get("/api/v1/users/select/1"))
                .andExpect(status().isOk());
        System.out.println("[OK] testGetUserById pasó correctamente.");
    }

    @Test
    @WithMockUser(username = "mail@mock.com")
    void testGetMyProfile() throws Exception {
        System.out.println("\n[TEST] testGetMyProfile: Obtiene el perfil del usuario autenticado...");
        MyProfile myProfile = Mockito.mock(MyProfile.class);
        when(userService.getMyProfile(anyString())).thenReturn(myProfile);

        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isOk());
        System.out.println("[OK] testGetMyProfile pasó correctamente.");
    }

    @Test
    @WithMockUser(username = "mail@mock.com")
    void testUpdateMyProfile() throws Exception {
        System.out.println("\n[TEST] testUpdateMyProfile: Actualiza el perfil del usuario autenticado...");
        UpdateProfile updateProfile = Mockito.mock(UpdateProfile.class);
        UpdateProfileResponse updateProfileResponse = Mockito.mock(UpdateProfileResponse.class);

        when(userService.updateMyProfile(anyString(), any(UpdateProfile.class)))
                .thenReturn(updateProfileResponse);

        mockMvc.perform(put("/api/v1/users/update/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateProfile)))
                .andExpect(status().isOk());
        System.out.println("[OK] testUpdateMyProfile pasó correctamente.");
    }
}