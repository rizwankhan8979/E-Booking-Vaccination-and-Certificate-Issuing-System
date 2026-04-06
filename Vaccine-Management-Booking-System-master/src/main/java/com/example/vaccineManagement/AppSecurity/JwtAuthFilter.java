package com.example.vaccineManagement.AppSecurity;


import jakarta.servlet.*;
import jakarta.servlet.http.*;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthFilter(JwtService jwtService,
                         CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        try {
            String email = jwtService.extractUsername(token);

            if (email != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null &&
                    jwtService.validateToken(token)) {

                var userDetails =
                        userDetailsService.loadUserByUsername(email);

                var authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());

                authToken.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request));

                SecurityContextHolder.getContext()
                        .setAuthentication(authToken);
            }
        } catch (ExpiredJwtException e) {
            // Token is expired. We don't set the authentication but we don't throw an error.
            // This allows the request to reach the permitAll() endpoints in SecurityConfig.
            System.out.println("JWT token expired: " + e.getMessage());
        } catch (Exception e) {
            // Handle any other JWT-related issues silently
            System.out.println("JWT validation failed: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}