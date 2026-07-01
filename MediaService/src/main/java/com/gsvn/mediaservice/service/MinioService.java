package com.gsvn.mediaservice.service;

import com.gsvn.mediaservice.config.MinioConfig;
import com.gsvn.mediaservice.exc.AppException;
import com.gsvn.mediaservice.exc.ErrorCode;
import io.minio.*;
import io.minio.http.Method;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinioService {

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;
    @Value("${minio.bucket-name}")
    private String rootBucket;

    @Value("${minio.temp-bucket-name}")
    private String tempBucket;

    private static final int DEFAULT_EXPIRY_HOURS = 1;
    private static final String DEFAULT_IMAGE_FORMAT = "jpg";
    private static final String DEFAULT_CONTENT_TYPE = "image/jpeg";


    public String uploadFile(String folder, String id, MultipartFile file, boolean isTemporary) {
        String bucket = isTemporary ? tempBucket : rootBucket;
        String fileName = generateFileName(file.getOriginalFilename());
        String objectName = String.format("%s/%s/%s", folder, id, fileName);

        ensureBucketExists(bucket);

        try (InputStream is = file.getInputStream()) {
            putObjectToMinio(bucket, objectName, is, file.getSize(), file.getContentType());
            return objectName;
        } catch (Exception e) {
            log.error("Upload failed for object: {}", objectName, e);
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

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
                putObjectToMinio(rootBucket, objectName, inputStream, (long) data.length, DEFAULT_CONTENT_TYPE);
            }

            return objectName;
        } catch (Exception e) {
            log.error("Resize and Upload error for: {}", objectName, e);
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    public String getPresignedUrl(String objectName) {
        return getPresignedUrl(rootBucket, objectName);
    }

    public String getPresignedUrl(String bucket, String objectName) {
        try {
            String internalUrl = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucket)
                            .object(objectName)
                            .expiry(DEFAULT_EXPIRY_HOURS, TimeUnit.HOURS)
                            .build()
            );
            if (internalUrl != null && !minioConfig.getEndpoint().equals(minioConfig.getExternalUrl())) {
                return internalUrl.replace(minioConfig.getEndpoint(), minioConfig.getExternalUrl());
            }

            return internalUrl;
        } catch (Exception e) {
            log.error("Error generating presigned URL for: {}", objectName, e);
            return null;
        }
    }

    public Map<String, String> getPresignedUrls(List<String> paths) {
        return paths.stream()
                .filter(path -> path != null && !path.isBlank())
                .distinct()
                .collect(Collectors.toMap(path -> path, this::getPresignedUrl, (existing, replacement) -> existing));
    }

    public void deleteFile(String objectName, boolean isTemporary) {
        String bucket = isTemporary ? tempBucket : rootBucket;
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucket).object(objectName).build());
        } catch (Exception e) {
            log.error("Delete failed for object: {}", objectName, e);
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }


    private void ensureBucketExists(String bucketName) {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
        } catch (Exception e) {
            log.error("Minio Bucket checking/creation error", e);
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    private void putObjectToMinio(String bucket, String objectName, InputStream stream, Long size, String contentType) throws Exception {
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectName)
                        .stream(stream, size, -1)
                        .contentType(contentType)
                        .build()
        );
    }

    private String generateFileName(String originalName) {
        return System.currentTimeMillis() + "_" + (originalName != null ? originalName.replaceAll("\\s+", "_") : "unnamed");
    }
}