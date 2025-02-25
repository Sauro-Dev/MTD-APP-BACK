package com.makethediference.mtdapi.domain.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.Period;

@Entity(name = "Volunteer")
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "volunteers")
@PrimaryKeyJoinColumn(name = "user_id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Volunteer extends User{

    @Enumerated(EnumType.STRING)
    private VolunteerStatus status = VolunteerStatus.PENDING;
    private String adminComments;

    @ManyToOne
    @JoinColumn(name = "applied_area_id")
    private Area appliedArea;

    public Volunteer(
            String name,
            String paternalSurname,
            String maternalSurname,
            String dni,
            String email,
            LocalDate birthdate,
            String phoneNumber,
            String codeNumber,
            String country,
            String region,
            String motivation,
            EstimatedHours estimatedHours,
            VolunteerStatus status,
            String adminComments,
            Area appliedArea
    ) {
        super(
                null,
                null,
                null,
                Role.MAKER,
                name,
                paternalSurname,
                maternalSurname,
                dni,
                email,
                calculateAge(birthdate),
                birthdate,
                phoneNumber,
                codeNumber,
                country,
                region,
                motivation,
                estimatedHours,
                false,
                false,
                null
        );
        this.status = status;
        this.adminComments = adminComments;
        this.appliedArea = appliedArea;

        if (getAge() < 16 || getAge() > 100) {
            throw new IllegalArgumentException("La edad debe estar entre 16 y 100 años.");
        }
    }

    private static int calculateAge(LocalDate birthdate) {
        if (birthdate == null) return 0;
        return Period.between(birthdate, LocalDate.now()).getYears();
    }
}
