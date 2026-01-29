package org.example.anpfacturationbackend.service;

import org.example.anpfacturationbackend.entity.Prestation;
import org.example.anpfacturationbackend.entity.TarifOTDP;
import org.example.anpfacturationbackend.repository.TarifAutorisationRepository;
import org.example.anpfacturationbackend.repository.TarifConcessionRepository;
import org.example.anpfacturationbackend.repository.TarifEauElectriciteRepository;
import org.example.anpfacturationbackend.repository.TarifOTDPRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TarifRevisionServiceTest {

    @Mock
    private TarifOTDPRepository tarifOTDPRepository;

    @Mock
    private TarifEauElectriciteRepository tarifEauElectriciteRepository;

    @Mock
    private TarifAutorisationRepository tarifAutorisationRepository;

    @Mock
    private TarifConcessionRepository tarifConcessionRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private TarifRevisionService tarifRevisionService;

    @Test
    void testPerformAnnualRevision_OTDP() {
        // Given
        int currentYear = 2026;
        Prestation p = new Prestation();
        p.setId(1L);
        p.setCode("TEST");

        TarifOTDP oldTarif = new TarifOTDP();
        oldTarif.setId(10L);
        oldTarif.setPrestation(p);
        oldTarif.setTypeTerrain("T1");
        oldTarif.setNatureActivite("N1");
        oldTarif.setCategorie("C1");
        oldTarif.setMontant(100.0);
        oldTarif.setAnneeTarif(2025);
        oldTarif.setActif(true);
        // Revision params: start 2026, +5%, delay 1 year
        oldTarif.setAnneeDebutRevision(2026);
        oldTarif.setTauxRevision(5.0);
        oldTarif.setDelaiRevision(1);

        when(tarifOTDPRepository.findByActifTrue()).thenReturn(List.of(oldTarif));
        // We might need to mock other repositories to return empty lists to avoid NPEs if the service calls them
        // The service calls reviseTarifsEauElectricite, reviseTarifsAutorisation, reviseTarifsConcession
        // These methods call findByActifTrue on their respective repositories.
        // If we don't stub them, they return empty list by default (Mockito default for List), so it should be fine.
        // BUT, if the repository mock itself is null (which was the error), then we get NPE.
        // With the mocks added, the repository fields won't be null.
        // The methods findByActifTrue will return empty list by default.

        // When
        int count = tarifRevisionService.performAnnualRevision(currentYear);

        // Then
        assertEquals(1, count);

        // Verify old tariff deactivated
        verify(tarifOTDPRepository, atLeastOnce())
                .save(argThat(t -> t.getId() != null && t.getId() == 10L && !t.getActif()));

        // Verify new tariff created
        verify(tarifOTDPRepository, atLeastOnce()).save(argThat(t -> t.getId() == null &&
                t.getActif() &&
                t.getAnneeTarif() == 2026 &&
                Math.abs(t.getMontant() - 105.0) < 0.001 // 100 * 1.05
        ));
    }

    @Test
    void testPerformAnnualRevision_NoRevisionNeeded() {
        // Given
        int currentYear = 2026;

        TarifOTDP recentTarif = new TarifOTDP();
        recentTarif.setAnneeTarif(2026); // Already current
        recentTarif.setDelaiRevision(1);
        recentTarif.setActif(true);

        when(tarifOTDPRepository.findByActifTrue()).thenReturn(List.of(recentTarif));
        when(tarifEauElectriciteRepository.findByActifTrue()).thenReturn(List.of());

        // When
        int count = tarifRevisionService.performAnnualRevision(currentYear);

        // Then
        assertEquals(0, count);
        verify(tarifOTDPRepository, never()).save(any());
    }
}
