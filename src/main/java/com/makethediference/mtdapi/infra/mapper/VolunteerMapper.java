package com.makethediference.mtdapi.infra.mapper;

import com.makethediference.mtdapi.domain.dto.volunteer.VolunteerForm;
import com.makethediference.mtdapi.domain.entity.Area;
import com.makethediference.mtdapi.domain.entity.Volunteer;
import com.makethediference.mtdapi.domain.entity.VolunteerStatus;
import com.makethediference.mtdapi.infra.repository.AreaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VolunteerMapper {

    private final AreaRepository areaRepository;

    public Volunteer toEntity(VolunteerForm form) {
        Area appliedArea = areaRepository.findById(form.areaId())
                .orElseThrow(() -> new IllegalArgumentException("Área no encontrada"));

        return new Volunteer(
                form.name(),
                form.paternalSurname(),
                form.maternalSurname(),
                form.dni(),
                form.email(),
                form.birthdate(),
                form.phoneNumber(),
                form.codeNumber(),
                form.country(),
                form.region(),
                form.motivation(),
                form.estimatedHours(),
                VolunteerStatus.PENDING,
                null,
                appliedArea
        );
    }
}
