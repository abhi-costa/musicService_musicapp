package com.apollo.musicService.service.impl;

import java.io.InputStream;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.apollo.musicService.service.MinioService;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.MinioException;

/**
 * Service to handle file uploads and deletions in MinIO.
 */
@Service
public class MinioServiceImpl implements MinioService {

    private static final Logger logger = LoggerFactory.getLogger(MinioServiceImpl.class);

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    @Value("${minio.url}")
    private String minioUrl; // e.g., http://localhost:9000

    public MinioServiceImpl(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    /**
     * Uploads a file to the configured MinIO bucket and returns the public URL.
     */
    @Override
    public String uploadFile(MultipartFile file) {
        try {
            String filename = UUID.randomUUID() + "-" + file.getOriginalFilename();
            logger.info("Uploading file '{}' to MinIO bucket '{}'", filename, bucket);

            // Ensure bucket exists
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
                logger.info("Created bucket '{}'", bucket);
            }

            try (InputStream inputStream = file.getInputStream()) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucket)
                                .object(filename)
                                .stream(inputStream, file.getSize(), -1)
                                .contentType(file.getContentType())
                                .build()
                );
            }

            String fileUrl = minioUrl + "/" + bucket + "/" + filename;
            logger.info("File uploaded successfully: {}", fileUrl);
            return fileUrl;

        } catch (MinioException e) {
            logger.error("MinIO error: {}", e.getMessage(), e);
            throw new RuntimeException("MinIO error: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("File upload failed: {}", e.getMessage(), e);
            throw new RuntimeException("File upload failed: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes a file from the configured MinIO bucket using its public URL.
     */
    @Override
    public void deleteFile(String fileUrl) {
        try {
            // Extract object name from URL
            String prefix = minioUrl + "/" + bucket + "/";
            if (!fileUrl.startsWith(prefix)) {
                throw new IllegalArgumentException("Invalid file URL: " + fileUrl);
            }
            String objectName = fileUrl.substring(prefix.length());

            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .build());

            logger.info("File deleted successfully from MinIO: {}", fileUrl);

        } catch (MinioException e) {
            logger.error("MinIO error while deleting file: {}", e.getMessage(), e);
            throw new RuntimeException("MinIO error while deleting file: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("File deletion failed: {}", e.getMessage(), e);
            throw new RuntimeException("File deletion failed: " + e.getMessage(), e);
        }
    }



    @Override
    public Resource getFile(String fileName) {
        try {
            InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket) // corrected field
                            .object(fileName)
                            .build()
            );
            return new InputStreamResource(stream);
        } catch (Exception e) {
            logger.error("Error retrieving file '{}': {}", fileName, e.getMessage());
            throw new RuntimeException("Error retrieving file: " + fileName, e);
        }
    }

    
    
}
