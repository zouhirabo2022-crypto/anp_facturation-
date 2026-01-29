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

class FacturationFlowIntegrationTest extends AbstractIntegrationTest {

        @Autowired
        private TestRestTemplate restTemplate;

        @org.springframework.test.context.bean.override.mockito.MockitoBean
        private org.example.anpfacturationbackend.service.EmailService emailService;

        @Test
        void verifyFullBusinessFlow() {
                // 1. Authenticate
                String token = authenticate("admin", "admin123");
                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(token);
                headers.setContentType(MediaType.APPLICATION_JSON);

                // 2. Create Client
                ClientDTO client = new ClientDTO();
                client.setNom("Integration Test Client");
                client.setEmail("test@integration.com");
                client.setIce("123456789");
                client.setAdresse("123 Integration Street");
                client.setTelephone("0600000000");

                HttpEntity<ClientDTO> clientRequest = new HttpEntity<>(client, headers);
                ResponseEntity<ClientDTO> clientResponse = restTemplate.postForEntity("/api/clients", clientRequest,
                                ClientDTO.class);
                assertThat(clientResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
                Long clientId = clientResponse.getBody().getId();
                assertThat(clientId).isNotNull();

                // 3. Create Prestation
                PrestationDTO prestation = new PrestationDTO();
                prestation.setCode("TEST-PREST-001");
                prestation.setLibelle("Prestation Test Integration");
                prestation.setTauxTva(20.0);
                prestation.setTauxTr(10.0);
                prestation.setCompteComptable("7001");

                HttpEntity<PrestationDTO> prestationRequest = new HttpEntity<>(prestation, headers);
                ResponseEntity<PrestationDTO> prestationResponse = restTemplate.postForEntity("/api/prestations",
                                prestationRequest, PrestationDTO.class);
                assertThat(prestationResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
                Long prestationId = prestationResponse.getBody().getId();

                // 4. Create Facture
                FactureDTO facture = new FactureDTO();
                facture.setClientId(clientId);
                facture.setDate(java.time.LocalDate.now());
                facture.setStatut(StatutFacture.BROUILLON);

                LigneFactureDTO ligne = new LigneFactureDTO();
                ligne.setPrestationId(prestationId);
                ligne.setQuantite(10.0);
                ligne.setPrixUnitaire(100.0);
                // Base: 1000, TR: 10% -> 100, Net: 900, TVA: 20% -> 180, TTC: 1080

                facture.setLignes(Collections.singletonList(ligne));

                HttpEntity<FactureDTO> factureRequest = new HttpEntity<>(facture, headers);
                ResponseEntity<FactureDTO> factureResponse = restTemplate.postForEntity("/api/factures", factureRequest,
                                FactureDTO.class);
                assertThat(factureResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
                FactureDTO createdFacture = factureResponse.getBody();
                assertThat(createdFacture.getId()).isNotNull();

                // 5. Verify Calculations
                assertThat(createdFacture.getMontantHt()).isEqualTo(1000.0); // 10 * 100
                assertThat(createdFacture.getMontantTr()).isEqualTo(100.0); // 1000 * 10%
                assertThat(createdFacture.getMontantTva()).isEqualTo(220.0); // (1000 + 100) * 20%
                assertThat(createdFacture.getMontantTtc()).isEqualTo(1320.0); // 1000 + 100 + 220

                // 6. Verify PDF Generation
                HttpHeaders pdfHeaders = new HttpHeaders();
                pdfHeaders.setBearerAuth(token);
                HttpEntity<Void> pdfRequest = new HttpEntity<>(pdfHeaders);
                ResponseEntity<byte[]> pdfResponse = restTemplate.exchange(
                                "/api/factures/" + createdFacture.getId() + "/pdf",
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
