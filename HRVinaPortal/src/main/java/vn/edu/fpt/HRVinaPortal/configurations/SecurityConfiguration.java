package vn.edu.fpt.HRVinaPortal.configurations;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // ✅ Cho phép truy cập các endpoint xác thực
                        .requestMatchers("/api/auth/**").permitAll()

                        // ✅ Cho phép các API public cho người dùng
                        .requestMatchers(HttpMethod.GET,
                                "/api/job-postings/**",
                                "/api/companies/**",
                                "/api/job-title-company/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/candidates/**").permitAll()

                        // ✅ Quản lý tài khoản (chỉ ADMIN)
                        .requestMatchers(HttpMethod.POST, "/api/account","api/role").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/account/**", "api/role/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/account/**", "api/role/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/account/**", "api/role/**").hasRole("ADMIN")



                        // ✅ Các API cho ADMIN và RECRUITMENT_MANAGER
                        .requestMatchers(HttpMethod.GET,
                                "/api/candidates/**",
                                "/api/interviews/**",
                                "/api/reports/**"
                        ).hasAnyRole("ADMIN", "Recruitment_Manager")
                        .requestMatchers(HttpMethod.POST,
                                "/api/companies/**",
                                "/api/interviews/**",
                                "/api/job-postings/**",
                                "/api/job-title-company/**",
                                "/api/reports/**"
                        ).hasAnyRole("ADMIN", "Recruitment_Manager")
                        .requestMatchers(HttpMethod.PUT,
                                "/api/candidates/**",
                                "/api/companies/**",
                                "/api/interviews/**",
                                "/api/job-postings/**",
                                "/api/job-title-company/**",
                                "/api/reports/**"
                        ).hasAnyRole("ADMIN", "Recruitment_Manager")
                        .requestMatchers(HttpMethod.DELETE,
                                "/api/candidates/**",
                                "/api/companies/**",
                                "/api/interviews/**",
                                "/api/job-postings/**",
                                "/api/job-title-company/**",
                                "/api/reports/**"
                        ).hasAnyRole("ADMIN", "Recruitment_Manager")

                        // ✅ Mặc định: tất cả request khác phải đăng nhập
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
