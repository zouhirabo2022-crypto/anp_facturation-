package org.anpfacturationbackend.config;

import org.anpfacturationbackend.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableMethodSecurity
@Profile("prod")
public class SecurityConfigProd {

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

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfigProd(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers
                        .contentSecurityPolicy(csp -> csp
                                .policyDirectives(
                                        "default-src 'self'; script-src 'self' http://localhost:4200 http://localhost:8088; style-src 'self' 'unsafe-inline'; img-src 'self' data:; font-src 'self' data:; connect-src 'self' http://localhost:8080 http://localhost:4200 http://localhost:8088 ws://localhost:4200")))
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
    public AuthenticationProvider daoAuthenticationProvider(org.springframework.security.core.userdetails.UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        org.springframework.security.authentication.dao.DaoAuthenticationProvider provider = new org.springframework.security.authentication.dao.DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationProvider ldapAuthenticationProvider() {
        if (adDomain != null && !adDomain.isEmpty()) {
            // Active Directory Configuration
            ActiveDirectoryLdapAuthenticationProvider adProvider = new ActiveDirectoryLdapAuthenticationProvider(
                    adDomain, adUrl);
            adProvider.setConvertSubErrorCodesToExceptions(true);
            adProvider.setUseAuthenticationRequestCredentials(true);
            adProvider.setSearchFilter("(&(objectClass=user)(sAMAccountName={1}))");
            return adProvider;
        } else {
            // Standard LDAP Configuration
            if (ldapUrls == null || ldapUrls.isEmpty()) {
                throw new IllegalStateException("LDAP URLs are missing for Standard LDAP configuration");
            }
            // Create context source inline to avoid defining a bean that might be null or
            // unused
            DefaultSpringSecurityContextSource contextSource = new DefaultSpringSecurityContextSource(
                    Collections.singletonList(ldapUrls), ldapBase);
            contextSource.setUserDn(ldapUsername);
            contextSource.setPassword(ldapPassword);
            contextSource.afterPropertiesSet();

            BindAuthenticator authenticator = new BindAuthenticator(contextSource);
            authenticator.setUserDnPatterns(new String[] { "uid={0},ou=people" });

            DefaultLdapAuthoritiesPopulator authoritiesPopulator = new DefaultLdapAuthoritiesPopulator(
                    contextSource, "ou=groups");
            authoritiesPopulator.setGroupRoleAttribute("cn");
            authoritiesPopulator.setConvertToUpperCase(true);
            authoritiesPopulator.setRolePrefix("ROLE_");

            return new LdapAuthenticationProvider(authenticator, authoritiesPopulator);
        }
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Allow all origins for development convenience? Keeping consistent with
        // previous config.
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
