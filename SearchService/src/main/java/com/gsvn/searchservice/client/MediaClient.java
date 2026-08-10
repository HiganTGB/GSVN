package com.gsvn.searchservice.client;


import com.gsvn.searchservice.common.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@FeignClient(name = "media-service", path = "/api/v1/media")
public interface MediaClient {


    @GetMapping("/preview")
    ApiResponse<String> getPreviewUrl(@RequestParam("path") String path);

    @GetMapping("/preview-batch")
    ApiResponse<Map<String, String>> getPreviewUrls(@RequestParam("paths") List<String> paths);
}