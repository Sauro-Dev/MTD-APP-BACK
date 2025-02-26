package com.makethediference.mtdapi.service.aws;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class S3Service {

    @Value("${cloud.aws.region}")
    private String awsRegion;

    @Value("${application.bucket.name}")
    private String bucketName;

    private final S3Client s3Client;

    public String uploadFile(MultipartFile file) {
        try {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromBytes(file.getBytes())
            );

            // Retorna el "key" del objeto, que luego podemos guardar en la DB
            return fileName;

        } catch (IOException e) {
            throw new RuntimeException("Error al subir archivo a S3", e);
        }
    }

    public URL generatePresignedUrl(String fileKey) {
        // Instanciar un S3Presigner (puede ser @Bean también)
        try (S3Presigner presigner = S3Presigner.builder()
                .region(Region.of(awsRegion))
                .build()) {

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .build();

            // Configurar tiempo de validez de la URL
            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(15))
                    .getObjectRequest(getObjectRequest)
                    .build();

            return presigner.presignGetObject(presignRequest).url();
        }
    }

    public void deleteFile(String fileKey) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .build();
        s3Client.deleteObject(deleteObjectRequest);
    }

    public boolean doesObjectExist(String fileKey) {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .build();
            HeadObjectResponse response = s3Client.headObject(headObjectRequest);
            return true;
        } catch (S3Exception e) {
            if (e.statusCode() == 404) {
                return false;
            }
            throw e; // Para otros errores, relanza la excepción
        }
    }

    public List<String> listBuckets() {
        return s3Client.listBuckets().buckets().stream()
                .map(bucket -> bucket.name())
                .collect(Collectors.toList());
    }


    // Otros métodos para listar archivos, etc.
}