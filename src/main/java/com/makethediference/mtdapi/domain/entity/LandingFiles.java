package com.makethediference.mtdapi.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "LandingFiles")
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "landingFiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LandingFiles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long idLandingFiles;
    String  fileTypes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private Admin admin;

}
