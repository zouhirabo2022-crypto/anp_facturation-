package org.anpfacturationbackend.service;

import org.anpfacturationbackend.client.GrcClient;
import org.anpfacturationbackend.client.PrestClient;
import org.anpfacturationbackend.client.SiFinanceClient;
import org.anpfacturationbackend.entity.Facture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Service to handle transmission of validated invoices to external systems
 * (PREST, GRC & SI Finance).
 * As per specifications, these are destination systems for Technical, CRM and Financial data.
 */
@Service
public class ExternalIntegrationService {

    private static final Logger logger = LoggerFactory.getLogger(ExternalIntegrationService.class);
    private final AuditService auditService;
    private final SiFinanceClient siFinanceClient;
    private final PrestClient prestClient;
    private final GrcClient grcClient;

    public ExternalIntegrationService(AuditService auditService,
                                      SiFinanceClient siFinanceClient,
                                      PrestClient prestClient,
                                      GrcClient grcClient) {
        this.auditService = auditService;
        this.siFinanceClient = siFinanceClient;
        this.prestClient = prestClient;
        this.grcClient = grcClient;
    }

    public boolean transmitInvoice(Facture facture) {
        logger.info("Initiating transmission for invoice {}", facture.getNumero());

        Map<String, Object> invoiceData = new HashMap<>();
        invoiceData.put("numero", facture.getNumero());
        invoiceData.put("montantTtc", facture.getMontantTtc());
        invoiceData.put("date", facture.getDate() != null ? facture.getDate().toString() : null);
        invoiceData.put("client", facture.getClient() != null ? facture.getClient().getNom() : "Unknown");

        // Call PREST
        boolean successPrest = prestClient.transmitInvoice(invoiceData);

        // Call GRC
        boolean successGrc = grcClient.transmitInvoice(invoiceData);

        // Call SI Finance
        boolean successFinance = siFinanceClient.transmitInvoice(invoiceData);

        boolean overallSuccess = successPrest && successGrc && successFinance;

        if (overallSuccess) {
            facture.setTransmissionStatut("SUCCESS");
            facture.setDateTransmission(LocalDateTime.now());
            auditService.log("TRANSMIT_SUCCESS",
                    "Invoice " + facture.getNumero() + " successfully transmitted to PREST, GRC & SI Finance.");
            logger.info("Transmission successful for invoice {}", facture.getNumero());
        } else {
            facture.setTransmissionStatut("FAILED");
            auditService.log("TRANSMIT_FAILED",
                    "Invoice " + facture.getNumero() + " failed to transmit to external systems.");
            logger.error("Transmission failed for invoice {}", facture.getNumero());
        }

        return overallSuccess;
    }
}

