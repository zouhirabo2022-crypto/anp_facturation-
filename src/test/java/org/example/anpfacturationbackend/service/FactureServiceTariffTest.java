package org.example.anpfacturationbackend.service;

import org.example.anpfacturationbackend.entity.*;
import org.example.anpfacturationbackend.repository.*;
import org.example.anpfacturationbackend.service.impl.FactureServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FactureServiceTariffTest {

    @Mock
    private TarifOTDPRepository tarifOTDPRepository;
    @Mock
    private TarifEauElectriciteRepository tarifEauElectriciteRepository;
    @Mock
    private TarifAutorisationRepository tarifAutorisationRepository;
    @Mock
    private TarifConcessionRepository tarifConcessionRepository;

    // We mock other dependencies to avoid NullPointerExceptions during
    // initialization
    @Mock
    private org.example.anpfacturationbackend.repository.FactureRepository factureRepository;
    @Mock
    private org.example.anpfacturationbackend.repository.ClientRepository clientRepository;
    @Mock
    private org.example.anpfacturationbackend.repository.PrestationRepository prestationRepository;
    @Mock
    private org.example.anpfacturationbackend.service.PdfService pdfService;
    @Mock
    private org.example.anpfacturationbackend.service.AuditService auditService;
    @Mock
    private org.example.anpfacturationbackend.service.FiscalRateService fiscalRateService;
    @Mock
    private org.example.anpfacturationbackend.service.ExternalIntegrationService externalIntegrationService;
    @Mock
    private org.example.anpfacturationbackend.service.EmailService emailService;
    @Mock
    private org.example.anpfacturationbackend.mapper.FactureMapper mapper;

    @InjectMocks
    private FactureServiceImpl factureService;

    private Long prestationId = 1L;

    @Test
    void shouldCalculateOTDPTariff_WhenMatchFound() {
        // Arrange
        String typeTerrain = "Zone-A";
        String natureActivite = "Logistique";

        TarifOTDP tarif = new TarifOTDP();
        tarif.setTypeTerrain(typeTerrain);
        tarif.setNatureActivite(natureActivite);
        tarif.setCategorie("Standard");
        tarif.setMontant(50.0);

        when(tarifOTDPRepository.findByPrestationIdAndActifTrue(prestationId))
                .thenReturn(List.of(tarif));

        // Act
        Double price = factureService.lookupPrice(prestationId, typeTerrain, natureActivite, "Standard", null, null);

        // Assert
        assertThat(price).isEqualTo(50.0);
    }

    @Test
    void shouldCalculateEauTariff_WhenMatchFound() {
        // Arrange
        String codePort = "CAS";
        String codeActivite = "EAU001";

        TarifEauElectricite tarif = new TarifEauElectricite();
        tarif.setCodePort(codePort);
        tarif.setCodeActivite(codeActivite);
        tarif.setTarifFacture(15.5);

        when(tarifEauElectriciteRepository.findByPrestationIdAndActifTrue(prestationId))
                .thenReturn(List.of(tarif));

        // Act
        Double price = factureService.lookupPrice(prestationId, null, null, null, codePort, codeActivite);

        // Assert
        assertThat(price).isEqualTo(15.5);
    }

    @Test
    void shouldReturnNull_WhenNoTariffMatch() {
        // Arrange
        when(tarifOTDPRepository.findByPrestationIdAndActifTrue(prestationId))
                .thenReturn(Collections.emptyList());

        // Act
        Double price = factureService.lookupPrice(prestationId, "UnknownZone", null, null, null, null);

        // Assert
        assertThat(price).isNull();
    }
}
