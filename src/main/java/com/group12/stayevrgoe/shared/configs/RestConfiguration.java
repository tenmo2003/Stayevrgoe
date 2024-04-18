package com.group12.stayevrgoe.shared.configs;

import com.group12.stayevrgoe.user.UserService;
import com.group12.stayevrgoe.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class RestConfiguration {
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final UserService userService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(
                        requests -> requests.requestMatchers("/api/authenticate").permitAll()
                                .requestMatchers("/api/users/admin").hasRole(User.Role.ADMIN.toString())
                                .requestMatchers("/api/users/**").authenticated()
                                .anyRequest().permitAll())
                .httpBasic(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(
                        exception ->
                                exception.authenticationEntryPoint(customAuthenticationEntryPoint)
                                        .accessDeniedHandler(customAuthenticationEntryPoint))
                .addFilterBefore(jwtAuthenticationFilter, BasicAuthenticationFilter.class)
        ;

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
