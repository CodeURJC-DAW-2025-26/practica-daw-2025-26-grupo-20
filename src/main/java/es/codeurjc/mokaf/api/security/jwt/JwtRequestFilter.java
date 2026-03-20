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

/**
 * Filtro que se ejecuta en cada petición a /api/**.
 * Lee el AuthToken de la cookie, lo valida y si es correcto
 * establece el usuario autenticado en el SecurityContext.
 *
 * Al ser stateless, no hay sesión: el usuario se autentica
 * de nuevo en cada petición a partir del token.
 */
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

                // Cargar el UserDetails desde base de datos para tener el objeto User completo
                UserDetails userDetails = userService.loadUserByUsername(username);

                // Crear autenticación con los authorities del token
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
            // No lanzamos excepción — dejamos que la cadena continúe
            // y Spring Security devolverá 401 si el endpoint lo requiere
        }

        filterChain.doFilter(request, response);
    }
}
