package com.makethediference.mtdapi;

import com.makethediference.mtdapi.domain.dto.volunteer.*;
import com.makethediference.mtdapi.domain.entity.*;
import com.makethediference.mtdapi.infra.mapper.VolunteerMapper;
import com.makethediference.mtdapi.infra.repository.UserRepository;
import com.makethediference.mtdapi.infra.repository.VolunteerRepository;
import com.makethediference.mtdapi.service.impl.EmailNotificationServiceImpl;
import com.makethediference.mtdapi.service.impl.UserNameGeneratorServiceImpl;
import com.makethediference.mtdapi.service.impl.VolunteerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VolunteerServiceImplTest {

    @Mock private VolunteerRepository volunteerRepository;
    @Mock private VolunteerMapper volunteerMapper;
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private UserNameGeneratorServiceImpl userNameGeneratorServiceImpl;
    @Mock private EmailNotificationServiceImpl emailNotificationServiceImpl;

    @InjectMocks
    private VolunteerServiceImpl volunteerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // --- submitVolunteerForm ---
    @Test
    void testSubmitVolunteerFormSuccess() {
        System.out.println("\n[TEST] testSubmitVolunteerFormSuccess: Debe guardar una solicitud de voluntario correctamente...");
        VolunteerForm form = mock(VolunteerForm.class);
        when(form.email()).thenReturn("email@vol.com");
        when(form.dni()).thenReturn("11111111");
        when(form.phoneNumber()).thenReturn("999999999");
        when(volunteerRepository.existsByEmail(anyString())).thenReturn(false);
        when(volunteerRepository.existsByDni(anyString())).thenReturn(false);
        when(volunteerRepository.existsByPhoneNumber(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(anyString())).thenReturn(false);
        when(userRepository.existsByDni(anyString())).thenReturn(false);

        Volunteer volunteer = mock(Volunteer.class);
        when(volunteerMapper.toEntity(form)).thenReturn(volunteer);

        volunteerService.submitVolunteerForm(form);
        verify(volunteerRepository).save(volunteer);
        System.out.println("[OK] testSubmitVolunteerFormSuccess pasó correctamente.");
    }

    // --- getPendingVolunteers ---
    @Test
    void testGetPendingVolunteers() {
        System.out.println("\n[TEST] testGetPendingVolunteers: Debe listar solicitudes pendientes correctamente...");
        Volunteer v = mock(Volunteer.class);
        VolunteerPending pending = mock(VolunteerPending.class);
        when(volunteerRepository.findByStatus(VolunteerStatus.PENDING)).thenReturn(List.of(v));
        when(volunteerMapper.toPending(v)).thenReturn(pending);

        List<VolunteerPending> result = volunteerService.getPendingVolunteers();
        assertEquals(1, result.size());
        verify(volunteerMapper).toPending(v);
        System.out.println("[OK] testGetPendingVolunteers pasó correctamente.");
    }

    // --- getVolunteerById ---
    @Test
    void testGetVolunteerByIdFoundPending() {
        System.out.println("\n[TEST] testGetVolunteerByIdFoundPending: Debe retornar voluntario pendiente por ID...");
        Volunteer v = mock(Volunteer.class);
        when(v.getStatus()).thenReturn(VolunteerStatus.PENDING);
        VolunteerPending pending = mock(VolunteerPending.class);
        when(volunteerRepository.findById(1L)).thenReturn(Optional.of(v));
        when(volunteerMapper.toPending(v)).thenReturn(pending);

        Optional<VolunteerPending> result = volunteerService.getVolunteerById(1L);
        assertTrue(result.isPresent());
        assertEquals(pending, result.get());
        System.out.println("[OK] testGetVolunteerByIdFoundPending pasó correctamente.");
    }

    @Test
    void testGetVolunteerByIdNotFound() {
        System.out.println("\n[TEST] testGetVolunteerByIdNotFound: Debe devolver vacío si no existe voluntario...");
        when(volunteerRepository.findById(2L)).thenReturn(Optional.empty());
        Optional<VolunteerPending> result = volunteerService.getVolunteerById(2L);
        assertTrue(result.isEmpty());
        System.out.println("[OK] testGetVolunteerByIdNotFound pasó correctamente.");
    }

    // --- validateRequest (Approved) ---
    @Test
    void testValidateRequestApproved() {
        System.out.println("\n[TEST] testValidateRequestApproved: Debe aprobar y registrar un voluntario correctamente...");
        ValidateVolunteer dto = mock(ValidateVolunteer.class);
        when(dto.userId()).thenReturn(1L);
        when(dto.adminComments()).thenReturn("OK");
        when(dto.approved()).thenReturn(true);

        Volunteer volunteer = mock(Volunteer.class);
        Area areaMock = mock(Area.class);
        when(volunteer.getAppliedArea()).thenReturn(areaMock);
        when(volunteer.getStatus()).thenReturn(VolunteerStatus.PENDING);
        when(volunteer.getName()).thenReturn("name");
        when(volunteer.getPaternalSurname()).thenReturn("pat");
        when(volunteer.getMaternalSurname()).thenReturn("mat");
        when(volunteer.getEmail()).thenReturn("v@vol.com");
        when(volunteer.getRole()).thenReturn(Role.MAKER);

        when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));
        when(userNameGeneratorServiceImpl.generateUsername(any(), any(), any())).thenReturn("userauto");
        when(passwordEncoder.encode(any())).thenReturn("passenc");

        volunteerService.validateRequest(dto);
        verify(volunteer).setAdminComments("OK");
        verify(volunteer).setUsername("userauto");
        verify(volunteer).setEnabled(true);
        verify(emailNotificationServiceImpl).sendVolunteerApprovalEmail(eq("v@vol.com"), eq("userauto"), any(), eq("MAKER"));
        verify(userRepository).save(volunteer);
        System.out.println("[OK] testValidateRequestApproved pasó correctamente.");
    }

    // --- validateRequest (Rejected) ---
    @Test
    void testValidateRequestRejected() {
        System.out.println("\n[TEST] testValidateRequestRejected: Debe rechazar y eliminar la solicitud correctamente...");
        ValidateVolunteer dto = mock(ValidateVolunteer.class);
        when(dto.userId()).thenReturn(1L);
        when(dto.adminComments()).thenReturn("NO");
        when(dto.approved()).thenReturn(false);

        Volunteer volunteer = mock(Volunteer.class);
        Area areaMock = mock(Area.class);
        when(volunteer.getAppliedArea()).thenReturn(areaMock);
        when(volunteer.getStatus()).thenReturn(VolunteerStatus.PENDING);

        when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));

        volunteerService.validateRequest(dto);
        verify(volunteerRepository).delete(volunteer);
        System.out.println("[OK] testValidateRequestRejected pasó correctamente.");
    }
}
