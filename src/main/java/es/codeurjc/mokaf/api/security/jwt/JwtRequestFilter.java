package es.codeurjc.mokaf.api.security.jwt;

import es.codeurjc.mokaf.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String token = jwtTokenProvider.getTokenFromCookie(request, TokenType.ACCESS);

            if (token != null && jwtTokenProvider.validateToken(token)) {
                String username = jwtTokenProvider.getUsernameFromToken(token);
                String role     = jwtTokenProvider.getRoleFromToken(token);

                //charge user details from DB (optional, but can be useful for additional checks)
                UserDetails userDetails = userService.loadUserByUsername(username);

                //Create authentication token with role from JWT (no need to fetch from DB if role is in token)
                List<SimpleGrantedAuthority> authorities =
                        List.of(new SimpleGrantedAuthority("ROLE_" + role));

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, authorities);

                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.debug("JWT auth OK for user: {}", username);
            }

        } catch (Exception e) {
            logger.error("JWT filter error: {}", e.getMessage());
            // dont launch exception, just continue without authentication. If the endpoint requires auth, Spring Security will handle it and return 401.
            // Spring security will automatically return 401 for unauthenticated requests to protected endpoints, so we don't need to do anything here. Just log the error and continue.
        }

        filterChain.doFilter(request, response);
    }
}
