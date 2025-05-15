package com.tgb.gsvnbackend.service.impl;

import com.tgb.gsvnbackend.service.MediaService;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
@Slf4j
public class MinioServiceImpl implements MediaService {

    private final MinioClient minioClient;

    @Autowired
    public MinioServiceImpl(MinioClient minioClient) {
        this.minioClient = minioClient;
        log.info("MinioServiceImpl initialized.");
    }

    public void uploadFile(MultipartFile file, String objectName, String bucketName) throws Exception {
        log.info("Uploading file '{}' to bucket '{}' with object name '{}'", file.getOriginalFilename(), bucketName, objectName);
        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(inputStream, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
            log.info("File '{}' uploaded successfully to bucket '{}' as '{}'", file.getOriginalFilename(), bucketName, objectName);
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException
                 | InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException
                 | XmlParserException e) {
            log.error("Error occurred while uploading file '{}' to bucket '{}' as '{}': {}", file.getOriginalFilename(), bucketName, objectName, e.getMessage());
            throw new Exception(String.format("Error uploading file '%s' to bucket '%s': %s", objectName, bucketName, e.getMessage()));
        }
    }

    public InputStream getFile(String objectName, String bucketName) throws Exception {
        log.info("Getting file '{}' from bucket '{}'", objectName, bucketName);
        try {
            InputStream inputStream = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());
            log.info("File '{}' retrieved successfully from bucket '{}'", objectName, bucketName);
            return inputStream;
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException
                 | InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException
                 | XmlParserException e) {
            log.error("Error occurred while getting file '{}' from bucket '{}': {}", objectName, bucketName, e.getMessage());
            throw new Exception(String.format("Error getting file '%s' from bucket '%s': %s", objectName, bucketName, e.getMessage()));
        }
    }
    public Iterable<Result<Item>> listObjects(ListObjectsArgs listObjectsArgs) throws Exception {
        log.info("Listing objects from bucket '{}' with prefix '{}'", listObjectsArgs.bucket(), listObjectsArgs.prefix());
        try {
            Iterable<Result<Item>> results = minioClient.listObjects(listObjectsArgs);
            log.info("Successfully listed objects from bucket '{}' with prefix '{}'", listObjectsArgs.bucket(), listObjectsArgs.prefix());
            return results;
        } catch (Exception e) {
            log.error("Error occurred while listing objects from bucket '{}' with prefix '{}': {}", listObjectsArgs.bucket(), listObjectsArgs.prefix(), e.getMessage());
            throw new Exception(String.format("Error listing objects from bucket '%s' with prefix '%s': %s",
                    listObjectsArgs.bucket(), listObjectsArgs.prefix(), e.getMessage()));
        }
    }
}