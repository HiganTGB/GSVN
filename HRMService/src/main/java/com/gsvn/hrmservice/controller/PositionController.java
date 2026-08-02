package com.gsvn.hrmservice.controller;

import com.gsvn.hrmservice.common.ApiResponse;
import com.gsvn.hrmservice.common.PageResponse;
import com.gsvn.hrmservice.model.dto.request.PositionRequest;
import com.gsvn.hrmservice.model.dto.response.PositionResponse;
import com.gsvn.hrmservice.service.PositionService;
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
public class PositionController {

    private final PositionService positionService;
    @GetMapping("/search")
    @PreAuthorize("hasAuthority('all') or hasAuthority('position_read')")
    public ApiResponse<PageResponse<PositionResponse>> getPage(
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(required = false, defaultValue = "positionId") String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") String direction,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        var result = positionService.getPage(keyword, sortBy, direction, page, size);
        return new ApiResponse<>(result);
    }

    @GetMapping
    public ApiResponse<List<PositionResponse>> getAll() {
        return new ApiResponse<>(positionService.getAllPositions());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('all') or hasAuthority('position_read')")
    public ApiResponse<PositionResponse> getById(@PathVariable Integer id) {
        return new ApiResponse<>(positionService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('all') or hasAuthority('position_create')")
    public ApiResponse<PositionResponse> create(@Valid @RequestBody PositionRequest request) {
        return new ApiResponse<>( positionService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('all') or hasAuthority('position_update')")
    public ApiResponse<PositionResponse> update(
            @PathVariable Integer id,
            @RequestBody @Valid PositionRequest request
    ) {
        return new ApiResponse<>( positionService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('all') or hasAuthority('position_delete')")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        positionService.delete(id);
        return new ApiResponse<>();
    }
}