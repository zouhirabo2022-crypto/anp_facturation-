package org.anpfacturationbackend.controller.stub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Stub Controller simulating PREST and GRC external systems.
 */
@RestController
@RequestMapping("/api/stub")
public class ExternalSystemsStubController {

    private static final Logger logger = LoggerFactory.getLogger(ExternalSystemsStubController.class);

    @PostMapping("/prest/invoices")
    public ResponseEntity<String> receivePrestInvoice(@RequestBody Map<String, Object> invoiceData) {
        logger.info("[STUB PREST] Received invoice transmission: {}", invoiceData);
        return ResponseEntity.ok("Invoice Received by PREST");
    }

    @PostMapping("/grc/invoices")
    public ResponseEntity<String> receiveGrcInvoice(@RequestBody Map<String, Object> invoiceData) {
        logger.info("[STUB GRC] Received invoice transmission: {}", invoiceData);
        return ResponseEntity.ok("Invoice Received by GRC");
    }
}

