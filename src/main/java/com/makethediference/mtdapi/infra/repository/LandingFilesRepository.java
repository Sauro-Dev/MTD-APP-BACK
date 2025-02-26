package com.makethediference.mtdapi.infra.repository;

import com.makethediference.mtdapi.domain.entity.LandingFiles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LandingFilesRepository extends JpaRepository<LandingFiles, Long> {

    // Buscar archivos por tipo
    List<LandingFiles> findByFileTypes(String fileTypes);

    // Buscar archivos de un administrador en particular usando el campo 'userId'
    List<LandingFiles> findByAdminUserId(Long adminId);
}
