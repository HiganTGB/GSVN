package com.gsvn.hrmservice.controller;

import com.gsvn.hrmservice.common.ApiResponse;
import com.gsvn.hrmservice.common.PageResponse;
import com.gsvn.hrmservice.model.dto.request.PositionRequest;
import com.gsvn.hrmservice.model.dto.response.PositionResponse;
import com.gsvn.hrmservice.service.PositionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/positions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Position Management", description = "Endpoints for managing job positions and titles in the organization")
public class PositionController {

    private final PositionService positionService;

    @Operation(summary = "Search positions with pagination", description = "Retrieves a paginated list of job positions filtered by keyword with dynamic sorting.")
    @GetMapping("/search")
    @PreAuthorize("hasAuthority('all') or hasAuthority('position_read')")
    public ApiResponse<PageResponse<PositionResponse>> getPage(
            @Parameter(description = "Keyword to search by position title or description")
            @RequestParam(required = false, defaultValue = "") String keyword,

            @Parameter(description = "Field name to sort results by")
            @RequestParam(required = false, defaultValue = "positionId") String sortBy,

            @Parameter(description = "Sorting direction: 'ASC' or 'DESC'")
            @RequestParam(required = false, defaultValue = "DESC") String direction,

            @Parameter(description = "Page number (1-based index)")
            @RequestParam(required = false, defaultValue = "1") int page,

            @Parameter(description = "Number of items per page")
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        var result = positionService.getPage(keyword, sortBy, direction, page, size);
        return new ApiResponse<>(result);
    }

    @Operation(summary = "Get all positions", description = "Retrieves a complete list of all job positions without pagination.")
    @GetMapping
    public ApiResponse<List<PositionResponse>> getAll() {
        return new ApiResponse<>(positionService.getAllPositions());
    }

    @Operation(summary = "Get position by ID", description = "Retrieves detailed information of a specific job position by its ID.")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('all') or hasAuthority('position_read')")
    public ApiResponse<PositionResponse> getById(
            @Parameter(description = "ID of the position") @PathVariable Integer id) {
        return new ApiResponse<>(positionService.getById(id));
    }

    @Operation(summary = "Create position", description = "Creates a new job position record.")
    @PostMapping
    @PreAuthorize("hasAuthority('all') or hasAuthority('position_create')")
    public ApiResponse<PositionResponse> create(@Valid @RequestBody PositionRequest request) {
        return new ApiResponse<>(positionService.create(request));
    }

    @Operation(summary = "Update position", description = "Updates details of an existing job position by ID.")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('all') or hasAuthority('position_update')")
    public ApiResponse<PositionResponse> update(
            @Parameter(description = "ID of the position to update") @PathVariable Integer id,
            @RequestBody @Valid PositionRequest request
    ) {
        return new ApiResponse<>(positionService.update(id, request));
    }

    @Operation(summary = "Delete position", description = "Deletes a job position by ID.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('all') or hasAuthority('position_delete')")
    public ApiResponse<Void> delete(
            @Parameter(description = "ID of the position to delete") @PathVariable Integer id) {
        positionService.delete(id);
        return new ApiResponse<>();
    }
}