package org.example.anpfacturationbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.example.anpfacturationbackend.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;

@Configuration
@EnableMethodSecurity
@Profile("!test")
public class SecurityConfig {

    @Value("${spring.ldap.urls:}")
    private String ldapUrls;

    @Value("${spring.ldap.base:}")
    private String ldapBase;

    @Value("${spring.ldap.username:}")
    private String ldapUsername;

    @Value("${spring.ldap.password:}")
    private String ldapPassword;

    @Value("${spring.ldap.ad.domain:}")
    private String adDomain;

    @Value("${spring.ldap.ad.url:}")
    private String adUrl;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers
                        .contentSecurityPolicy(csp -> csp
                                .policyDirectives(
                                        "default-src 'self'; script-src 'self' 'unsafe-inline' http://localhost:4200 http://localhost:8088; style-src 'self' 'unsafe-inline'; img-src 'self' data:; font-src 'self' data:; connect-src 'self' http://localhost:8080 http://localhost:4200 http://localhost:8088 ws://localhost:4200")))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .anyRequest().authenticated());

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public org.springframework.security.authentication.dao.DaoAuthenticationProvider daoAuthenticationProvider(
            org.example.anpfacturationbackend.security.CustomUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        org.springframework.security.authentication.dao.DaoAuthenticationProvider authProvider = new org.springframework.security.authentication.dao.DaoAuthenticationProvider(
                userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    // --- LDAP / Active Directory Configuration ---
    @Bean
    public AuthenticationProvider ldapAuthenticationProvider() {
        if (adDomain != null && !adDomain.isEmpty()) {
            // Active Directory Configuration
            ActiveDirectoryLdapAuthenticationProvider adProvider = new ActiveDirectoryLdapAuthenticationProvider(
                    adDomain, adUrl);
            adProvider.setConvertSubErrorCodesToExceptions(true);
            adProvider.setUseAuthenticationRequestCredentials(true);
            // Default search filter for sAMAccountName
            adProvider.setSearchFilter("(&(objectClass=user)(sAMAccountName={1}))"); 
            return adProvider;
        } else {
            // Standard LDAP Configuration
            if (ldapUrls == null || ldapUrls.isEmpty()) {
                throw new IllegalStateException("LDAP URLs are missing for Standard LDAP configuration");
            }
            org.springframework.security.ldap.authentication.BindAuthenticator authenticator = new org.springframework.security.ldap.authentication.BindAuthenticator(
                    contextSource());
            authenticator.setUserDnPatterns(new String[] { "uid={0},ou=people" });

            org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator authoritiesPopulator = new org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator(
                    contextSource(), "ou=groups");
            authoritiesPopulator.setGroupRoleAttribute("cn");
            authoritiesPopulator.setConvertToUpperCase(true);
            authoritiesPopulator.setRolePrefix("ROLE_");

            return new org.springframework.security.ldap.authentication.LdapAuthenticationProvider(authenticator,
                    authoritiesPopulator);
        }
    }

    @Bean
    public org.springframework.security.ldap.DefaultSpringSecurityContextSource contextSource() {
        if (ldapUrls == null || ldapUrls.isEmpty()) {
            // Return null or a dummy source to avoid startup failure when using AD
            return null;
        }
        org.springframework.security.ldap.DefaultSpringSecurityContextSource contextSource = new org.springframework.security.ldap.DefaultSpringSecurityContextSource(
                java.util.Collections.singletonList(ldapUrls), ldapBase);
        contextSource.setUserDn(ldapUsername);
        contextSource.setPassword(ldapPassword);
        return contextSource;
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Allow all origins for development convenience
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration
                .setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Skip-Auth"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
