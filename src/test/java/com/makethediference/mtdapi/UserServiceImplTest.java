package com.makethediference.mtdapi;

import com.makethediference.mtdapi.domain.dto.user.*;
import com.makethediference.mtdapi.domain.entity.User;
import com.makethediference.mtdapi.infra.mapper.UserMapper;
import com.makethediference.mtdapi.infra.repository.UserRepository;
import com.makethediference.mtdapi.infra.security.JwtService;
import com.makethediference.mtdapi.infra.security.LoginRequest;
import com.makethediference.mtdapi.infra.security.TokenResponse;
import com.makethediference.mtdapi.service.cloudflare.d1.D1Service;
import com.makethediference.mtdapi.service.impl.UserNameGeneratorServiceImpl;
import com.makethediference.mtdapi.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private UserMapper userMapper;
    @Mock private JwtService jwtService;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private UserNameGeneratorServiceImpl userNameGeneratorServiceImpl;
    @Mock private D1Service d1Service;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // -------- LOGIN --------
    @Test
    void testLoginSuccess() {
        System.out.println("\n[TEST] testLoginSuccess: Verifica login exitoso y generación de token...");
        String email = "test@email.com";
        String password = "pass";
        LoginRequest request = new LoginRequest(email, password);

        Authentication auth = mock(Authentication.class);
        User user = mock(User.class);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(user.isEnabled()).thenReturn(true);
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(user);
        when(jwtService.getToken(any(), any())).thenReturn("token-test");
        when(user.isFirstLogin()).thenReturn(true);

        TokenResponse response = userService.login(request);

        assertEquals("token-test", response.getToken());
        Assertions.assertTrue(response.isFirstLogin());
        verify(userRepository).save(user);
        System.out.println("[OK] testLoginSuccess pasó correctamente.");
    }

    // -------- CREATE USER --------
    @Test
    void testAddUserSuccess() {
        System.out.println("\n[TEST] testAddUserSuccess: Crea usuario correctamente y genera token...");
        RegisterUser data = mock(RegisterUser.class);
        when(data.phoneNumber()).thenReturn("999999999");
        when(data.email()).thenReturn("email@test.com");
        when(data.dni()).thenReturn("11111111");
        when(userRepository.existsByPhoneNumber(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByDni(anyString())).thenReturn(false);

        User user = mock(User.class);
        when(userMapper.toEntity(any(), any())).thenReturn(user);
        when(userNameGeneratorServiceImpl.generateUsername(any(), any(), any())).thenReturn("usuarioauto");
        when(passwordEncoder.encode(any())).thenReturn("encrypted");
        when(jwtService.getToken(any(), any())).thenReturn("token-x");

        // Mockea los getters del usuario para que no sean null:
        when(user.getUsername()).thenReturn("usuarioauto");
        when(user.getEmail()).thenReturn("email@test.com");
        when(user.getPhoneNumber()).thenReturn("999999999");
        when(user.getDni()).thenReturn("11111111");

        TokenResponse response = userService.addUser(data);

        assertEquals("token-x", response.getToken());
        verify(userRepository).save(any(User.class));
        verify(d1Service).executeQuery(anyString(), anyList());
        System.out.println("[OK] testAddUserSuccess pasó correctamente.");
    }

    // -------- LIST USERS --------
    @Test
    void testGetAllUsers() {
        System.out.println("\n[TEST] testGetAllUsers: Retorna lista de usuarios correctamente...");
        User user = mock(User.class);
        ListUser listUser = mock(ListUser.class);
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toDto(any())).thenReturn(listUser);

        List<ListUser> result = userService.getAllUsers();

        assertEquals(1, result.size());
        verify(userMapper).toDto(user);
        System.out.println("[OK] testGetAllUsers pasó correctamente.");
    }

    // -------- GET USER BY ID --------
    @Test
    void testGetUserByIdFound() {
        System.out.println("\n[TEST] testGetUserByIdFound: Retorna usuario por ID correctamente...");
        User user = mock(User.class);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        ListUser dto = mock(ListUser.class);
        when(userMapper.toDto(user)).thenReturn(dto);

        ListUser found = userService.getUserById(1L);
        assertEquals(dto, found);
        System.out.println("[OK] testGetUserByIdFound pasó correctamente.");
    }

    @Test
    void testGetUserByIdNotFound() {
        System.out.println("\n[TEST] testGetUserByIdNotFound: Lanza excepción cuando no existe usuario con ese ID...");
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        Exception ex = Assertions.assertThrows(RuntimeException.class, () -> userService.getUserById(1L));
        Assertions.assertTrue(ex.getMessage().contains("Usuario no encontrado"));
        System.out.println("[OK] testGetUserByIdNotFound pasó correctamente.");
    }

    // -------- GET MY PROFILE --------
    @Test
    void testGetMyProfileFound() {
        System.out.println("\n[TEST] testGetMyProfileFound: Obtiene perfil correctamente usando email...");
        String email = "mail";
        User user = mock(User.class);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        MyProfile myProfile = mock(MyProfile.class);
        when(userMapper.toMyProfile(user)).thenReturn(myProfile);

        MyProfile res = userService.getMyProfile(email);
        assertEquals(myProfile, res);
        System.out.println("[OK] testGetMyProfileFound pasó correctamente.");
    }

    // -------- UPDATE PROFILE --------
    @Test
    void testUpdateMyProfileSuccess() {
        System.out.println("\n[TEST] testUpdateMyProfileSuccess: Actualiza el perfil del usuario correctamente...");
        String email = "mail";
        UpdateProfile update = mock(UpdateProfile.class);
        User user = mock(User.class);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(update.phoneNumber()).thenReturn("nuevofono");
        when(update.email()).thenReturn("nuevo@mail.com");
        when(update.dni()).thenReturn("nuevoDNI");
        when(user.getPhoneNumber()).thenReturn("old");
        when(user.getEmail()).thenReturn("old@mail.com");
        when(user.getDni()).thenReturn("oldDNI");
        when(userMapper.toUpdateProfile(user)).thenReturn(update);

        UpdateProfileResponse res = userService.updateMyProfile(email, update);
        Assertions.assertNotNull(res);
        verify(userRepository).save(user);
        System.out.println("[OK] testUpdateMyProfileSuccess pasó correctamente.");
    }

    // -------- GET TOKEN --------
    @Test
    void testGetTokenSuccess() {
        System.out.println("\n[TEST] testGetTokenSuccess: Genera token correctamente usando email...");
        String email = "mail";
        User user = mock(User.class);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(jwtService.getToken(user, user)).thenReturn("tok");

        TokenResponse res = userService.getToken(email);
        assertEquals("tok", res.getToken());
        System.out.println("[OK] testGetTokenSuccess pasó correctamente.");
    }
}
