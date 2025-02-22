package com.makethediference.mtdapi.service;

import com.makethediference.mtdapi.domain.dto.volunteer.ValidateVolunteer;
import com.makethediference.mtdapi.domain.dto.volunteer.VolunteerForm;
import com.makethediference.mtdapi.domain.entity.Volunteer;

import java.util.List;

public interface VolunteerService {
    void submitVolunteerForm(VolunteerForm form);
    List<Volunteer> getPendingVolunteers();
    void validateRequest(ValidateVolunteer dto);
}
