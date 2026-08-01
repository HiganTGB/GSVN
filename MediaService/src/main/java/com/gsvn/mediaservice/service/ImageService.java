package com.gsvn.mediaservice.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ImageService {
    String uploadFile(String folder, String id, MultipartFile file, boolean isTemporary);
    String uploadAndResizeImage(String folder, String id, MultipartFile file, int width, int height);
    String getPresignedUrl(String objectName);
    String getPresignedUrl(String bucket, String objectName);
    Map<String, String> getPresignedUrls(List<String> paths);
    void deleteFile(String objectName, boolean isTemporary);
}
