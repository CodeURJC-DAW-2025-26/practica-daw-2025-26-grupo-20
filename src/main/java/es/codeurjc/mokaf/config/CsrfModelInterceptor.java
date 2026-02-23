package es.codeurjc.mokaf.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import es.codeurjc.mokaf.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class CsrfModelInterceptor implements HandlerInterceptor {

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, 
                          Object handler, ModelAndView modelAndView) {
        
        if (modelAndView != null && !modelAndView.isEmpty()) {
            // Debug session info
            HttpSession session = request.getSession(false);
            System.out.println("\n=== INTERCEPTOR DEBUG ===");
            System.out.println("URL: " + request.getRequestURI());
            System.out.println("Session ID: " + (session != null ? session.getId() : "NULL"));
            
            // Add CSRF token
            CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
            if (csrfToken != null) {
                modelAndView.addObject("_csrf", csrfToken);
                System.out.println("CSRF token added");
            }
            
            // Add user to model for header display
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            System.out.println("Authentication from SecurityContextHolder: " + authentication);
            
            if (authentication != null && authentication.isAuthenticated() 
                && !"anonymousUser".equals(authentication.getPrincipal())) {
                
                Object principal = authentication.getPrincipal();
                System.out.println("Principal type: " + principal.getClass().getName());
                
                if (principal instanceof User) {
                    User user = (User) principal;
                    modelAndView.addObject("user", user);
                    System.out.println("User added to model: " + user.getEmail());
                    
                    if (user.getRole() == User.Role.ADMIN) {
                        modelAndView.addObject("isAdmin", true);
                        System.out.println("isAdmin flag added");
                    }
                } else {
                    System.out.println("Principal is not User instance: " + principal);
                }
            } else {
                System.out.println("No authenticated user found");
            }
            System.out.println("========================\n");
        }
    }
}