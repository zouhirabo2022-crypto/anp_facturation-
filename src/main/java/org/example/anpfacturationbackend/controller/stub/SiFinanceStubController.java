package org.example.anpfacturationbackend.controller.stub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Stub Controller simulating the SI Finance external system.
 * Allows testing integration via HTTP calls.
 */
@RestController
@RequestMapping("/api/stub/si-finance")
public class SiFinanceStubController {

    private static final Logger logger = LoggerFactory.getLogger(SiFinanceStubController.class);

    @GetMapping("/rates/{prestationCode}")
    public ResponseEntity<Map<String, Double>> getFiscalRates(@PathVariable String prestationCode) {
        logger.info("[STUB SI FINANCE] Received rate request for code: {}", prestationCode);
        
        // Simulate business logic of SI Finance
        if (prestationCode != null) {
            if (prestationCode.contains("EAU")) {
                return ResponseEntity.ok(Map.of("TVA", 7.0, "TR", 0.0));
            } else if (prestationCode.contains("ELEC")) {
                return ResponseEntity.ok(Map.of("TVA", 14.0, "TR", 0.0));
            } else if (prestationCode.contains("OTDP")) {
                return ResponseEntity.ok(Map.of("TVA", 20.0, "TR", 5.0));
            }
        }
        
        // Default fallback
        return ResponseEntity.ok(Map.of("TVA", 20.0, "TR", 0.0));
    }

    @PostMapping("/invoices")
    public ResponseEntity<String> receiveInvoice(@RequestBody Map<String, Object> invoiceData) {
        logger.info("[STUB SI FINANCE] Received invoice transmission: {}", invoiceData);
        
        // Simulate validation/acceptance
        if (invoiceData.containsKey("numero")) {
             String numero = (String) invoiceData.get("numero");
             if (numero != null && numero.startsWith("FAIL")) {
                 logger.warn("[STUB SI FINANCE] Simulating rejection for invoice: {}", numero);
                 return ResponseEntity.badRequest().body("Simulated Rejection");
             }
        }
        
        return ResponseEntity.ok("Invoice Received Successfully");
    }
}
