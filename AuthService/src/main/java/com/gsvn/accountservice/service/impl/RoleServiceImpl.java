package com.gsvn.accountservice.service.impl;



import com.gsvn.accountservice.service.RoleService;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import com.gsvn.accountservice.exc.AppException;
import com.gsvn.accountservice.exc.DuplicateResourceException;
import com.gsvn.accountservice.exc.ErrorCode;
import com.gsvn.accountservice.mapper.PermissionMapper;
import  com.gsvn.accountservice.mapper.RoleMapper;

import com.gsvn.accountservice.common.PageResponse;
import com.gsvn.accountservice.model.dto.request.RoleRequest;
import com.gsvn.accountservice.model.dto.response.PermissionResponse;
import com.gsvn.accountservice.model.dto.response.RoleResponse;

import com.gsvn.accountservice.model.entity.Role;
import com.gsvn.accountservice.model.entity.RolePermission;

import com.gsvn.accountservice.model.entity.User;
import com.gsvn.accountservice.model.entity.UserRole;
import com.gsvn.accountservice.repository.*;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleServiceImpl implements RoleService {
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;
    RolePermissionRepository rolePermissionRepository;
    UserRoleRepository userRoleRepository;
    UserRepository userRepository;

    StringRedisTemplate stringRedisTemplate;
    ObjectMapper objectMapper;

    String CACHE_ALL_ROLES = "gsvn:account:roles:all";
    String CACHE_ROLE_PERMS = "gsvn:account:roles:perms:";
    Duration CACHE_TTL = Duration.ofMinutes(30);
    private static final long REFRESH_RATE = 30 * 60 * 1000;

    public List<Role> getAll() {
        String json = stringRedisTemplate.opsForValue().get(CACHE_ALL_ROLES);
        if (json != null) {
            return objectMapper.readValue(json, new TypeReference<List<Role>>() {});
        }
        List<Role> roles = roleRepository.findAll();
        saveToCache(CACHE_ALL_ROLES, roles);

        return roles;
    }

    public Set<PermissionResponse> getRolePermissions(int id) {
        String key = CACHE_ROLE_PERMS + id;

        String json = stringRedisTemplate.opsForValue().get(key);
        if (json != null) {
            return objectMapper.readValue(json, new TypeReference<Set<PermissionResponse>>() {});
        }

        var rolePermissions = rolePermissionRepository.getAllByRoleId(id);
        Set<Integer> pIds = rolePermissions.stream()
                .map(RolePermission::getPermissionId)
                .collect(Collectors.toSet());

        Set<PermissionResponse> perms = permissionRepository.findAllById(pIds).stream()
                .map(PermissionMapper::TO_PERMISSION_RESPONSE)
                .collect(Collectors.toSet());

        saveToCache(key, perms);
        return perms;
    }
    public Set<String> getRolePermissionStrings(Integer roleId) {
        Set<PermissionResponse> perms = getRolePermissions(roleId);

        if (perms == null || perms.isEmpty()) {
            return Collections.emptySet();
        }
        return perms.stream()
                .map(PermissionResponse::name)
                .collect(Collectors.toSet());
    }

    @Transactional(rollbackOn = {Exception.class})
    public RoleResponse create(RoleRequest request) {
        boolean existsName=roleRepository.existsRoleByRoleName(request.roleName());
        if(existsName) throw new DuplicateResourceException(ErrorCode.INVALID_REQUEST_BODY,"roleName");

        var role = roleRepository.save(RoleMapper.TO_ROLE(request));

        updateRolePermissionsMapping(role.getRoleId(), request.permissionId());

        stringRedisTemplate.delete(CACHE_ALL_ROLES);

        var permissions = new HashSet<>(permissionRepository.findAllById(request.permissionId()));
        return RoleMapper.TO_ROLE_RESPONSE(role, permissions);
    }

    @Transactional(rollbackOn = {Exception.class})
    public RoleResponse update(RoleRequest request, int id) {
        if (!roleRepository.existsById(id))
            throw new AppException(ErrorCode.ITEM_NOT_EXISTED);
        boolean existsName = roleRepository.existsByRoleNameAndRoleIdNot(request.roleName(), id);
        if (existsName) {
            throw new DuplicateResourceException(ErrorCode.INVALID_REQUEST_BODY, "roleName");
        }
        var role = RoleMapper.TO_ROLE(request);
        role.setRoleId(id);

        rolePermissionRepository.deleteAllByRoleId(id);
        updateRolePermissionsMapping(id, request.permissionId());

        var savedRole = roleRepository.save(role);

        clearCache(id);

        var permissions = new HashSet<>(permissionRepository.findAllById(request.permissionId()));
        return RoleMapper.TO_ROLE_RESPONSE(savedRole, permissions);
    }

    @Transactional(rollbackOn = {Exception.class})
    public void delete(int id) {
        rolePermissionRepository.deleteAllByRoleId(id);
        roleRepository.deleteById(id);
        clearCache(id);
    }


    private void saveToCache(String key, Object data) {
        String json = objectMapper.writeValueAsString(data);
        stringRedisTemplate.opsForValue().set(key, json, CACHE_TTL);
    }

    private void clearCache(int roleId) {
        stringRedisTemplate.delete(CACHE_ALL_ROLES);
        stringRedisTemplate.delete(CACHE_ROLE_PERMS + roleId);
    }

    private void updateRolePermissionsMapping(int roleId, Set<Integer> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) return;
        List<RolePermission> mappings = permissionIds.stream()
                .map(pId -> RolePermission.builder()
                        .roleId(roleId)
                        .permissionId(pId)
                        .build())
                .collect(Collectors.toList());
        rolePermissionRepository.saveAll(mappings);
    }

    public List<RoleResponse> getRoles() {
        return getAll().stream()
                .map(x -> RoleMapper.TO_ROLE_RESPONSE(x, Collections.emptySet()))
                .collect(Collectors.toList());
    }
    public void warmUpCache() {
        log.info("Starting Cache Warm-up: Roles and Permissions");
        List<Role> roles = getAll();
        log.info("Successfully cached {} roles.", roles.size());

        for (Role role : roles) {
            Set<PermissionResponse> perms = getRolePermissions(role.getRoleId());
            log.info("Loaded {} permissions for Role: [{}]", perms.size(), role.getRoleName());
        }
        log.info("Cache Warm-up Completed Successfully");
    }

    @Scheduled(fixedRate = REFRESH_RATE)
    public void scheduledCacheRefresh() {
        log.info("Scheduled Cache Refresh Starting");
        try {
            this.warmUpCache();
            log.info("Scheduled Cache Refresh Completed Successfully");
        } catch (Exception e) {
            log.error("Scheduled Cache Refresh Failed: {}", e.getMessage());
        }
    }


    public Set<RoleResponse> getRolesByUserId(String userId) {
        log.info("Fetching roles for User ID: {}", userId);
        Set<Integer> userRoleIds = userRoleRepository.findByUserId(userId)
                .stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toSet());

        if (userRoleIds.isEmpty()) return Collections.emptySet();

        return roleRepository.findAllByRoleIdIn(userRoleIds).stream()
                .map(x -> RoleMapper.TO_ROLE_RESPONSE(x, null))
                .collect(Collectors.toSet());
    }

    @Transactional(rollbackOn = {Exception.class})
    public Set<RoleResponse> updateRolesByUserId(String userId, List<Integer> newRoleIds) {
        log.info("Updating roles for User ID: {}", userId);

        Set<Integer> currentRoleIds = userRoleRepository.findByUserId(userId)
                .stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toSet());


        Set<Integer> roleIdsToDelete = currentRoleIds.stream()
                .filter(id -> !newRoleIds.contains(id))
                .collect(Collectors.toSet());

        Set<Integer> roleIdsToInsert = newRoleIds.stream()
                .filter(id -> !currentRoleIds.contains(id))
                .collect(Collectors.toSet());

        if (!roleIdsToDelete.isEmpty()) {
            userRoleRepository.deleteByUserIdAndRoleIdIn(userId, roleIdsToDelete);
        }

        if (!roleIdsToInsert.isEmpty()) {
            List<UserRole> userRolesToInsert = roleIdsToInsert.stream()
                    .map(roleId -> new UserRole(userId, roleId, null))
                    .collect(Collectors.toList());
            userRoleRepository.saveAll(userRolesToInsert);
        }

        return getRolesByUserId(userId);
    }

    public String getMyRoles() {
        var context = SecurityContextHolder.getContext();
        String email = Objects.requireNonNull(context.getAuthentication()).getName();
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
            return user.getIsStaff() ? "STAFF" : "CUSTOMER";
        }catch (AppException ex)
        {
            return "CUSTOMER";
        }
    }
    public Set<String> getPermissionsByUserId(String userId) {
        Set<Integer> roleIds = userRoleRepository.findByUserId(userId)
                .stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toSet());

        if (roleIds.isEmpty()) return Collections.emptySet();

        return roleIds.stream()
                .flatMap(roleId -> getRolePermissions(roleId).stream())
                .map(PermissionResponse::name)
                .collect(Collectors.toSet());
    }
    public Set<String> getMyPermissions() {
        var context = SecurityContextHolder.getContext();
        String email = Objects.requireNonNull(context.getAuthentication()).getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return getPermissionsByUserId(user.getUserId());
    }

    public PageResponse<RoleResponse> searchRoles(String keyword, String sortBy, String direction, int page, int size) {

        Map<String, String> fieldMapping = Map.of(
                "createdAt", "createdAt",
                "updatedAt", "updatedAt",
                "name", "roleName",
                "id", "roleId"
        );

        String actualField = fieldMapping.getOrDefault(sortBy, sortBy);
        log.error(actualField);

        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(actualField).ascending()
                : Sort.by(actualField).descending();
        int pageIndex = (page > 0) ? page - 1 : 0;
        Pageable pageable = PageRequest.of(pageIndex , size, sort);

        Page<Role> rolePage;
        if (keyword != null && !keyword.isBlank()) {
            rolePage = roleRepository.findByRoleNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                    keyword, keyword, pageable);
        } else {
            rolePage = roleRepository.findAll(pageable);
        }

        var roleResponses = rolePage.getContent().stream()
                .map(role -> RoleMapper.TO_ROLE_RESPONSE(role,null))
                .toList();

        return PageResponse.of(roleResponses,rolePage.getTotalPages(),page,size);
    }
}