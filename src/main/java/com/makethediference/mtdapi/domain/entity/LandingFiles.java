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
    private Long idLandingFiles;

    // Se usa para almacenar el content type, pero se podría renombrar o separar según convenga
    private String fileTypes;

    // Nuevo campo para almacenar el nombre del archivo (o ruta relativa)
    private String fileName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private Admin admin;
}
