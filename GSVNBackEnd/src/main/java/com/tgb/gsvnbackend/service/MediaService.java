package com.tgb.gsvnbackend.service;

import io.minio.ListObjectsArgs;
import io.minio.Result;
import io.minio.messages.Item;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface MediaService {
    void uploadFile(MultipartFile file, String objectName, String bucketName) throws Exception;

    InputStream getFile(String objectName, String bucketName) throws Exception;

    Iterable<Result<Item>> listObjects(ListObjectsArgs listObjectsArgs) throws Exception;
}
