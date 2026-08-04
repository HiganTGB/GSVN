package com.gsvn.mediaservice.service.impl;

import com.gsvn.mediaservice.config.S3Config;
import com.gsvn.mediaservice.exc.AppException;
import com.gsvn.mediaservice.exc.ErrorCode;
import com.gsvn.mediaservice.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageServiceImpl implements ImageService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final S3Config s3Config;

    @Value("${s3.bucket-name}")
    private String rootBucket;

    @Value("${s3.temp-bucket-name}")
    private String tempBucket;

    private static final int DEFAULT_EXPIRY_HOURS = 1;
    private static final String DEFAULT_IMAGE_FORMAT = "jpg";
    private static final String DEFAULT_CONTENT_TYPE = "image/jpeg";

    @Override
    public String uploadFile(String folder, String id, MultipartFile file, boolean isTemporary) {
        String bucket = isTemporary ? tempBucket : rootBucket;
        String fileName = generateFileName(file.getOriginalFilename());
        String objectName = String.format("%s/%s/%s", folder, id, fileName);

        ensureBucketExists(bucket);

        try (InputStream is = file.getInputStream()) {
            putObjectToS3(bucket, objectName, is, file.getSize(), file.getContentType());
            return objectName;
        } catch (Exception e) {
            log.error("Upload failed for object: {}", objectName, e);
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    @Override
    public String uploadAndResizeImage(String folder, String id, MultipartFile file, int width, int height) {
        String fileName = generateFileName(file.getOriginalFilename());
        String objectName = String.format("%s/%s/%s", folder, id, fileName);

        ensureBucketExists(rootBucket);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Thumbnails.of(file.getInputStream())
                    .size(width, height)
                    .outputFormat(DEFAULT_IMAGE_FORMAT)
                    .outputQuality(0.8)
                    .toOutputStream(outputStream);

            byte[] data = outputStream.toByteArray();
            try (InputStream inputStream = new ByteArrayInputStream(data)) {
                putObjectToS3(rootBucket, objectName, inputStream, (long) data.length, DEFAULT_CONTENT_TYPE);
            }

            return objectName;
        } catch (Exception e) {
            log.error("Resize and Upload error for: {}", objectName, e);
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    @Override
    public String getPresignedUrl(String objectName) {
        return getPresignedUrl(rootBucket, objectName);
    }

    @Override
    public String getPresignedUrl(String bucket, String objectName) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(objectName)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofHours(DEFAULT_EXPIRY_HOURS))
                    .getObjectRequest(getObjectRequest)
                    .build();

            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
            String internalUrl = presignedRequest.url().toString();

            if (internalUrl != null && !s3Config.getEndpoint().equals(s3Config.getExternalUrl())) {
                return internalUrl.replace(s3Config.getEndpoint(), s3Config.getExternalUrl());
            }

            return internalUrl;
        } catch (Exception e) {
            log.error("Error generating presigned URL for: {}", objectName, e);
            return null;
        }
    }

    @Override
    public Map<String, String> getPresignedUrls(List<String> paths) {
        return paths.stream()
                .filter(path -> path != null && !path.isBlank())
                .distinct()
                .collect(Collectors.toMap(path -> path, this::getPresignedUrl, (existing, replacement) -> existing));
    }

    @Override
    public void deleteFile(String objectName, boolean isTemporary) {
        String bucket = isTemporary ? tempBucket : rootBucket;
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(objectName)
                    .build();
            s3Client.deleteObject(deleteObjectRequest);
        } catch (Exception e) {
            log.error("Delete failed for object: {}", objectName, e);
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    private void ensureBucketExists(String bucketName) {
        try {
            HeadBucketRequest headBucketRequest = HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build();
            s3Client.headBucket(headBucketRequest);
        } catch (NoSuchBucketException e) {
            try {
                CreateBucketRequest createBucketRequest = CreateBucketRequest.builder()
                        .bucket(bucketName)
                        .build();
                s3Client.createBucket(createBucketRequest);
            } catch (Exception ex) {
                log.error("Minio Bucket creation error", ex);
                throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
            }
        } catch (Exception e) {
            log.error("Minio Bucket checking error", e);
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    private void putObjectToS3(String bucket, String objectName, InputStream stream, Long size, String contentType) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(objectName)
                .contentType(contentType)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(stream, size));
    }

    private String generateFileName(String originalName) {
        return System.currentTimeMillis() + "_" + (originalName != null ? originalName.replaceAll("\\s+", "_") : "unnamed");
    }
}