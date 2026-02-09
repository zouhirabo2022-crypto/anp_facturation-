package org.anpfacturationbackend.controller;

import org.anpfacturationbackend.service.TarifRevisionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/tariffs/revision")
@PreAuthorize("hasRole('ADMIN_SYSTEME')")
public class TariffRevisionController {

    private final TarifRevisionService revisionService;

    public TariffRevisionController(TarifRevisionService revisionService) {
        this.revisionService = revisionService;
    }

    @PostMapping("/annual")
    public ResponseEntity<?> performAnnualRevision(@RequestParam int year) {
        int count = revisionService.performAnnualRevision(year);
        return ResponseEntity.ok(Map.of("message", "Annual revision completed", "count", count));
    }

    @PostMapping("/otdp")
    public ResponseEntity<?> reviseOTDP(@RequestParam int year) {
        int count = revisionService.reviseTarifsOTDP(year);
        return ResponseEntity.ok(Map.of("message", "OTDP Tariffs revised", "count", count));
    }

    @PostMapping("/eau-elec")
    public ResponseEntity<?> reviseEauElec(@RequestParam int year) {
        int count = revisionService.reviseTarifsEauElectricite(year);
        return ResponseEntity.ok(Map.of("message", "Water/Electricity Tariffs revised", "count", count));
    }

    @PostMapping("/autorisation")
    public ResponseEntity<?> reviseAutorisation(@RequestParam int year) {
        int count = revisionService.reviseTarifsAutorisation(year);
        return ResponseEntity.ok(Map.of("message", "Autorisation Tariffs revised", "count", count));
    }

    @PostMapping("/concession")
    public ResponseEntity<?> reviseConcession(@RequestParam int year) {
        int count = revisionService.reviseTarifsConcession(year);
        return ResponseEntity.ok(Map.of("message", "Concession Tariffs revised", "count", count));
    }
}

