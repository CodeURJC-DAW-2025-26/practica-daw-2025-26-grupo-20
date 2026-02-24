package es.codeurjc.mokaf.config;

import es.codeurjc.mokaf.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Autowired
        private UserService userService;

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public DaoAuthenticationProvider authenticationProvider() {
                DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userService);
                authProvider.setPasswordEncoder(passwordEncoder());
                return authProvider;
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
                return authConfig.getAuthenticationManager();
        }

        @Bean
        public SecurityContextRepository securityContextRepository() {
                HttpSessionSecurityContextRepository repo = new HttpSessionSecurityContextRepository();
                repo.setSpringSecurityContextKey("SPRING_SECURITY_CONTEXT");
                return repo;
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf
                                                .ignoringRequestMatchers("/api/**"))

                                .securityContext(context -> context
                                                .securityContextRepository(securityContextRepository())
                                                .requireExplicitSave(false) // Automatically save security context
                                )

                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**",
                                                                "/favicon.ico")
                                                .permitAll()
                                                .requestMatchers("/", "/index", "/menu", "/nosotros",
                                                                "/sucursales", "/contact", "/login", "/register",
                                                                "/statistics/**")
                                                .permitAll()
                                                .requestMatchers("/admin/**", "/profileADMIN", "/profileADMIN/**",
                                                                "/statistics", "/gestion_menu")
                                                .hasRole("ADMIN")
                                                .requestMatchers("/profile", "/profile/**", "/cart", "/orders")
                                                .authenticated()
                                                .anyRequest().permitAll())

                                .formLogin(form -> form
                                                .loginPage("/login")
                                                .loginProcessingUrl("/login")
                                                .usernameParameter("email")
                                                .passwordParameter("password")
                                                .defaultSuccessUrl("/redirect-after-login", true)
                                                .failureUrl("/login?error=true")
                                                .permitAll())

                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/login?logout=true")
                                                .invalidateHttpSession(true)
                                                .deleteCookies("JSESSIONID")
                                                .permitAll())

                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                                                .enableSessionUrlRewriting(false) // Use cookies only, not URL rewriting
                                );

                return http.build();
        }
}