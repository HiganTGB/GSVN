package com.tgb.gsvnbackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@EnableMethodSecurity
public  class WebSecurityConfig { }

/*Configuration
@EnableWebSecurity
@EnableMethodSecurity
public static class WebSecurityConfig {

    @Bean
    SecurityFilterChain filterChain(
            HttpSecurity http,
            Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter,
            @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") String issuerUri) throws Exception {

        http.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)));

        // Enable and configure CORS
        http.cors(cors -> cors.configurationSource(corsConfigurationSource("http://localhost:4200")));

        // State-less session (state in access-token only)
        http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Disable CSRF because of state-less session-management
        http.csrf(csrf -> csrf.disable());

        // Return 401 (unauthorized) instead of 302 (redirect to login) when
        // authorization is missing or invalid
        http.exceptionHandling(eh -> eh.authenticationEntryPoint((request, response, authException) -> {
            response.addHeader(HttpHeaders.WWW_AUTHENTICATE, "OAuth realm=\"%s\"".formatted(issuerUri));
            response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
        }));

        // @formatter:off
        http.authorizeHttpRequests(accessManagement -> accessManagement
                .requestMatchers("/actuator/health/readiness", "/actuator/health/liveness", "/v3/api-docs/**").permitAll()
                .anyRequest().authenticated()
        );
        // @formatter:on

        return http.build();
    }

    private UrlBasedCorsConfigurationSource corsConfigurationSource(String... origins) {
        final var configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(origins));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("*"));

        final var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/my-resources/**", configuration);
        return source;
    }

    @RequiredArgsConstructor
    static class JwtGrantedAuthoritiesConverter implements Converter<Jwt, Collection<? extends GrantedAuthority>> {

        @Override
        @SuppressWarnings({ "rawtypes", "unchecked" })
        public Collection<? extends GrantedAuthority> convert(Jwt jwt) {
            return Stream.of("$.realm_access.roles", "$.resource_access.*.roles").flatMap(claimPaths -> {
                        Object claim;
                        try {
                            claim = JsonPath.read(jwt.getClaims(), claimPaths);
                        } catch (PathNotFoundException e) {
                            claim = null;
                        }
                        if (claim == null) {
                            return Stream.empty();
                        }
                        if (claim instanceof String claimStr) {
                            return Stream.of(claimStr.split(","));
                        }
                        if (claim instanceof String[] claimArr) {
                            return Stream.of(claimArr);
                        }
                        if (Collection.class.isAssignableFrom(claim.getClass())) {
                            final var iter = ((Collection) claim).iterator();
                            if (!iter.hasNext()) {
                                return Stream.empty();
                            }
                            final var firstItem = iter.next();
                            if (firstItem instanceof String) {
                                return (Stream<String>) ((Collection) claim).stream();
                            }
                            if (Collection.class.isAssignableFrom(firstItem.getClass())) {
                                return (Stream<String>) ((Collection) claim).stream().flatMap(colItem -> ((Collection) colItem).stream()).map(String.class::cast);
                            }
                        }
                        return Stream.empty();
                    })
                    .map(SimpleGrantedAuthority::new)
                    .map(GrantedAuthority.class::cast).toList();
        }
    }

    @Component
    @RequiredArgsConstructor
    static class SpringAddonsJwtAuthenticationConverter implements Converter<Jwt, JwtAuthenticationToken> {

        @Override
        public JwtAuthenticationToken convert(Jwt jwt) {
            final var authorities = new JwtGrantedAuthoritiesConverter().convert(jwt);
            final String username = JsonPath.read(jwt.getClaims(), "preferred_username");
            return new JwtAuthenticationToken(jwt, authorities, username);
        }
    }*/
}
