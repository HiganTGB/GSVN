package com.gsvn.productservice.client.fallback;



import com.gsvn.productservice.client.MediaClient;
import com.gsvn.productservice.common.ApiResponse;
import com.gsvn.productservice.exc.AppException;
import com.gsvn.productservice.exc.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class MediaClientFallbackFactory implements FallbackFactory<MediaClient> {

    @Override
    public MediaClient create(Throwable cause) {
        return new MediaClient() {

            @Override
            public ApiResponse<String> upload(MultipartFile file, String type, String id) {
                log.error("[CircuitBreaker OPEN/FALLBACK] Media Service upload failed for type: {}, id: {}. Cause: {}",
                        type, id, cause.getMessage());
                throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
            }

            @Override
            public ApiResponse<String> getPreviewUrl(String path) {
                log.warn("[CircuitBreaker OPEN/FALLBACK] Media Service getPreviewUrl failed for path: {}. Cause: {}",
                        path, cause.getMessage());
                return new ApiResponse<>("");
            }

            @Override
            public ApiResponse<Void> deleteFile(String path) {
                log.error("[CircuitBreaker OPEN/FALLBACK] Media Service deleteFile failed for path: {}. Cause: {}",
                        path, cause.getMessage());
                throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
            }

            @Override
            public ApiResponse<Map<String, String>> getPreviewUrls(List<String> paths) {
                log.warn("[CircuitBreaker OPEN/FALLBACK] Media Service getPreviewUrls failed for batch paths. Cause: {}",
                        cause.getMessage());
                return new ApiResponse<>(Collections.emptyMap());
            }
        };
    }
}