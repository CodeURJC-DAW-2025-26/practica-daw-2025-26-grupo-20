package es.codeurjc.mokaf.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

import es.codeurjc.mokaf.api.security.UnauthorizedHandlerJwt;
import es.codeurjc.mokaf.api.security.jwt.JwtRequestFilter;
import es.codeurjc.mokaf.service.UserService;



@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserService userService;

    @Autowired
    private UnauthorizedHandlerJwt unauthorizedHandlerJwt;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    // ── share Beans  ─────────────────────────────────────────────────────

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

    // ── @Order(1)  API REST → /api/** → JWT stateless ────────────────────────

    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {

        http
            .securityMatcher("/api/**")
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(unauthorizedHandlerJwt))
            .authenticationProvider(authenticationProvider())

            .authorizeHttpRequests(auth -> auth
                // public endpoints for authentication
                .requestMatchers("/api/v1/auth/**").permitAll()

                // own profile → authenticated
                .requestMatchers("/api/v1/users/me",
                                 "/api/v1/users/me/**").authenticated()

                
                //Self cart → autenticated
                .requestMatchers("/api/v1/cart/",
                                 "/api/v1/cart/**").authenticated()

                // CRUD of users → only ADMIN
                .requestMatchers(HttpMethod.GET,    "/api/v1/users", "/api/v1/users/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST,   "/api/v1/users").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,    "/api/v1/users/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/users/**").hasRole("ADMIN")

                // Reviews of products → authenticated (any user can review, but must be logged in)
                .requestMatchers(HttpMethod.POST,   "/api/v1/products/*/reviews").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/v1/products/*/reviews/*").authenticated()

                //Statistics → only ADMIN
                .requestMatchers("/api/v1/statistics/**").hasRole("ADMIN")

                // CRUD of products (everything else that is not a review) → only ADMIN
                .requestMatchers(HttpMethod.POST,   "/api/v1/products", "/api/v1/products/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,    "/api/v1/products", "/api/v1/products/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/products", "/api/v1/products/**").hasRole("ADMIN")

                .anyRequest().permitAll())

            .formLogin(form -> form.disable())
            .csrf(csrf -> csrf.disable())
            .httpBasic(basic -> basic.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ── @Order(2)  Web MVC → sesión HTTP + form login ─────────────────────────

    @Bean
    @Order(2)
    public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/cart/**"))

            .securityContext(ctx -> ctx
                .securityContextRepository(securityContextRepository())
                .requireExplicitSave(false))

            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico")
                    .permitAll()
                .requestMatchers(
                    "/", "/index", "/menu", "/nosotros",
                    "/branches", "/contact", "/login", "/register")
                    .permitAll()
                .requestMatchers(
                    "/admin/**", "/profileADMIN", "/profileADMIN/**",
                    "/statistics/**", "/gestion_menu")
                    .hasRole("ADMIN")
                .requestMatchers(
                    "/profile", "/profile/**",
                    "/cart", "/cart/**",
                    "/orders", "/profiles/images/**")
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
                .enableSessionUrlRewriting(false));

        return http.build();
    }
}
