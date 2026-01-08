package de.x132.ahp.config;

import de.x132.ahp.service.AuthenticationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticationService authenticationService;

    public JwtAuthenticationFilter(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Get token from header
        String token = getTokenFromRequest(request);

        if (token != null) {
            Optional<org.springframework.security.core.userdetails.User> user = authenticationService.validateToken(token)
                    .map(client -> new org.springframework.security.core.userdetails.User(
                            client.getNickname(),
                            "",
                            new ArrayList<>()
                    ));

            if (user.isPresent()) {
                Authentication auth = new UsernamePasswordAuthenticationToken(
                        user.get().getUsername(),
                        null,
                        user.get().getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        // Try X-Auth-Token header first
        String token = request.getHeader("X-Auth-Token");
        if (token != null && !token.isEmpty()) {
            return token;
        }

        // Try Authorization header (Bearer token)
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        return null;
    }
}
