package org.example.anpfacturationbackend;

import org.example.anpfacturationbackend.dto.*;
import org.example.anpfacturationbackend.enums.StatutFacture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.Map;

class FactureAvoirIntegrationTest extends AbstractIntegrationTest {

        @Autowired
        private TestRestTemplate restTemplate;

        @Autowired
        private org.example.anpfacturationbackend.repository.UserRepository userRepository;
        @Autowired
        private org.example.anpfacturationbackend.repository.RoleRepository roleRepository;
        @Autowired
        private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

        @org.junit.jupiter.api.BeforeEach
        void setupUser() {
                org.example.anpfacturationbackend.entity.Role adminRole = roleRepository.findByName("ADMIN_SYSTEME")
                                .orElseGet(() -> roleRepository.save(org.example.anpfacturationbackend.entity.Role
                                                .builder().name("ADMIN_SYSTEME").build()));

                if (userRepository.findByUsername("admin").isEmpty()) {
                        org.example.anpfacturationbackend.entity.User admin = org.example.anpfacturationbackend.entity.User
                                        .builder()
                                        .username("admin")
                                        .password(passwordEncoder.encode("admin123"))
                                        .roles(java.util.Collections.singleton(adminRole))
                                        .enabled(true)
                                        .build();
                        userRepository.save(admin);
                }
        }

        @Test
        void verifyAvoirCreation() {
                // 1. Authenticate
                String token = authenticate("admin", "admin123");
                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(token);
                headers.setContentType(MediaType.APPLICATION_JSON);

                // 2. Create Client
                ClientDTO client = new ClientDTO();
                client.setNom("Avoir Test Client");
                client.setEmail("test-avoir@integration.com");
                client.setIce("999999999");
                client.setAdresse("123 Avoir Street");
                client.setTelephone("0699999999");

                HttpEntity<ClientDTO> clientRequest = new HttpEntity<>(client, headers);
                ResponseEntity<ClientDTO> clientResponse = restTemplate.postForEntity("/api/clients", clientRequest,
                                ClientDTO.class);
                assertThat(clientResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
                Long clientId = clientResponse.getBody().getId();

                // 3. Create Prestation
                PrestationDTO prestation = new PrestationDTO();
                prestation.setCode("TEST-AVOIR-001");
                prestation.setLibelle("Prestation Avoir Integration");
                prestation.setTauxTva(20.0);
                prestation.setTauxTr(0.0);
                prestation.setCompteComptable("7002");

                HttpEntity<PrestationDTO> prestationRequest = new HttpEntity<>(prestation, headers);
                ResponseEntity<PrestationDTO> prestationResponse = restTemplate.postForEntity("/api/prestations",
                                prestationRequest, PrestationDTO.class);
                assertThat(prestationResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
                Long prestationId = prestationResponse.getBody().getId();

                // 4. Create Facture (Standard)
                FactureDTO facture = new FactureDTO();
                facture.setClientId(clientId);
                facture.setDate(java.time.LocalDate.now());
                facture.setStatut(StatutFacture.BROUILLON);

                LigneFactureDTO ligne = new LigneFactureDTO();
                ligne.setPrestationId(prestationId);
                ligne.setQuantite(5.0);
                ligne.setPrixUnitaire(200.0); // 1000 Total HT

                facture.setLignes(Collections.singletonList(ligne));

                HttpEntity<FactureDTO> factureRequest = new HttpEntity<>(facture, headers);
                ResponseEntity<FactureDTO> factureResponse = restTemplate.postForEntity("/api/factures", factureRequest,
                                FactureDTO.class);
                assertThat(factureResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
                FactureDTO draftFacture = factureResponse.getBody();

                // 5. Validate Facture
                ResponseEntity<FactureDTO> validatedResponse = restTemplate.exchange(
                                "/api/factures/" + draftFacture.getId() + "/validate",
                                HttpMethod.POST,
                                new HttpEntity<>(headers),
                                FactureDTO.class);
                assertThat(validatedResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
                FactureDTO validatedFacture = validatedResponse.getBody();
                assertThat(validatedFacture.getStatut()).isEqualTo(StatutFacture.VALIDEE);
                assertThat(validatedFacture.getMontantTtc()).isEqualTo(1200.0); // 1000 + 20% TVA

                // 6. Create Avoir (Credit Note)
                ResponseEntity<FactureDTO> avoirResponse = restTemplate.exchange(
                                "/api/factures/" + validatedFacture.getId() + "/avoir",
                                HttpMethod.POST,
                                new HttpEntity<>(headers),
                                FactureDTO.class);

                assertThat(avoirResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
                FactureDTO avoir = avoirResponse.getBody();

                // 7. Verify Avoir Properties
                assertThat(avoir.getId()).isNotEqualTo(validatedFacture.getId());
                assertThat(avoir.getStatut()).isEqualTo(StatutFacture.VALIDEE); // Should be auto-validated
                assertThat(avoir.getNumero()).isNotEqualTo(validatedFacture.getNumero());
                assertThat(avoir.getMontantHt()).isEqualTo(-1000.0);
                assertThat(avoir.getMontantTva()).isEqualTo(-200.0);
                assertThat(avoir.getMontantTtc()).isEqualTo(-1200.0);
                assertThat(avoir.getLignes()).hasSize(1);
                assertThat(avoir.getLignes().get(0).getQuantite()).isEqualTo(-5.0);

                // 8. Verify PDF for Avoir
                HttpHeaders pdfHeaders = new HttpHeaders();
                pdfHeaders.setBearerAuth(token);
                HttpEntity<Void> pdfRequest = new HttpEntity<>(pdfHeaders);
                ResponseEntity<byte[]> pdfResponse = restTemplate.exchange(
                                "/api/factures/" + avoir.getId() + "/pdf",
                                HttpMethod.GET,
                                pdfRequest,
                                byte[].class);
                assertThat(pdfResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
                assertThat(pdfResponse.getBody()).startsWith("%PDF-".getBytes());
        }

        private String authenticate(String username, String password) {
                Map<String, String> creds = Map.of("username", username, "password", password);
                ResponseEntity<Map<String, Object>> response = restTemplate.exchange("/api/auth/login", HttpMethod.POST,
                                new HttpEntity<>(creds),
                                new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {
                                });
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                return (String) response.getBody().get("accessToken");
        }
}
