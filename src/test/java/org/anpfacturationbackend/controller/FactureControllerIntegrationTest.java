package org.anpfacturationbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.anpfacturationbackend.dto.FactureDTO;
import org.anpfacturationbackend.dto.LigneFactureDTO;
import org.anpfacturationbackend.entity.Client;
import org.anpfacturationbackend.entity.Prestation;
import org.anpfacturationbackend.enums.StatutFacture;
import org.anpfacturationbackend.repository.ClientRepository;
import org.anpfacturationbackend.repository.PrestationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class FactureControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private PrestationRepository prestationRepository;

    private Long clientId;
    private Long prestationId;

    @BeforeEach
    public void setup() {
        // Create Client
        Client client = new Client();
        client.setNom("Test Client");
        client.setAdresse("123 Street");
        client.setTelephone("0600000000");
        client = clientRepository.save(client);
        this.clientId = client.getId();

        // Create Prestation
        if (prestationRepository.findByCode("TEST001").isEmpty()) {
            Prestation prestation = new Prestation();
            prestation.setCode("TEST001");
            prestation.setLibelle("Test Prestation");
            prestation.setTauxTva(20.0);
            prestation.setTauxTr(0.0);
            prestation.setCompteComptable("7000");
            prestation = prestationRepository.save(prestation);
            this.prestationId = prestation.getId();
        } else {
            this.prestationId = prestationRepository.findByCode("TEST001").get().getId();
        }
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN_SYSTEME" })
    public void testCreateFactureSuccess() throws Exception {
        FactureDTO factureDTO = new FactureDTO();
        factureDTO.setClientId(clientId);
        factureDTO.setDate(LocalDate.now());
        factureDTO.setStatut(StatutFacture.BROUILLON);

        LigneFactureDTO ligne = new LigneFactureDTO();
        ligne.setPrestationId(prestationId);
        ligne.setQuantite(10.0);
        ligne.setPrixUnitaire(100.0);

        // Fields that might be required/calculated
        ligne.setTypeTerrain("NORMAL"); // Example assumption

        factureDTO.setLignes(Collections.singletonList(ligne));

        mockMvc.perform(post("/api/factures")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(factureDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.statut").value("BROUILLON"));
    }
}
