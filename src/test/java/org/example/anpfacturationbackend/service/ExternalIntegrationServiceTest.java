package org.example.anpfacturationbackend.service;

import org.example.anpfacturationbackend.client.GrcClient;
import org.example.anpfacturationbackend.client.PrestClient;
import org.example.anpfacturationbackend.client.SiFinanceClient;
import org.example.anpfacturationbackend.entity.Facture;
import org.example.anpfacturationbackend.entity.Client;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExternalIntegrationServiceTest {

    @Mock
    private AuditService auditService;

    @Mock
    private SiFinanceClient siFinanceClient;

    @Mock
    private PrestClient prestClient;

    @Mock
    private GrcClient grcClient;

    @InjectMocks
    private ExternalIntegrationService externalIntegrationService;

    private Facture facture;

    @BeforeEach
    void setUp() {
        facture = new Facture();
        facture.setNumero("F2023001");
        facture.setMontantTtc(120.0);
        Client client = new Client();
        client.setNom("TestClient");
        facture.setClient(client);
    }

    @Test
    void transmitInvoice_ShouldReturnTrue_WhenAllSystemsSucceed() {
        // Given
        when(prestClient.transmitInvoice(any())).thenReturn(true);
        when(grcClient.transmitInvoice(any())).thenReturn(true);
        when(siFinanceClient.transmitInvoice(any())).thenReturn(true);

        // When
        boolean result = externalIntegrationService.transmitInvoice(facture);

        // Then
        assertTrue(result);
        verify(prestClient).transmitInvoice(any());
        verify(grcClient).transmitInvoice(any());
        verify(siFinanceClient).transmitInvoice(any());
        verify(auditService).log(eq("TRANSMIT_SUCCESS"), anyString());
    }

    @Test
    void transmitInvoice_ShouldReturnFalse_WhenOneSystemFails() {
        // Given
        when(prestClient.transmitInvoice(any())).thenReturn(true);
        when(grcClient.transmitInvoice(any())).thenReturn(false); // GRC fails
        when(siFinanceClient.transmitInvoice(any())).thenReturn(true);

        // When
        boolean result = externalIntegrationService.transmitInvoice(facture);

        // Then
        assertFalse(result);
        verify(auditService).log(eq("TRANSMIT_FAILED"), anyString());
    }
}
