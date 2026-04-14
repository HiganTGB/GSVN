package com.gsvn.accountservice.service;


import com.gsvn.accountservice.client.CustomerServiceClient;
import com.gsvn.accountservice.exc.AppException;
import com.gsvn.accountservice.exc.ErrorCode;
import com.gsvn.accountservice.model.dto.request.*;
import com.gsvn.accountservice.model.dto.response.AuthenticationResponse;

import com.gsvn.accountservice.model.entity.*;
import com.gsvn.accountservice.model.internal.CustomerRequest;
import com.gsvn.accountservice.model.internal.IntrospectRequest;
import com.gsvn.accountservice.model.internal.IntrospectResponse;
import com.gsvn.accountservice.repository.RolePermissionRepository;
import com.gsvn.accountservice.repository.UserRepository;
import com.gsvn.accountservice.repository.UserRoleRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    UserRepository userRepository;
    UserRoleRepository userRoleRepository;
    TokenService tokenService;
    RoleService roleService;
    PasswordEncoder passwordEncoder;
    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long REFRESHABLE_DURATION;

    @NonFinal
    protected final String GRANT_TYPE = "authorization_code";

    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.token();
        boolean isValid = true;

        try {
            verifyToken(token, false);
        } catch (AppException e) {
            isValid = false;
        }

        return new IntrospectResponse(isValid);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // Find user
        User user = userRepository
                .findByEmail(request.email())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        // Check active or inactive User
        if (user.getDeletedAt()!=null) throw new AppException(ErrorCode.USER_NOT_EXISTED);
        if (!user.getIsActive()) throw new AppException(ErrorCode.USER_LOCKED);
        // Check Password
        boolean authenticated = passwordEncoder.matches(request.password(), user.getPassword());
        if (!authenticated) throw new AppException(ErrorCode.UNAUTHENTICATED);

        Set<Integer> roleIds = getRoleIdForUser(user);

        var token = generateToken(user, roleIds, VALID_DURATION);
        var refreshToken = generateToken(user, roleIds, REFRESHABLE_DURATION);

        tokenService.logoutAllDevices(user.getUserId());

        tokenService.saveToken(user.getUserId(),token,VALID_DURATION, TimeUnit.SECONDS);
        tokenService.saveToken(user.getUserId(),refreshToken,REFRESHABLE_DURATION, TimeUnit.SECONDS);

        return new AuthenticationResponse(token,refreshToken,true);
    }
    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        try {
            var signedJWT = verifyToken(request.token(), true);
            String jit = signedJWT.getJWTClaimsSet().getJWTID();
            String userId = signedJWT.getJWTClaimsSet().getStringClaim("userId");
            Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

            if (expiryTime != null) {
                long expirationRemainingMillis = expiryTime.getTime() - System.currentTimeMillis();
                if (expirationRemainingMillis > 0) {
                    tokenService.blacklistToken(userId, jit, expirationRemainingMillis);
                }
            }
        } catch (AppException exception) {
            log.info("Logout: Token is already invalid or expired, no action needed.");
        }
    }

    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
        var signedJWT = verifyToken(request.token(), true);
        var jit = signedJWT.getJWTClaimsSet().getJWTID();
        var userId = signedJWT.getJWTClaimsSet().getStringClaim("userId");

        long expirationRemainingMillis = signedJWT.getJWTClaimsSet().getExpirationTime().getTime() - System.currentTimeMillis();
        tokenService.blacklistToken(userId, jit, expirationRemainingMillis);

        var email = signedJWT.getJWTClaimsSet().getSubject();
        var user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        Set<Integer> roleIds = getRoleIdForUser(user);
        var newToken = generateToken(user, roleIds, VALID_DURATION);
        var newRefreshToken = generateToken(user, roleIds, REFRESHABLE_DURATION);
        tokenService.saveToken(user.getUserId(), newRefreshToken, REFRESHABLE_DURATION, TimeUnit.SECONDS);

        return new AuthenticationResponse(newToken, newRefreshToken, true);
    }

    private String generateToken(User user, Set<Integer> roleIds, long duration) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        //TODO: Check staff is admin
        var role = "STAFF";
        if(roleIds.contains(1))
            role = "ADMIN";
        if(!user.getIsStaff()) {
            role = "CUSTOMER";
        }
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issuer("gsvn.com")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(duration, ChronoUnit.SECONDS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("userId", user.getUserId())
                .claim("role", role)
                .claim("roleIds", roleIds)
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = (isRefresh)
                ? new Date(signedJWT
                .getJWTClaimsSet()
                .getIssueTime()
                .toInstant()
                .plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS)
                .toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        if (!(verified && expiryTime.after(new Date()))) throw new AppException(ErrorCode.UNAUTHENTICATED);

        String userId = signedJWT.getJWTClaimsSet().getStringClaim("userId");
        String jit = signedJWT.getJWTClaimsSet().getJWTID();

        if (tokenService.isTokenBlacklisted(userId, jit)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        return signedJWT;
    }
    private Set<Integer> getRoleIdForUser(User user) {
        return userRoleRepository.getAllByUserId(user.getUserId())
                .stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toSet());
    }
}