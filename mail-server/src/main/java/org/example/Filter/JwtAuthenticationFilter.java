package org.example.Filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.Helper.JwtHelper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;


public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        System.out.println("[JwtFilter] Incoming request: " + request.getRequestURI());

        if (authHeader == null) {
            System.out.println("[JwtFilter] No Authorization header found");
        } else if (!authHeader.startsWith("Bearer ")) {
            System.out.println("[JwtFilter] Authorization header does not start with Bearer: " + authHeader);
        } else {
            String token = authHeader.substring(7);
            System.out.println("[JwtFilter] Extracted Token: " + token);

            if (JwtHelper.IsValidToken(token)) {
                String username = JwtHelper.extractUsername(token);
                System.out.println("[JwtFilter] Token is valid. Extracted username: " + username);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());

                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println("[JwtFilter] Authentication set in SecurityContext for user: " + username);
            } else {
                System.out.println("[JwtFilter] Token is invalid");
            }
        }

        filterChain.doFilter(request, response);
    }
}