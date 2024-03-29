package com.najot.oddiyauth.config;

import com.najot.oddiyauth.service.MyUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConf {
    private final MyUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request->
                        request
                                .requestMatchers("/api/v1/auth/signup").permitAll()
                                .requestMatchers("/api/v1/auth/signup-page").permitAll()
                                .requestMatchers("/api/v1/auth/login").permitAll()
                                .requestMatchers("/api/v1/auth/login-page/**").permitAll()
                                .requestMatchers("/api/v1/home").permitAll()
                                .requestMatchers("/views/**").permitAll()
                                .requestMatchers("/api/v1/admin").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.POST,"/api/v1/product").hasAnyAuthority("CREATE_PRODUCT")
                                .requestMatchers(HttpMethod.PUT,"/api/v1/product").hasAnyAuthority("UPDATE_PRODUCT")
                                .requestMatchers(HttpMethod.DELETE,"/api/v1/product").hasAnyAuthority("DELETE_PRODUCT")
                                .requestMatchers(HttpMethod.GET,"/api/v1/product").hasAuthority("READ_PRODUCT")
                                .anyRequest().authenticated()
                        )
                        .formLogin()
                        .loginPage("/api/v1/auth/login-page")
                        .permitAll();
//
//
//        http.logout(authz -> authz
//                .deleteCookies("JSESSIONID")
//                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
//        );


        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
