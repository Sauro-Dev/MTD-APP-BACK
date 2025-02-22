package com.makethediference.mtdapi.domain.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity(name = "Volunteer")
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "volunteers", uniqueConstraints = {@UniqueConstraint(columnNames = {"`email`"})})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Volunteer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVolunteer;
    private String name;
    private String paternalSurname;
    private String maternalSurname;
    @Column(unique=true)
    private String dni;
    @Column(unique=true)
    private String email;
    private LocalDate birthdate;
    @Column(unique=true)
    private String phoneNumber;
    private String codeNumber;
    private String country;
    private String region;
    private String motivation;
    @Enumerated(EnumType.STRING)
    private EstimatedHours estimatedHours;
    @Enumerated(EnumType.STRING)
    private VolunteerStatus status = VolunteerStatus.PENDING;
    private String adminComments;
}
