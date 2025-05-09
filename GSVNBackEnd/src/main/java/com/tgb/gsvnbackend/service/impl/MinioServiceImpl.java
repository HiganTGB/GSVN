package com.tgb.gsvnbackend.service.impl;
import com.tgb.gsvnbackend.service.MediaService;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class MinioServiceImpl implements MediaService {

    private final MinioClient minioClient;

    @Autowired
    public MinioServiceImpl(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public void uploadFile(MultipartFile file, String objectName, String bucketName) throws Exception {
        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(inputStream, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException
                 | InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException
                 | XmlParserException e) {
            throw new Exception(String.format("Lỗi khi tải lên file '%s' vào bucket '%s': %s", objectName, bucketName, e.getMessage()));
        }
    }

    public InputStream getFile(String objectName, String bucketName) throws Exception {
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException
                 | InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException
                 | XmlParserException e) {
            throw new Exception(String.format("Lỗi khi lấy file '%s' từ bucket '%s': %s", objectName, bucketName, e.getMessage()));
        }
    }
    public Iterable<Result<Item>> listObjects(ListObjectsArgs listObjectsArgs) throws Exception {
        try {
            return minioClient.listObjects(listObjectsArgs);
        } catch (Exception e) {
            throw new Exception(String.format("Lỗi khi liệt kê objects từ bucket '%s' với prefix '%s': %s",
                    listObjectsArgs.bucket(), listObjectsArgs.prefix(), e.getMessage()));
        }
    }

}