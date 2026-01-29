package org.example.anpfacturationbackend.service;

import org.example.anpfacturationbackend.dto.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.example.anpfacturationbackend.config.TestSecurityConfig;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
@Transactional
class FactureServiceTest {

    @Autowired
    private FactureService factureService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private PrestationService prestationService;

    @Autowired
    private TarifEauElectriciteService tarifEauService;

    @Autowired
    private TarifOTDPService tarifOTDPService;

    @MockitoBean
    private org.example.anpfacturationbackend.client.SiFinanceClient siFinanceClient;

    @Test
    void testCreateFactureSimple() {
        // 1. Setup Client
        ClientDTO client = new ClientDTO();
        client.setNom("TestClient");
        client.setPrenom("TestPrenom");
        client.setAdresse("Test Address");
        client.setTelephone("0600000000");
        ClientDTO savedClient = clientService.create(client);

        // 2. Setup Prestation (Simple, no tranches)
        PrestationDTO prestation = new PrestationDTO();
        prestation.setCode("P_SIMPLE");
        prestation.setLibelle("Prestation Simple");
        prestation.setTauxTva(20.0);
        prestation.setTauxTr(0.0);
        prestation.setCompteComptable("7000");
        PrestationDTO savedPrestation = prestationService.create(prestation);

        // 3. Create Facture
        FactureDTO factureDTO = new FactureDTO();
        factureDTO.setClientId(savedClient.getId());

        LigneFactureDTO ligne = new LigneFactureDTO();
        ligne.setPrestationId(savedPrestation.getId());
        ligne.setQuantite(2.0);
        ligne.setPrixUnitaire(100.0); // Manual price
        factureDTO.setLignes(List.of(ligne));

        FactureDTO savedFacture = factureService.create(factureDTO);

        // 4. Verify
        assertNotNull(savedFacture.getId());
        assertEquals("BROUILLON", savedFacture.getStatut().name());
        assertEquals(200.0, savedFacture.getMontantHt()); // 2 * 100
        assertEquals(40.0, savedFacture.getMontantTva()); // 20% of 200
        assertEquals(240.0, savedFacture.getMontantTtc());
    }

    @Test
    void testCreateFactureWithTarifEau() {
        // 1. Setup Client
        ClientDTO client = new ClientDTO();
        client.setNom("EauClient");
        client.setAdresse("Addr");
        client.setTelephone("0600");
        ClientDTO savedClient = clientService.create(client);

        // 2. Setup Prestation (Eau)
        PrestationDTO prestation = new PrestationDTO();
        prestation.setCode("EAU_TEST");
        prestation.setLibelle("Eau Potable");
        prestation.setTauxTva(10.0); // 10% TVA
        prestation.setTauxTr(0.0);
        prestation.setCompteComptable("7010");
        PrestationDTO savedPrestation = prestationService.create(prestation);

        // 3. Setup Tarif
        TarifEauElectriciteDTO t1 = new TarifEauElectriciteDTO();
        t1.setPrestationId(savedPrestation.getId());
        t1.setCodePort("P001");
        t1.setLibelle("Tarif Eau P001");
        t1.setCodeActivite("ACT01");
        t1.setTarifDistributeur(4.0);
        t1.setTarifFacture(5.0); // This should be used
        t1.setAnneeTarif(2025);
        t1.setActif(true);
        tarifEauService.create(t1);

        // 4. Create Facture
        FactureDTO factureDTO = new FactureDTO();
        factureDTO.setClientId(savedClient.getId());

        LigneFactureDTO ligne = new LigneFactureDTO();
        ligne.setPrestationId(savedPrestation.getId());
        ligne.setQuantite(10.0);
        ligne.setCodePort("P001"); // Select by Code Port
        factureDTO.setLignes(List.of(ligne));

        FactureDTO savedFacture = factureService.create(factureDTO);

        // 5. Verify
        // 10 * 5.0 = 50.0 HT
        assertEquals(50.0, savedFacture.getMontantHt());
        assertEquals(5.0, savedFacture.getMontantTva()); // 10% of 50
        assertEquals(55.0, savedFacture.getMontantTtc());

        // Check finding by ID
        Long id = savedFacture.getId();
        assertNotNull(id);
        FactureDTO found = factureService.getById(id);
        assertEquals(savedFacture.getMontantTtc(), found.getMontantTtc());
    }

    @Test
    void testCreateFactureWithTR() {
        // 1. Setup Client
        ClientDTO client = new ClientDTO();
        client.setNom("TRClient");
        client.setAdresse("Addr");
        client.setTelephone("0600");
        ClientDTO savedClient = clientService.create(client);

        // 2. Setup Prestation with TR
        PrestationDTO prestation = new PrestationDTO();
        prestation.setCode("P_TR");
        prestation.setLibelle("Prestation with TR");
        prestation.setTauxTva(20.0);
        prestation.setTauxTr(5.0); // 5% TR
        prestation.setCompteComptable("7000");
        PrestationDTO savedPrestation = prestationService.create(prestation);

        // 3. Create Facture
        FactureDTO factureDTO = new FactureDTO();
        factureDTO.setClientId(savedClient.getId());

        LigneFactureDTO ligne = new LigneFactureDTO();
        ligne.setPrestationId(savedPrestation.getId());
        ligne.setQuantite(2.0);
        ligne.setPrixUnitaire(100.0);
        factureDTO.setLignes(List.of(ligne));

        FactureDTO savedFacture = factureService.create(factureDTO);

        // 4. Verify Calculations
        // HT = 2 * 100 = 200
        // TR = 200 * 5% = 10
        // TVA = (200 + 10) * 20% = 210 * 0.2 = 42
        // TTC = 200 + 10 + 42 = 252

        assertEquals(200.0, savedFacture.getMontantHt());
        assertEquals(10.0, savedFacture.getMontantTr());
        assertEquals(42.0, savedFacture.getMontantTva());
        assertEquals(252.0, savedFacture.getMontantTtc());
    }

    @Test
    void testCreateFactureWithTarifOTDP() {
        // 1. Setup Client
        ClientDTO client = new ClientDTO();
        client.setNom("OTDPClient");
        client.setAdresse("Port");
        client.setTelephone("0611");
        ClientDTO savedClient = clientService.create(client);

        // 2. Setup Prestation (OTDP)
        PrestationDTO prestation = new PrestationDTO();
        prestation.setCode("OTDP_TEST");
        prestation.setLibelle("OTDP Prestation");
        prestation.setTauxTva(20.0);
        prestation.setTauxTr(0.0);
        prestation.setCompteComptable("7121");
        PrestationDTO savedPrestation = prestationService.create(prestation);

        // 3. Setup Tarif OTDP
        TarifOTDPDTO t1 = new TarifOTDPDTO();
        t1.setPrestationId(savedPrestation.getId());
        t1.setTypeTerrain("TERRE_PLEIN");
        t1.setNatureActivite("COMMERCIALE");
        t1.setCategorie("AUTRE");
        t1.setMontant(50.0); // 50 DH per unit
        t1.setAnneeTarif(2025);
        t1.setActif(true);
        t1.setUniteBase("M2");
        tarifOTDPService.create(t1);

        // 4. Create Facture matching the criteria
        FactureDTO factureDTO = new FactureDTO();
        factureDTO.setClientId(savedClient.getId());

        LigneFactureDTO ligne = new LigneFactureDTO();
        ligne.setPrestationId(savedPrestation.getId());
        ligne.setQuantite(10.0); // 10 m2
        // Criteria for OTDP selection
        ligne.setTypeTerrain("TERRE_PLEIN");
        ligne.setNatureActivite("COMMERCIALE");
        ligne.setCategorie("AUTRE");

        factureDTO.setLignes(List.of(ligne));

        FactureDTO savedFacture = factureService.create(factureDTO);

        // 5. Verify
        // 10 * 50.0 = 500.0 HT
        assertEquals(500.0, savedFacture.getMontantHt());
        assertEquals(100.0, savedFacture.getMontantTva()); // 20% of 500
        assertEquals(600.0, savedFacture.getMontantTtc());
    }

    @Test
    void testValidateFacture() {
        // 1. Create Draft Facture
        ClientDTO client = new ClientDTO();
        client.setNom("ValidateClient");
        client.setAdresse("Test");
        client.setTelephone("0600");
        ClientDTO savedClient = clientService.create(client);

        PrestationDTO prestation = new PrestationDTO();
        prestation.setCode("P_VAL");
        prestation.setLibelle("P Val");
        prestation.setTauxTva(20.0);
        prestation.setTauxTr(0.0);
        prestation.setCompteComptable("7000");
        PrestationDTO savedPrestation = prestationService.create(prestation);

        FactureDTO factureDTO = new FactureDTO();
        factureDTO.setClientId(savedClient.getId());
        LigneFactureDTO ligne = new LigneFactureDTO();
        ligne.setPrestationId(savedPrestation.getId());
        ligne.setQuantite(1.0);
        ligne.setPrixUnitaire(100.0);
        factureDTO.setLignes(List.of(ligne));
        FactureDTO savedFacture = factureService.create(factureDTO);

        // 2. Validate
        FactureDTO validatedFacture = factureService.validate(savedFacture.getId());

        // 3. Verify
        assertEquals(org.example.anpfacturationbackend.enums.StatutFacture.VALIDEE, validatedFacture.getStatut());
        assertNotNull(validatedFacture.getNumero());
    }

    @Test
    void testMarkAsPaid() {
        // 1. Create and Validate
        ClientDTO client = new ClientDTO();
        client.setNom("PaidClient");
        client.setAdresse("Test");
        client.setTelephone("0600");
        ClientDTO savedClient = clientService.create(client);

        PrestationDTO prestation = new PrestationDTO();
        prestation.setCode("P_PAID");
        prestation.setLibelle("P Paid");
        prestation.setTauxTva(20.0);
        prestation.setTauxTr(0.0);
        prestation.setCompteComptable("7000");
        PrestationDTO savedPrestation = prestationService.create(prestation);

        FactureDTO factureDTO = new FactureDTO();
        factureDTO.setClientId(savedClient.getId());
        LigneFactureDTO ligne = new LigneFactureDTO();
        ligne.setPrestationId(savedPrestation.getId());
        ligne.setQuantite(1.0);
        ligne.setPrixUnitaire(100.0);
        factureDTO.setLignes(List.of(ligne));
        FactureDTO savedFacture = factureService.create(factureDTO);

        factureService.validate(savedFacture.getId());

        // 2. Mark as Paid
        FactureDTO paidFacture = factureService.markAsPaid(savedFacture.getId());

        // 3. Verify
        assertEquals(org.example.anpfacturationbackend.enums.StatutFacture.PAYEE, paidFacture.getStatut());
    }

    @Test
    void testGeneratePdf() {
        // 1. Create Facture
        ClientDTO client = new ClientDTO();
        client.setNom("PdfClient");
        client.setAdresse("Test");
        client.setTelephone("0600");
        ClientDTO savedClient = clientService.create(client);

        PrestationDTO prestation = new PrestationDTO();
        prestation.setCode("P_PDF");
        prestation.setLibelle("P Pdf");
        prestation.setTauxTva(20.0);
        prestation.setTauxTr(0.0);
        prestation.setCompteComptable("7000");
        PrestationDTO savedPrestation = prestationService.create(prestation);

        FactureDTO factureDTO = new FactureDTO();
        factureDTO.setClientId(savedClient.getId());
        LigneFactureDTO ligne = new LigneFactureDTO();
        ligne.setPrestationId(savedPrestation.getId());
        ligne.setQuantite(1.0);
        ligne.setPrixUnitaire(100.0);
        factureDTO.setLignes(List.of(ligne));
        FactureDTO savedFacture = factureService.create(factureDTO);

        // 2. Generate PDF
        byte[] pdfContent = factureService.generatePdf(savedFacture.getId());

        // 3. Verify
        assertNotNull(pdfContent);
        assertTrue(pdfContent.length > 0);
    }

    @Test
    void testCreateFactureClientNotFound() {
        FactureDTO factureDTO = new FactureDTO();
        factureDTO.setClientId(99999L);
        factureDTO.setLignes(List.of());

        assertThrows(RuntimeException.class, () -> {
            factureService.create(factureDTO);
        });
    }
}
