package com.boot.shopdemo.config;

import com.boot.shopdemo.jwt.JwtAccessDeniedHandler;
import com.boot.shopdemo.jwt.JwtAuthenticationEntryPoint;
import com.boot.shopdemo.jwt.JwtFilter;
import com.boot.shopdemo.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${frontDomain}")
    private String frontDomain;

    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .httpBasic(
                        basic->basic.disable()
                )
                .csrf(
                        csrf->csrf.disable()
                )

                .sessionManagement(
                        sessionManage -> sessionManage
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(
                        new JwtFilter(tokenProvider),
                        UsernamePasswordAuthenticationFilter.class
                )
                .exceptionHandling(
                        exceptionHandler -> exceptionHandler
                                .accessDeniedHandler(jwtAccessDeniedHandler)
                                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )
                .authorizeHttpRequests(
                        request -> {
                            request
                                    .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                                    .requestMatchers(antMatcher("/")).permitAll()
                                    .requestMatchers(antMatcher("/favicon.ico")).permitAll()
                                    .requestMatchers(antMatcher("/member/**")).permitAll()
                                    .requestMatchers(antMatcher("/member/signup")).permitAll()
                                    .requestMatchers(antMatcher("/css/**")).permitAll()
                                    .anyRequest().authenticated();
                        }
                )
                .cors(
                        cors -> cors.configurationSource(configurationSource())
                );
        return http.build();
    }
    @Bean
    public CorsConfigurationSource configurationSource(){
        CorsConfiguration config = new CorsConfiguration();

        config.addAllowedOrigin(frontDomain);
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setMaxAge(86400L);
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

}
