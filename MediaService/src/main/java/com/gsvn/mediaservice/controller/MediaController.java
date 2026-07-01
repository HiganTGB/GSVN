package com.gsvn.mediaservice.controller;


import com.gsvn.mediaservice.common.ApiResponse;
import com.gsvn.mediaservice.common.UploadType;
import com.gsvn.mediaservice.service.MinioService;
import com.gsvn.mediaservice.exc.AppException;
import com.gsvn.mediaservice.exc.ErrorCode;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/media")
@RequiredArgsConstructor
@Slf4j
public class MediaController {

    private final MinioService minioService;

    @PostMapping("/upload")
    public ApiResponse<String> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") String type,
            @RequestParam("id") String id) {

        validateImage(file);

        UploadType uploadType = UploadType.fromString(type);
        String objectPath;

        if (uploadType.isRequireResize() && file.getContentType().startsWith("image/")) {
            objectPath = minioService.uploadAndResizeImage(
                    uploadType.getFolder(),
                    id,
                    file,
                    uploadType.getWidth(),
                    uploadType.getHeight()
            );
        } else {
            objectPath = minioService.uploadFile(uploadType.getFolder(), id, file,false);
        }

        return ApiResponse.<String>builder().result(objectPath).build();
    }

    @GetMapping("/preview")
    public ApiResponse<String> getPreviewUrl(@RequestParam("path") String path) {
        if (path == null || path.isBlank()) {
            return ApiResponse.<String>builder().result("").build();
        }

        String url = minioService.getPresignedUrl(path);
        return ApiResponse.<String>builder()
                .result(url)
                .build();
    }

    @DeleteMapping("/delete")
    public ApiResponse<Void> deleteFile(@RequestParam("path") String path) {
        minioService.deleteFile(path,false);
        return ApiResponse.<Void>builder()
                .message("File deleted successfully")
                .build();
    }
    @GetMapping("/preview-batch")
    public ApiResponse<Map<String, String>> getPreviewUrls(@RequestParam("paths") List<String> paths) {
        if (paths == null || paths.isEmpty()) {
            return ApiResponse.<Map<String, String>>builder()
                    .result(Collections.emptyMap())
                    .build();
        }

        Map<String, String> urls = minioService.getPresignedUrls(paths);

        return ApiResponse.<Map<String, String>>builder()
                .result(urls)
                .build();
    }
    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new AppException(ErrorCode.INVALID_REQUEST_BODY);
        }

        String contentType = file.getContentType();
        List<String> validTypes = List.of(MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE, "image/jpg");

        if (contentType == null || !validTypes.contains(contentType)) {
            throw new AppException(ErrorCode.INVALID_REQUEST_BODY);
        }
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new AppException(ErrorCode.PAYLOAD_TOO_LARGE);
        }
    }
}