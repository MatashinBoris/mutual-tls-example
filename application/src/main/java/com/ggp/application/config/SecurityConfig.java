package com.ggp.application.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private static final String TRUSTED_GGP_NAME = "client";

    /**
     * We take from certificate CN (Common Name "CN" - its value hardcoded in certificate, for example "clientBob.p12" contains CN "Bob")
     * and load it to userDetailsService.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .x509(httpSecurityX509Configurer -> {
                    httpSecurityX509Configurer.subjectPrincipalRegex("CN=(.*?)(?:,|$)")
                            .userDetailsService(userDetailsService());
                }).build();
    }

    @Bean
    public UserDetailsService userDetailsService() {

        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                /**
                 * Its mock implementation, instead userRepository.findByUserName(), or id().
                 * "username" arg get from .x509() config.
                 * TRUSTED_GGP_NAME its a client name (CN)
                 */
                if (username.equals(TRUSTED_GGP_NAME)) {
                    return new User(username, "",
                            AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER"));
                }
                throw new UsernameNotFoundException("User not found!");
            }
        };
    }

}
