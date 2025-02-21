package com.makethediference.mtdapi.infra.repository;

import com.makethediference.mtdapi.domain.entity.Volunteer;
import com.makethediference.mtdapi.domain.entity.VolunteerStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VolunteerRepository extends JpaRepository<Volunteer, Long> {
    List<Volunteer> findByStatus(VolunteerStatus status);
    boolean existsByEmail(String email);
    boolean existsByDni(String dni);
    boolean existsByPhoneNumber(String phoneNumber);
}
