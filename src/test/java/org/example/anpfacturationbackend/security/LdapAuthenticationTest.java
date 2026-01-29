package org.example.anpfacturationbackend.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integration")
@TestPropertySource(properties = {
    "spring.ldap.embedded.ldif=classpath:users.ldif",
    "spring.ldap.embedded.base-dn=dc=example,dc=com",
    "spring.ldap.embedded.port=12345",
    "spring.ldap.embedded.validation.enabled=false",
    "spring.ldap.urls=ldap://localhost:12345",
    "spring.ldap.base=dc=example,dc=com",
    "spring.ldap.username=uid=ben,ou=people,dc=example,dc=com",
    "spring.ldap.password=benspassword",
    "si.finance.url=http://localhost:8080/api/stub/si-finance" // Avoid error on client bean creation
})
class LdapAuthenticationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldAuthenticateWithLdapUser() throws Exception {
        String loginJson = """
            {
                "username": "ben",
                "password": "benspassword",
                "provider": "ldap"
            }
            """;

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists());
    }

    @Test
    void shouldRejectWrongLdapPassword() throws Exception {
        String loginJson = """
            {
                "username": "ben",
                "password": "wrongpassword",
                "provider": "ldap"
            }
            """;

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
                .andExpect(status().is(401));
    }
}
