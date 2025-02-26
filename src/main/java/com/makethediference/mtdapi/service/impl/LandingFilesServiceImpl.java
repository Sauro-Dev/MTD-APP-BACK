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

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class LandingFilesServiceImpl implements LandingFilesService {

    private final LandingFilesRepository landingFilesRepository;
    private final UserRepository userRepository;

    private static final Set<String> ALLOWED_TYPES = Set.of("image/png", "image/jpeg", "image/webp", "application/pdf");
    private final S3Service s3Service;

    @Override
    public LandingFiles saveLandingFile(MultipartFile file, Long adminId, FileSector fileSector) {
        // Verificamos que el usuario sea un administrador
        User user = userRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        if (!(user instanceof Admin)) {
            throw new IllegalArgumentException("El usuario debe ser un administrador para subir archivos.");
        }

        // Subimos el archivo a S3
        String fileKey = s3Service.uploadFile(file);

        // Creamos la entidad y asignamos los datos
        LandingFiles landingFile = new LandingFiles();
        landingFile.setFileTypes(file.getContentType());
        landingFile.setFileName(fileKey);         // <-- Guardamos el "key" que devuelve S3
        landingFile.setFileSector(fileSector);
        landingFile.setAdmin((Admin) user);

        return landingFilesRepository.save(landingFile);
    }

    @Override
    public Optional<LandingFiles> updateLandingFile(Long id, MultipartFile file) {
        return landingFilesRepository.findById(id).map(existingFile -> {
            // Subir el nuevo archivo a S3 y actualizar la entidad
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
        // Para cada registro, si el objeto existe en S3, reemplazamos el fileName (que es el key)
        // por una URL firmada
        files.forEach(file -> {
            String s3Key = file.getFileName();
            if (s3Service.doesObjectExist(s3Key)) {
                URL presignedUrl = s3Service.generatePresignedUrl(s3Key);
                file.setFileName(presignedUrl.toString());
            } else {
                // Si el objeto ya no existe, puedes dejarlo en null o asignar un mensaje
                file.setFileName("El recurso no se encuentra disponible");
            }
        });
        return files;
    }

    @Override
    public boolean disableLandingFile(Long id) {
        return landingFilesRepository.findById(id).map(existingFile -> {
            // Se simula el deshabilitado marcando el archivo como "DISABLED"
            existingFile.setFileTypes("DISABLED");
            landingFilesRepository.save(existingFile);
            return true;
        }).orElse(false);
    }
}
