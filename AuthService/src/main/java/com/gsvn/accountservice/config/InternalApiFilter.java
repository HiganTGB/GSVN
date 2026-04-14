package com.gsvn.accountservice.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class InternalApiFilter extends OncePerRequestFilter {
    @Value("${app.security.internal-api-key}")
    private String internalApiKey;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        if (path.contains("/internal/")) {
            String requestKey = request.getHeader("X-Internal-Key");
            if (requestKey == null || !requestKey.equals(internalApiKey)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("Invalid Internal API Key");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}