package org.example.anpfacturationbackend.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.mockito.Mockito;

@TestConfiguration
public class TestSecurityConfig {

    @Bean
    @Primary
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        // Set dummy UserDetailsService to avoid NPE if initialized
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(username -> {
            throw new org.springframework.security.core.userdetails.UsernameNotFoundException(username);
        });
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    @Primary
    public LdapAuthenticationProvider ldapAuthenticationProvider() {
        return Mockito.mock(LdapAuthenticationProvider.class);
    }

    @Bean
    @Primary
    public AuthenticationManager authenticationManager() {
        return Mockito.mock(AuthenticationManager.class);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
