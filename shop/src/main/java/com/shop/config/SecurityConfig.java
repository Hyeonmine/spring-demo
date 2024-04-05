package com.shop.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig{

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        HttpSessionRequestCache  cache = new HttpSessionRequestCache();
        cache.setMatchingRequestParameterName(null);
         http.formLogin((it) -> it
                 .loginPage("/members/login")
                 .defaultSuccessUrl("/")
                 .usernameParameter("email")
                 .failureUrl("/members/login/error")
        );
         http.logout( (it) -> it
                .logoutRequestMatcher(new AntPathRequestMatcher("/members/logout"))
                .logoutSuccessUrl("/")
         );

         http.authorizeHttpRequests((req)->{req
                 .requestMatchers(antMatcher("/")).permitAll()
                 .requestMatchers(antMatcher("/members/**")).permitAll()
                 .requestMatchers(antMatcher("/item/**")).permitAll()
                 .requestMatchers(antMatcher("/images/**")).permitAll()
                 .requestMatchers(antMatcher("/css/**")).permitAll()
                 .requestMatchers(antMatcher("/admin/**")).hasRole("ADMIN")
                 .anyRequest().authenticated();
        });

         http.requestCache((it) -> it.requestCache(cache));

         http.exceptionHandling((it)->
                 it.authenticationEntryPoint(new CustomAuthenticationEntryPoint("/members/login")));
         return http.build();
    }
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}
