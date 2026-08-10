package com.gsvn.customerservice.controller;

import com.gsvn.customerservice.common.ApiResponse;
import com.gsvn.customerservice.common.PageResponse;
import com.gsvn.customerservice.model.dto.response.CustomerResponse;
import com.gsvn.customerservice.model.dto.request.CustomerRequest;
import com.gsvn.customerservice.service.impl.CustomerServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Customer Management", description = "Endpoints for managing customer profiles, self-service account lookups, and internal user-customer mapping")
public class CustomerController {

    CustomerServiceImpl customerService;

    @Operation(summary = "Create customer profile", description = "Creates a new customer profile record.")
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('customer_create'))")
    public ApiResponse<CustomerResponse> createCustomer(@RequestBody @Valid CustomerRequest request) {
        return new ApiResponse<>(customerService.create(request));
    }

    @Operation(summary = "Create customer mapped to user (Internal)", description = "Internal endpoint to create a customer profile linked to a specific authentication User ID.")
    @PostMapping("/internal/{userId}/byUser")
    public ApiResponse<CustomerResponse> createInternalCustomer(
            @RequestBody @Valid CustomerRequest request,
            @Parameter(description = "ID of the auth user account") @PathVariable String userId) {
        return new ApiResponse<>(customerService.createWithUser(request, userId));
    }

    @Operation(summary = "Update customer profile", description = "Updates details of an existing customer profile by customer ID.")
    @PutMapping("/{customerId}")
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('customer_update'))")
    public ApiResponse<CustomerResponse> updateCustomer(
            @Parameter(description = "ID of the customer") @PathVariable long customerId,
            @RequestBody @Valid CustomerRequest request) {
        return new ApiResponse<>(customerService.update(customerId, request));
    }

    @Operation(summary = "Get customer by ID", description = "Retrieves detailed profile information of a specific customer by ID.")
    @GetMapping("/{customerId}")
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('customer_read'))")
    public ApiResponse<CustomerResponse> getCustomer(
            @Parameter(description = "ID of the customer") @PathVariable long customerId) {
        return new ApiResponse<>(customerService.getById(customerId));
    }

    @Operation(summary = "Delete customer profile", description = "Deletes a customer profile record by ID.")
    @DeleteMapping("/{customerId}")
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('customer_delete'))")
    public ApiResponse<Void> deleteCustomer(
            @Parameter(description = "ID of the customer to delete") @PathVariable long customerId) {
        customerService.delete(customerId);
        return new ApiResponse<>();
    }

    @Operation(summary = "Get own customer profile", description = "Self-service endpoint to retrieve profile details for the currently authenticated customer.")
    @GetMapping("/my-info")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ApiResponse<CustomerResponse> getMyInfo() {
        return new ApiResponse<>(customerService.getMyInfo());
    }

    @Operation(summary = "Update own customer profile", description = "Self-service endpoint to update profile details for the currently authenticated customer.")
    @PutMapping("/my-info")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ApiResponse<CustomerResponse> updateMyInfo(@RequestBody @Valid CustomerRequest request) {
        return new ApiResponse<>(customerService.updateMyInfo(request));
    }

    @Operation(summary = "Search customers with pagination", description = "Retrieves a paginated list of customers filtered by keyword with dynamic sorting.")
    @GetMapping("/search")
    @PreAuthorize("hasAuthority('ROLE_STAFF') and (hasAuthority('all') or hasAuthority('customer_read'))")
    public ApiResponse<PageResponse<CustomerResponse>> search(
            @Parameter(description = "Keyword to search by customer name, phone, or email")
            @RequestParam(required = false) String keyword,

            @Parameter(description = "Field name to sort results by")
            @RequestParam(defaultValue = "id") String sortBy,

            @Parameter(description = "Sorting direction: 'asc' or 'desc'")
            @RequestParam(defaultValue = "desc") String direction,

            @Parameter(description = "Page number (1-based index)")
            @RequestParam(defaultValue = "1") int page,

            @Parameter(description = "Number of items per page")
            @RequestParam(defaultValue = "10") int size) {

        return new ApiResponse<>(customerService.search(
                keyword,
                sortBy,
                direction,
                page,
                size
        ));
    }
}