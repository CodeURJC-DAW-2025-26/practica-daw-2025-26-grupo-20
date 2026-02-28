package es.codeurjc.mokaf.config;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class DebugFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String uri = httpRequest.getRequestURI();
        
        if (uri.contains("login") || uri.contains("profile") || uri.equals("/")) {
            System.out.println("\n========== DEBUG FILTER ==========");
            System.out.println("URL: " + uri);
            System.out.println("Method: " + httpRequest.getMethod());
            
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            System.out.println("Authentication: " + auth);
            
            if (auth != null) {
                System.out.println("  - Principal: " + auth.getPrincipal());
                System.out.println("  - Authorities: " + auth.getAuthorities());
                System.out.println("  - IsAuthenticated: " + auth.isAuthenticated());
            }
            System.out.println("==================================\n");
        }
        
        chain.doFilter(request, response);
    }
}