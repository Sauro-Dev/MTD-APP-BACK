package com.makethediference.mtdapi.service.impl;

import com.makethediference.mtdapi.domain.entity.Admin;
import com.makethediference.mtdapi.domain.entity.FileSector;
import com.makethediference.mtdapi.domain.entity.LandingFiles;
import com.makethediference.mtdapi.domain.entity.User;
import com.makethediference.mtdapi.infra.repository.LandingFilesRepository;
import com.makethediference.mtdapi.infra.repository.UserRepository;
import com.makethediference.mtdapi.service.LandingFilesService;
import com.makethediference.mtdapi.service.aws.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class LandingFilesServiceImpl implements LandingFilesService {

    private final LandingFilesRepository landingFilesRepository;
    private final UserRepository userRepository;

    private static final Set<String> ALLOWED_TYPES = Set.of("image/png", "image/jpeg", "image/jpg", "image/webp", "application/pdf");
    private final S3Service s3Service;

    @Override
    public LandingFiles saveLandingFile(MultipartFile file, Long adminId, FileSector fileSector, String makerName, String description) {

        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("Tipo de archivo no permitido. Solo se aceptan PNG, JPG, WEBP y PDF.");
        }

        if (fileSector == FileSector.HISTORY) {
            // Solo se permiten imágenes para HISTORY
            if (!Set.of("image/png", "image/jpeg", "image/jpg", "image/webp").contains(file.getContentType())) {
                throw new IllegalArgumentException("Solo se permiten imágenes para el sector HISTORY.");
            }
            // Verificar que no se haya cargado ya una imagen para HISTORY
            long historyCount = landingFilesRepository.countByFileSector(FileSector.HISTORY);
            if (historyCount >= 1) {
                throw new IllegalArgumentException("Ya existe una imagen para el sector HISTORY.");
            }
        }

        if (fileSector == FileSector.FEATURED_MAKER) {
            long makerCount = landingFilesRepository.countByFileSector(FileSector.FEATURED_MAKER);
            if (makerCount >= 10) {
                throw new IllegalArgumentException("No se pueden registrar más de 10 Makers Destacados.");
            }
        }

        User user = userRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        if (!(user instanceof Admin)) {
            throw new IllegalArgumentException("El usuario debe ser un administrador para subir archivos.");
        }

        String fileKey = s3Service.uploadFile(file);

        LandingFiles landingFile = new LandingFiles();
        landingFile.setFileTypes(file.getContentType());
        landingFile.setFileName(fileKey);
        landingFile.setFileSector(fileSector);
        landingFile.setAdmin((Admin) user);

        if (fileSector == FileSector.FEATURED_MAKER) {
            landingFile.setMakerName(makerName);
            landingFile.setDescription(description);
        }

        return landingFilesRepository.save(landingFile);
    }



    @Override
    public Optional<LandingFiles> updateLandingFile(Long id, MultipartFile file) {
        return landingFilesRepository.findById(id).map(existingFile -> {
            String newFileKey = s3Service.uploadFile(file);
            existingFile.setFileTypes(file.getContentType());
            existingFile.setFileName(newFileKey);
            return landingFilesRepository.save(existingFile);
        });
    }

    @Override
    public Optional<LandingFiles> getLandingFileById(Long id) {
        return landingFilesRepository.findById(id);
    }

    @Override
    public List<LandingFiles> getAllLandingFiles() {
        List<LandingFiles> files = landingFilesRepository.findAll();
        files.forEach(file -> {
            String s3Key = file.getFileName();
            if (s3Service.doesObjectExist(s3Key)) {
                file.setFileName(s3Service.getFileUrl(s3Key));
            } else {
                file.setFileName("El recurso no se encuentra disponible");
            }
        });
        return files;
    }

    @Override
    public boolean disableLandingFile(Long id) {
        return landingFilesRepository.findById(id).map(existingFile -> {
            existingFile.setFileTypes("DISABLED");
            landingFilesRepository.save(existingFile);
            return true;
        }).orElse(false);
    }
}
