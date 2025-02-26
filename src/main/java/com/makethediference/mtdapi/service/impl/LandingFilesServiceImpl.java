package com.makethediference.mtdapi.service.impl;

import com.makethediference.mtdapi.domain.entity.Admin;
import com.makethediference.mtdapi.domain.entity.LandingFiles;
import com.makethediference.mtdapi.domain.entity.User;
import com.makethediference.mtdapi.infra.repository.LandingFilesRepository;
import com.makethediference.mtdapi.infra.repository.UserRepository;
import com.makethediference.mtdapi.service.LandingFilesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class LandingFilesServiceImpl implements LandingFilesService {

    private final LandingFilesRepository landingFilesRepository;
    private final UserRepository userRepository;

    private static final Set<String> ALLOWED_TYPES = Set.of("image/png", "image/jpeg", "image/webp", "application/pdf");
    private static final String UPLOAD_DIR = "uploads/landing_files/";


    private String storeFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("El archivo no puede estar vacío.");
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("Formato no permitido. Solo PNG, JPG, WEBP y PDF.");
        }
        try {
            Path uploadPath = Path.of(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo.", e);
        }
    }

    @Override
    public LandingFiles saveLandingFile(MultipartFile file, Long adminId) {
        // Almacenamos el archivo y obtenemos el nombre guardado
        String storedFileName = storeFile(file);

        // Verificamos que el usuario sea un administrador
        User user = userRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        if (!(user instanceof Admin)) {
            throw new IllegalArgumentException("El usuario debe ser un administrador para subir archivos.");
        }

        // Creamos la entidad y asignamos el admin y el nombre del archivo
        LandingFiles landingFile = new LandingFiles();
        landingFile.setFileTypes(file.getContentType());
        landingFile.setFileName(storedFileName);
        landingFile.setAdmin((Admin) user);

        return landingFilesRepository.save(landingFile);
    }

    @Override
    public Optional<LandingFiles> updateLandingFile(Long id, MultipartFile file) {
        return landingFilesRepository.findById(id).map(existingFile -> {
            // Almacenamos el nuevo archivo y actualizamos la entidad
            String storedFileName = storeFile(file);
            existingFile.setFileTypes(file.getContentType());
            return landingFilesRepository.save(existingFile);
        });
    }

    @Override
    public Optional<LandingFiles> getLandingFileById(Long id) {
        return landingFilesRepository.findById(id);
    }

    @Override
    public List<LandingFiles> getAllLandingFiles() {
        return landingFilesRepository.findAll();
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
