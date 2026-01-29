package org.example.anpfacturationbackend.controller;

import org.example.anpfacturationbackend.dto.UserDTO;
import org.example.anpfacturationbackend.service.UserService;
import org.example.anpfacturationbackend.security.JwtUtils;
import org.example.anpfacturationbackend.security.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(org.example.anpfacturationbackend.config.SecurityConfig.class) // Import Security Config
@org.springframework.test.context.TestPropertySource(properties = {
        "spring.ldap.urls=ldap://localhost:8389",
        "spring.ldap.base=dc=example,dc=com",
        "spring.ldap.username=cn=admin,dc=example,dc=com",
        "spring.ldap.password=password",
        "app.jwtSecret=testSecretKeyForJwtTokenGenerationShouldBeLongEnough",
        "app.jwtExpirationMs=86400000"
})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean(name = "ldapAuthenticationProvider")
    private org.springframework.security.authentication.AuthenticationProvider ldapAuthenticationProvider;

    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword("password");
        userDTO.setEnabled(true);
        userDTO.setRoles(Collections.singleton("ADMIN_SYSTEME"));
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN_SYSTEME" })
    void getAll_ShouldReturnUsers() throws Exception {
        when(userService.findAll()).thenReturn(Arrays.asList(userDTO));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("testuser"));
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN_SYSTEME" })
    void create_ShouldReturnUser() throws Exception {
        when(userService.create(any(UserDTO.class))).thenReturn(userDTO);

        mockMvc.perform(post("/api/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"testuser\",\"password\":\"password\",\"roles\":[\"ADMIN_SYSTEME\"]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN_SYSTEME" })
    void delete_ShouldReturnOk() throws Exception {
        mockMvc.perform(delete("/api/users/testuser").with(csrf()))
                .andExpect(status().isOk());

        verify(userService).delete("testuser");
    }
}
