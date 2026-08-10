package com.gsvn.productservice.client;


import com.gsvn.productservice.common.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@FeignClient(name = "media-service", path = "/api/v1/media")
public interface MediaClient {
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<String> upload(
            @RequestPart("file") MultipartFile file,
            @RequestParam("type") String type,
            @RequestParam("id") String id
    );

    @GetMapping("/preview")
    ApiResponse<String> getPreviewUrl(@RequestParam("path") String path);

    @DeleteMapping("/delete")
    ApiResponse<Void> deleteFile(@RequestParam("path") String path);

    @GetMapping("/preview-batch")
    ApiResponse<Map<String, String>> getPreviewUrls(@RequestParam("paths") List<String> paths);
}