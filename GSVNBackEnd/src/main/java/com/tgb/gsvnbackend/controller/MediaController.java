package com.tgb.gsvnbackend.controller;

import com.tgb.gsvnbackend.service.impl.MinioServiceImpl;
import io.minio.ListObjectsArgs;
import io.minio.Result;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/media")
public class MediaController {
    private final MinioServiceImpl minioService;

    @Value("${minio.sku-image-bucket-name}")
    private String skuImageBucketName;

    @Value("${minio.spu-image-bucket-name}")
    private String spuImageBucketName;

    @Value("${minio.spu-gallery-bucket-name}")
    private String spuGalleryBucketName;

    @Value("${minio.spu-video-bucket-name}")
    private String spuVideoBucketName;
    @Value("${minio.endpoint}")
    private String minioEndpoint;

    @Autowired
    public MediaController(MinioServiceImpl minioService) {
        this.minioService = minioService;
    }

    @PostMapping("/sku/{skuId}/image")
    public ResponseEntity<String> uploadSkuImage(@PathVariable Integer skuId, @RequestParam("file") MultipartFile file) {
        try {
            String objectName = String.format("sku/%d/%s", skuId, file.getOriginalFilename());
            minioService.uploadFile(file, objectName, skuImageBucketName);
            return ResponseEntity.ok("Tải lên ảnh SKU thành công: " + objectName);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi tải lên ảnh SKU: " + e.getMessage());
        }
    }

    @GetMapping("/sku/{skuId}/image/{imageName}")
    public ResponseEntity<byte[]> getSkuImage(@PathVariable Integer skuId, @PathVariable String imageName) {
        try {
            String objectName = String.format("sku/%d/%s", skuId, imageName);
            InputStream inputStream = minioService.getFile(objectName, skuImageBucketName);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(inputStream.readAllBytes());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping("/spu/{spuId}/image")
    public ResponseEntity<String> uploadSpuImage(@PathVariable Integer spuId, @RequestParam("file") MultipartFile file) {
        try {
            String objectName = String.format("spu/%d/image/%s", spuId, file.getOriginalFilename());
            minioService.uploadFile(file, objectName, spuImageBucketName);
            return ResponseEntity.ok("Tải lên ảnh SPU thành công: " + objectName);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi tải lên ảnh SPU: " + e.getMessage());
        }
    }

    @GetMapping("/spu/{spuId}/image/{imageName}")
    public ResponseEntity<byte[]> getSpuImage(@PathVariable Integer spuId, @PathVariable String imageName) {
        try {
            String objectName = String.format("spu/%d/image/%s", spuId, imageName);
            InputStream inputStream = minioService.getFile(objectName, spuImageBucketName);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(inputStream.readAllBytes());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping("/spu/{spuId}/gallery")
    public ResponseEntity<String> uploadSpuGallery(@PathVariable Integer spuId, @RequestParam("files") MultipartFile[] files) {
        try {
            for (MultipartFile file : files) {
                String objectName = String.format("spu/%d/gallery/%s", spuId, file.getOriginalFilename());
                minioService.uploadFile(file, objectName, spuGalleryBucketName);
            }
            return ResponseEntity.ok("Tải lên thư viện ảnh SPU thành công cho SPU ID: " + spuId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi tải lên thư viện ảnh SPU: " + e.getMessage());
        }
    }

    @GetMapping("/spu/{spuId}/gallery/{imageName}")
    public ResponseEntity<byte[]> getSpuGalleryImage(@PathVariable Integer spuId, @PathVariable String imageName) {
        try {
            String objectName = String.format("spu/%d/gallery/%s", spuId, imageName);
            InputStream inputStream = minioService.getFile(objectName, spuGalleryBucketName);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG) // Adjust content type based on your image type
                    .body(inputStream.readAllBytes());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    @GetMapping("/spu/{spuId}/gallery-images")
    public ResponseEntity<List<String>> getSpuGalleryImageLinks(@PathVariable Integer spuId) {
        List<String> galleryImageUrls = new ArrayList<>();
        String prefix = String.format("spu/%d/gallery/", spuId);

        try {
            Iterable<Result<Item>> results = minioService.listObjects(ListObjectsArgs.builder()
                    .bucket(spuGalleryBucketName)
                    .prefix(prefix)
                    .recursive(false)
                    .build());

            for (Result<Item> result : results) {
                Item item = result.get();
                String objectName = item.objectName();
                String imageUrl = String.format("%s/%s/%s", minioEndpoint, spuGalleryBucketName, objectName);
                galleryImageUrls.add(imageUrl);
            }
            return ResponseEntity.ok(galleryImageUrls);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @PostMapping("/spu/{spuId}/video")
    public ResponseEntity<String> uploadSpuVideo(@PathVariable Integer spuId, @RequestParam("file") MultipartFile file) {
        try {
            String objectName = String.format("spu/%d/video/%s", spuId, file.getOriginalFilename());
            minioService.uploadFile(file, objectName, spuVideoBucketName);
            return ResponseEntity.ok("Tải lên video SPU thành công: " + objectName);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi tải lên video SPU: " + e.getMessage());
        }
    }

    @GetMapping("/spu/{spuId}/video/{videoName}")
    public ResponseEntity<byte[]> getSpuVideo(@PathVariable Integer spuId, @PathVariable String videoName) {
        try {
            String objectName = String.format("spu/%d/video/%s", spuId, videoName);
            InputStream inputStream = minioService.getFile(objectName, spuVideoBucketName);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM) // Adjust content type based on your video type
                    .body(inputStream.readAllBytes());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
