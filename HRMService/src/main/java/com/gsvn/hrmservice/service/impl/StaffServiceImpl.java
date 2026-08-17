package com.gsvn.hrmservice.service.impl;

import com.gsvn.hrmservice.client.MediaClient;
import com.gsvn.hrmservice.client.UserServiceFeignClient;
import com.gsvn.hrmservice.common.ApiResponse;
import com.gsvn.hrmservice.common.PageResponse;
import com.gsvn.hrmservice.common.UploadType;
import com.gsvn.hrmservice.converter.StaffConverter;
import com.gsvn.hrmservice.converter.StaffSalaryConverter;
import com.gsvn.hrmservice.exc.AppException;
import com.gsvn.hrmservice.exc.DuplicateResourceException;
import com.gsvn.hrmservice.exc.ErrorCode;
import com.gsvn.hrmservice.mapper.StaffMapper;
import com.gsvn.hrmservice.mapper.StaffSalaryMapper;
import com.gsvn.hrmservice.model.dto.request.StaffCreateRequest;
import com.gsvn.hrmservice.model.dto.request.StaffRequest;
import com.gsvn.hrmservice.model.dto.response.StaffResponse;
import com.gsvn.hrmservice.model.entity.Staff;
import com.gsvn.hrmservice.model.entity.StaffSalary;
import com.gsvn.hrmservice.model.internal.SyncUserRequest;
import com.gsvn.hrmservice.model.internal.UserBaseRequest;
import com.gsvn.hrmservice.service.AuthenticationService;
import com.gsvn.hrmservice.service.BranchService;
import com.gsvn.hrmservice.service.PositionService;
import com.gsvn.hrmservice.service.StaffService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Service
public class StaffServiceImpl implements StaffService {

    private final StaffConverter converter;
    private final StaffSalaryConverter salaryConverter;
    private final MediaClient mediaClient;
    private final StaffMapper mapper;
    private final StaffSalaryMapper salaryMapper;
    private final AuthenticationService authenticationService;
    private final UserServiceFeignClient userServiceFeignClient;
    private final PositionService positionService;
    private final BranchService branchService; // Thêm BranchService để lấy thông tin chi nhánh

    @Transactional
    public StaffResponse create(StaffCreateRequest request) {
        if (mapper.existsByEmail(request.staffRequest().getEmail(), null)) {
            throw new DuplicateResourceException(ErrorCode.INVALID_REQUEST_BODY, "staffRequest.email");
        }
        if (mapper.existsByIdentityCard(request.staffRequest().getIdentityCard(), null)) {
            throw new DuplicateResourceException(ErrorCode.INVALID_REQUEST_BODY, "staffRequest.identityCard");
        }
        Staff entity = converter.toEntity(request.staffRequest());
        entity.setBaseSalary(request.salaryRequest().getBaseSalary());
        entity.setPositionId(request.salaryRequest().getPositionId());
        entity.setIsActive(true);

        if (request.createAccount()) {
            try {
                var response = userServiceFeignClient.create(UserBaseRequest
                        .builder()
                        .email(entity.getEmail())
                        .userName(entity.getFullName())
                        .verifier(true)
                        .phoneNumber(entity.getPhoneNumber())
                        .password(entity.getEmail() + entity.getPhoneNumber())
                        .build());
                entity.setUserId(response.result().getUserId());
            } catch (FeignException e) {
                throw new DuplicateResourceException(ErrorCode.INVALID_REQUEST_BODY, "staffRequest.email");
            }
        }

        mapper.insert(entity);
        StaffSalary salary = salaryConverter.toEntity(request.salaryRequest(), entity.getStaffId());
        salaryMapper.insert(salary);

        userServiceFeignClient.sync(entity.getUserId(), SyncUserRequest.builder()
                .email(entity.getEmail()).phoneNumber(entity.getPhoneNumber()).branchId(entity.getBranchId()).verifier(true).build()
        );

        var position = positionService.getById(entity.getPositionId());
        var branch = entity.getBranchId() != null ? branchService.getById(entity.getBranchId().longValue()) : null;

        return converter.toResponse(entity, position, branch);
    }

    @Transactional
    public StaffResponse update(Long id, StaffRequest request) {
        Staff entity = mapper.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        if (mapper.existsByEmail(request.getEmail(), id)) {
            throw new DuplicateResourceException(ErrorCode.INVALID_REQUEST_BODY, "email");
        }
        if (mapper.existsByIdentityCard(request.getIdentityCard(), id)) {
            throw new DuplicateResourceException(ErrorCode.INVALID_REQUEST_BODY, "identityCard");
        }

        converter.mapRequestToEntity(request, entity);
        mapper.update(entity);

        userServiceFeignClient.sync(entity.getUserId(), SyncUserRequest.builder()
                .userName(entity.getFullName())
                .email(entity.getEmail())
                .phoneNumber(entity.getPhoneNumber())
                .branchId(entity.getBranchId())
                .verifier(true)
                .build()
        );

        var position = positionService.getById(entity.getPositionId());
        var branch = entity.getBranchId() != null ? branchService.getById(entity.getBranchId().longValue()) : null;

        return converter.toResponse(entity, position, branch);
    }

    @Transactional
    public StaffResponse addAccountForStaff(Long id) {
        Staff entity = mapper.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        if (entity.getUserId() != null) throw new AppException(ErrorCode.NOT_ALLOW);

        var response = userServiceFeignClient.create(UserBaseRequest
                .builder()
                .email(entity.getEmail())
                .userName(entity.getFullName())
                .verifier(true)
                .phoneNumber(entity.getPhoneNumber())
                .password(entity.getEmail() + entity.getPhoneNumber())
                .build());
        entity.setUserId(response.result().getUserId());
        mapper.update(entity);

        var position = positionService.getById(entity.getPositionId());
        var branch = entity.getBranchId() != null ? branchService.getById(entity.getBranchId().longValue()) : null;

        return converter.toResponse(entity, position, branch);
    }

    public StaffResponse getById(Long id) {
        var staff = mapper.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        var position = positionService.getById(staff.getPositionId());
        var branch = staff.getBranchId() != null ? branchService.getById(staff.getBranchId().longValue()) : null;

        if (staff.getAvatarUrl() != null && !staff.getAvatarUrl().isBlank()) {
            var mediaResponse = mediaClient.getPreviewUrl(staff.getAvatarUrl());
            staff.setAvatarUrl(mediaResponse.result());
        }
        return converter.toResponse(staff, position, branch);
    }

    public void delete(Long id) {
        if (mapper.findById(id).isEmpty()) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        mapper.deleteById(id);
    }

    public StaffResponse getMyInfo() {
        var staffId = authenticationService.getStaffIdFromToken();
        return getById(staffId);
    }

    @Transactional
    public StaffResponse updateMyInfo(StaffRequest request) {
        var staffId = authenticationService.getStaffIdFromToken();
        var staff = mapper.findById(staffId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        staff.setEmail(request.getEmail());
        staff.setPhoneNumber(request.getPhoneNumber());
        staff.setDob(request.getDob());
        mapper.update(staff);
        return getById(staffId);
    }

    public List<StaffResponse> getActiveStaff() {
        var positionList = positionService.getAllPositions();
        var branchList = branchService.getAllBranches();
        return converter.toResponseList(mapper.findListByActive(true), positionList, branchList);
    }

    @Override
    public PageResponse<StaffResponse> searchStaffs(
            String keyword,
            Integer branchId, // Thay thế warehouseId thành branchId
            Integer positionId,
            String sortBy,
            String direction,
            int page,
            int size
    ) {
        page = Math.max(1, page);
        int offset = (page - 1) * size;

        String sortField = switch (sortBy != null ? sortBy : "") {
            case "id" -> "staff_id";
            case "name" -> "full_name";
            case "dob" -> "dob";
            default -> "created_at";
        };

        String sortOrder = "asc".equalsIgnoreCase(direction) ? "ASC" : "DESC";

        var list = mapper.findAdvanced(keyword, branchId, positionId, sortField, sortOrder, size, offset);
        var total = mapper.countAdvanced(keyword, branchId, positionId);

        var positionList = positionService.getAllPositions();
        var branchList = branchService.getAllBranches();

        List<StaffResponse> responses = converter.toResponseList(list, positionList, branchList);

        List<String> allPaths = responses.stream()
                .map(StaffResponse::getAvatarUrl)
                .filter(path -> path != null && !path.isBlank())
                .distinct()
                .toList();

        if (!allPaths.isEmpty()) {
            ApiResponse<Map<String, String>> mediaResponse = mediaClient.getPreviewUrls(allPaths);
            if (mediaResponse != null && mediaResponse.result() != null) {
                Map<String, String> urlMap = mediaResponse.result();
                responses.forEach(staff -> {
                    String signedUrl = urlMap.get(staff.getAvatarUrl());
                    if (signedUrl != null) {
                        staff.setAvatarUrl(signedUrl);
                    }
                });
            }
        }

        return PageResponse.of(responses, total, page, size);
    }

    @Override
    @Transactional
    public String uploadStaffAvatar(Long id, MultipartFile file) {
        validateImage(file);
        var staff = mapper.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        ApiResponse<String> response = mediaClient.upload(
                file,
                UploadType.STAFF_AVATAR.name().toLowerCase(),
                String.valueOf(id)
        );
        if (response == null || response.result() == null) {
            log.error("Media service returned empty response for staff: {}", id);
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }

        String oldPath = staff.getAvatarUrl();
        staff.setAvatarUrl(response.result());
        mapper.update(staff);

        if (oldPath != null && !oldPath.isBlank()) {
            mediaClient.deleteFile(oldPath);
        }
        return response.result();
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
    }
}