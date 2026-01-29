package org.example.anpfacturationbackend.service;

import org.example.anpfacturationbackend.dto.PrestationDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@org.springframework.context.annotation.Import(org.example.anpfacturationbackend.config.TestSecurityConfig.class)
class PrestationServiceTest {

    @Autowired
    private PrestationService prestationService;

    @Test
    void testSavePrestation() {
        // Créer une prestation test
        PrestationDTO dto = new PrestationDTO();
        dto.setCode("TEST_CODE");
        dto.setLibelle("Test Prestation");
        dto.setTauxTva(20.0);
        dto.setTauxTr(0.0);
        dto.setCompteComptable("12345");

        // Sauvegarder
        PrestationDTO saved = prestationService.create(dto);

        // Vérifier que l'objet existe
        assertNotNull(saved);
        assertNotNull(saved.getId());

        // Vérifier le nom
        assertEquals("Test Prestation", saved.getLibelle());
    }

    @Test
    void testGetAllPrestations() {
        var prestations = prestationService.getAll();
        assertNotNull(prestations);
    }
}
