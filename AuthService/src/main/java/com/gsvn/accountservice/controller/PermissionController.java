package com.gsvn.accountservice.controller;

import com.gsvn.accountservice.common.ApiResponse;
import com.gsvn.accountservice.model.dto.response.PermissionResponse;
import com.gsvn.accountservice.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Permission Management", description = "Endpoints for managing system permissions and authorization access")
@Slf4j
public class PermissionController {
    PermissionService permissionService;
    @GetMapping
    @Operation(summary = "Get all permissions", description = "Retrieves a list of all available permissions in the system.")
    ApiResponse<List<PermissionResponse>> getAll() {
        return new ApiResponse<>( permissionService.getAll());
    }
}