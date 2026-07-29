package com.gsvn.accountservice.service.impl;


import com.gsvn.accountservice.client.NotificationClient;
import com.gsvn.accountservice.exc.AppException;
import com.gsvn.accountservice.exc.ErrorCode;
import com.gsvn.accountservice.model.dto.request.*;
import com.gsvn.accountservice.model.dto.response.AuthenticationResponse;
import com.gsvn.accountservice.model.entity.*;
import com.gsvn.accountservice.model.internal.IntrospectRequest;
import com.gsvn.accountservice.model.internal.IntrospectResponse;
import com.gsvn.accountservice.model.internal.PasswordResetRequest;
import com.gsvn.accountservice.repository.UserRepository;
import com.gsvn.accountservice.repository.UserRoleRepository;
import com.gsvn.accountservice.service.AuthenticationService;
import com.gsvn.accountservice.service.TokenService;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
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
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final NotificationClient notificationClient;
    @Value("${jwt.signerKey}") @NonFinal String SIGNER_KEY;
    @Value("${jwt.valid-duration}") @NonFinal long VALID_DURATION;
    @Value("${jwt.refreshable-duration}") @NonFinal long REFRESHABLE_DURATION;
    @Value("${jwt.reset-password-duration:900}") @NonFinal long RESET_PASSWORD_DURATION;
    // protected final String GRANT_TYPE = "authorization_code";

    public IntrospectResponse introspect(IntrospectRequest request) {
        try {
            verifyToken(request.token(), false);
            return new IntrospectResponse(true);
        } catch (Exception e) {
            return new IntrospectResponse(false);
        }
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        validateUserStatus(user);

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        return generateAuthResponse(user);
    }
    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
        var signedJWT = verifyToken(request.token(), true);
        var jit = signedJWT.getJWTClaimsSet().getJWTID();
        var userId = signedJWT.getJWTClaimsSet().getStringClaim("userId");
        var email = signedJWT.getJWTClaimsSet().getSubject();

        long remainingTime = getRemainingMillis(signedJWT);
        tokenService.blacklistToken(userId, jit, remainingTime);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        return generateAuthResponse(user);
    }
    public void logout(LogoutRequest request) {
        try {
            var signedJWT = verifyToken(request.token(), true);
            String jit = signedJWT.getJWTClaimsSet().getJWTID();
            String userId = signedJWT.getJWTClaimsSet().getStringClaim("userId");

            tokenService.blacklistToken(userId, jit, getRemainingMillis(signedJWT));
        } catch (Exception e) {
            log.info("Logout: Token already invalid or expired.");
        }
    }
    private AuthenticationResponse generateAuthResponse(User user) {
        Set<Integer> roleIds = getRoleIdForUser(user);

        // Tạo Access Token
        String accessToken = generateToken(user, roleIds, VALID_DURATION, false);
        // Tạo Refresh Token
        String refreshToken = generateToken(user, roleIds, REFRESHABLE_DURATION, true);

        // Lưu vào Redis (Whitelist & Session management)
        tokenService.saveToken(user.getUserId(), accessToken, VALID_DURATION, TimeUnit.SECONDS);
        tokenService.saveToken(user.getUserId(), refreshToken, REFRESHABLE_DURATION, TimeUnit.SECONDS);

        return new AuthenticationResponse(accessToken, refreshToken, true);
    }
    private String generateToken(User user, Set<Integer> roleIds, long duration, boolean isRefresh) {
        String role = user.getIsStaff() ? "STAFF" : "CUSTOMER";
        String referenceType = user.getIsStaff() ? "staffId" : "customerId";

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issuer("gsvn.com")
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(duration, ChronoUnit.SECONDS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("userId", user.getUserId())
                .claim("role", role)
                .claim("roleIds", roleIds)
                .claim(referenceType, user.getReferenceId())
                .claim("isRefresh", isRefresh)
                .build();

        return signToken(claimsSet);
    }

    private SignedJWT verifyToken(String token, boolean isRefreshCheck) throws JOSEException, ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        if (!signedJWT.verify(verifier)) throw new AppException(ErrorCode.UNAUTHENTICATED);

        JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
        if (claims.getExpirationTime().before(new Date())) throw new AppException(ErrorCode.UNAUTHENTICATED);

        String userId = claims.getStringClaim("userId");
        String jit = claims.getJWTID();

        if (tokenService.isTokenBlacklisted(userId, jit)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        return signedJWT;
    }
    private String signToken(JWTClaimsSet claimsSet) {
        JWSObject jwsObject = new JWSObject(new JWSHeader(JWSAlgorithm.HS512), new Payload(claimsSet.toJSONObject()));
        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot sign JWT", e);
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }
    private void validateUserStatus(User user) {
        if (user.getDeletedAt() != null) throw new AppException(ErrorCode.USER_NOT_EXISTED);
        if (!user.getIsActive()) throw new AppException(ErrorCode.USER_LOCKED);
    }
    private long getRemainingMillis(SignedJWT signedJWT) throws ParseException {
        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        return Math.max(0, expiryTime.getTime() - System.currentTimeMillis());
    }
    private Set<Integer> getRoleIdForUser(User user) {
        return userRoleRepository.getAllByUserId(user.getUserId())
                .stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toSet());
    }

    public String generateResetPasswordToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        validateUserStatus(user);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issuer("gsvn.com")
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(RESET_PASSWORD_DURATION, ChronoUnit.SECONDS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("userId", user.getUserId())
                .claim("purpose", "RESET_PASSWORD")
                .build();

        String token = signToken(claimsSet);
        tokenService.saveToken(user.getUserId(), token, RESET_PASSWORD_DURATION, TimeUnit.SECONDS);

        return token;
    }
    public String verifyResetPasswordToken(String token) throws JOSEException, ParseException {
        var signedJWT = verifyToken(token, false);
        var claims = signedJWT.getJWTClaimsSet();
        String purpose = claims.getStringClaim("purpose");
        if (!"RESET_PASSWORD".equals(purpose)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        String userId = claims.getStringClaim("userId");
        String jit = claims.getJWTID();
        long remainingTime = getRemainingMillis(signedJWT);
        tokenService.blacklistToken(userId, jit, remainingTime);
        return userId;
    }
    public void requestResetPassword(String email) {
        String token = generateResetPasswordToken(email);
        PasswordResetRequest mailRequest = new PasswordResetRequest();
        mailRequest.setEmail(email);
        mailRequest.setToken(token);

        try {
            notificationClient.sendResetPasswordEmail(mailRequest);
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }
    public void resetPassword(String token, String newPassword) {
        try {
            String userId = verifyResetPasswordToken(token);

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        } catch (JOSEException | ParseException e) {
            throw new AppException(ErrorCode.ITEM_NOT_EXISTED);
        }
    }
}