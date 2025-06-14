package com.example.demo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//Tell the spring that we are going to customize this SecurityFilterChain
@Configuration
//Without this spring might ignore your custom security and uses default security(Not strictly needed for SpringBoot 3 and above).
@EnableWebSecurity
//Tells Spring to secure methods with annotations like @PreAuthorize or @Secured
@EnableMethodSecurity
//Creates constructor for this class that includes the fields marked final
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean 
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                //since you’re using tokens (JWT) for auth CSRF isn’t needed ,So we’re disabling it.
                .csrf(AbstractHttpConfigurer::disable)
                //Request Matching with specific path mentioned here should permit for everyone, all others should be authenticated
                .authorizeHttpRequests(auth -> auth.requestMatchers("/api/v1/auth/**").permitAll().anyRequest().authenticated())
                //Tells spring not to create sessions. each request must carry its own token
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //Only need if we use basic authentication not for JWT
                .authenticationProvider(authenticationProvider)
                //use jwtAuthFilter to handle token-based authentication in the first place, If token is invalid or unavailable it uses UsernamePasswordAuthenticationFilter which is kind of useless in scenario here.
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();

    }
}



