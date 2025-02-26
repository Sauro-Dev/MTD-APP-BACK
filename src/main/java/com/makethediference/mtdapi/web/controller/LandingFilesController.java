package com.makethediference.mtdapi.web.controller;

import com.makethediference.mtdapi.domain.entity.FileSector;
import com.makethediference.mtdapi.domain.entity.LandingFiles;
import com.makethediference.mtdapi.service.LandingFilesService;
import com.makethediference.mtdapi.service.aws.S3Service;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/landing-files")
@CrossOrigin("*")
public class LandingFilesController {

    private final LandingFilesService landingFilesService;
    private static final Set<String> ALLOWED_TYPES = Set.of("image/png", "image/jpeg", "image/webp", "application/pdf");
    private final S3Service s3Service;

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
    public ResponseEntity<?> getLandingFileById(@PathVariable Long id) {
        Optional<LandingFiles> landingFileOpt = landingFilesService.getLandingFileById(id);
        if (landingFileOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        LandingFiles landingFile = landingFileOpt.get();
        String fileKey = landingFile.getFileName();

        // Validar que el objeto exista en S3
        if (!s3Service.doesObjectExist(fileKey)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("El recurso no se encuentra disponible");
        }

        URL presignedUrl = s3Service.generatePresignedUrl(fileKey);
        return ResponseEntity.ok(presignedUrl.toString());
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

    // probar conexion con S3
    @GetMapping("/buckets")
    public List<String> listBuckets() {
        return s3Service.listBuckets();
    }
}
