package com.gsvn.hrmservice.controller;

import com.gsvn.hrmservice.common.ApiResponse;
import com.gsvn.hrmservice.common.PageResponse;
import com.gsvn.hrmservice.model.dto.request.BranchRequest;
import com.gsvn.hrmservice.model.dto.response.BranchResponse;
import com.gsvn.hrmservice.service.BranchService;
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
@RequestMapping("/api/v1/branches")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Branch Management", description = "Endpoints for managing company branches and locations")
public class BranchController {

    private final BranchService branchService;

    @Operation(summary = "Search branches with pagination", description = "Retrieves a paginated list of branches filtered by keyword with dynamic sorting.")
    @GetMapping("/search")
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('branch_read'))")
    public ApiResponse<PageResponse<BranchResponse>> getPage(
            @Parameter(description = "Keyword to search by branch code, name, or address")
            @RequestParam(required = false, defaultValue = "") String keyword,

            @Parameter(description = "Field name to sort results by")
            @RequestParam(required = false, defaultValue = "branchId") String sortBy,

            @Parameter(description = "Sorting direction: 'ASC' or 'DESC'")
            @RequestParam(required = false, defaultValue = "DESC") String direction,

            @Parameter(description = "Page number (1-based index)")
            @RequestParam(required = false, defaultValue = "1") int page,

            @Parameter(description = "Number of items per page")
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        var result = branchService.getPage(keyword, sortBy, direction, page, size);
        return new ApiResponse<>(result);
    }

    @Operation(summary = "Get all branches", description = "Retrieves a complete list of all branches without pagination.")
    @GetMapping
    public ApiResponse<List<BranchResponse>> getAll() {
        return new ApiResponse<>(branchService.getAllBranches());
    }

    @Operation(summary = "Get branch by ID", description = "Retrieves detailed information of a specific branch by its ID.")
    @GetMapping("/{id}")
    public ApiResponse<BranchResponse> getById(
            @Parameter(description = "ID of the branch") @PathVariable Long id) {
        return new ApiResponse<>(branchService.getById(id));
    }

    @Operation(summary = "Create branch", description = "Creates a new branch record.")
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('branch_create'))")
    public ApiResponse<BranchResponse> create(@Valid @RequestBody BranchRequest request) {
        return new ApiResponse<>(branchService.create(request));
    }

    @Operation(summary = "Update branch", description = "Updates details of an existing branch by ID.")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('branch_update'))")
    public ApiResponse<BranchResponse> update(
            @Parameter(description = "ID of the branch to update") @PathVariable Long id,
            @RequestBody @Valid BranchRequest request
    ) {
        return new ApiResponse<>(branchService.update(id, request));
    }

    @Operation(summary = "Delete branch", description = "Deletes a branch by ID.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('branch_delete'))")
    public ApiResponse<Void> delete(
            @Parameter(description = "ID of the branch to delete") @PathVariable Long id) {
        branchService.delete(id);
        return new ApiResponse<>();
    }

}