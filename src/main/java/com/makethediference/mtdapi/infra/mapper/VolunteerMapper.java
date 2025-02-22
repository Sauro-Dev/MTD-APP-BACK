package com.makethediference.mtdapi.infra.mapper;

import com.makethediference.mtdapi.domain.dto.volunteer.VolunteerForm;
import com.makethediference.mtdapi.domain.entity.Volunteer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VolunteerMapper {

    public Volunteer toEntity(VolunteerForm form) {
        Volunteer request = new Volunteer();
        request.setName(form.name());
        request.setPaternalSurname(form.paternalSurname());
        request.setMaternalSurname(form.maternalSurname());
        request.setDni(form.dni());
        request.setEmail(form.email());
        request.setBirthdate(form.birthdate());
        request.setPhoneNumber(form.phoneNumber());
        request.setCodeNumber(form.codeNumber());
        request.setCountry(form.country());
        request.setRegion(form.region());
        request.setMotivation(form.motivation());
        request.setEstimatedHours(form.estimatedHours());

        return request;
    }
}
