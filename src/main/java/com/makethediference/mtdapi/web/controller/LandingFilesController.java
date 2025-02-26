package com.makethediference.mtdapi.web.controller;

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


@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/landing-files")
public class LandingFilesController {

    private final LandingFilesService landingFilesService;
    private static final String UPLOAD_DIR = "uploads/landing_files/";


    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @PostMapping("/register")
    public ResponseEntity<LandingFiles> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("adminId") Long adminId) {
        LandingFiles savedFile = landingFilesService.saveLandingFile(file, adminId);
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
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(landingFile.getFileTypes()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body((Resource) resource);
        } catch (MalformedURLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<LandingFiles>> getAllLandingFiles() {
        List<LandingFiles> files = landingFilesService.getAllLandingFiles();
        return ResponseEntity.ok(files);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity<LandingFiles> updateLandingFile(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        Optional<LandingFiles> updatedFile = landingFilesService.updateLandingFile(id, file);
        return updatedFile.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @PatchMapping("/{id}/disable")
    public ResponseEntity<Void> disableLandingFile(@PathVariable Long id) {
        boolean disabled = landingFilesService.disableLandingFile(id);
        return disabled ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
