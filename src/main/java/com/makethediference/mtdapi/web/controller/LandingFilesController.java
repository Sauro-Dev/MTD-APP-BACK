package com.makethediference.mtdapi.web.controller;

import com.makethediference.mtdapi.domain.entity.FileSector;
import com.makethediference.mtdapi.domain.entity.LandingFiles;
import com.makethediference.mtdapi.service.LandingFilesService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/landing-files")
@CrossOrigin("*")
public class LandingFilesController {

    private final LandingFilesService landingFilesService;
    private static final String UPLOAD_DIR = "uploads/landing_files/";
    private static final Set<String> ALLOWED_TYPES = Set.of("image/png", "image/jpeg", "image/webp", "application/pdf");




    @PostMapping("/register")
    @Transactional
    public ResponseEntity<LandingFiles> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("adminId") Long adminId,
            @RequestParam("fileSector") FileSector fileSector) {
        LandingFiles savedFile = landingFilesService.saveLandingFile(file, adminId, fileSector);
        return ResponseEntity.ok(savedFile);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> getLandingFileById(@PathVariable Long id) {
        Optional<LandingFiles> landingFileOpt = landingFilesService.getLandingFileById(id);
        if (landingFileOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        LandingFiles landingFile = landingFileOpt.get();
        try {
            Path filePath = Paths.get(UPLOAD_DIR).resolve(landingFile.getFileName()).normalize();
            UrlResource resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }
            // Extraer el nombre original eliminando el prefijo timestamp
            String originalName = landingFile.getFileName();
            if (originalName.contains("_")) {
                originalName = originalName.substring(originalName.indexOf("_") + 1);
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(landingFile.getFileTypes()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + originalName + "\"")
                    .body(resource);
        } catch (MalformedURLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }



    @GetMapping("/all")
    public ResponseEntity<List<LandingFiles>> getAllLandingFiles() {
        List<LandingFiles> files = landingFilesService.getAllLandingFiles();
        return ResponseEntity.ok(files);
    }


    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<LandingFiles> updateLandingFile(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        Optional<LandingFiles> updatedFile = landingFilesService.updateLandingFile(id, file);
        return updatedFile.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PatchMapping("/{id}/disable")
    @Transactional
    public ResponseEntity<Void> disableLandingFile(@PathVariable Long id) {
        boolean disabled = landingFilesService.disableLandingFile(id);
        return disabled ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
