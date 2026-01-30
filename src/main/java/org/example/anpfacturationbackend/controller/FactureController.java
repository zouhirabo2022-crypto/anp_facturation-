package org.example.anpfacturationbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.example.anpfacturationbackend.dto.FactureDTO;
import org.example.anpfacturationbackend.dto.DashboardStatsDTO;
import org.example.anpfacturationbackend.service.FactureService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/factures")
@PreAuthorize("isAuthenticated()")
public class FactureController {

    private final FactureService service;

    public FactureController(FactureService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<FactureDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats() {
        return ResponseEntity.ok(service.getDashboardStats());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FactureDTO> getById(@PathVariable @org.springframework.lang.NonNull Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN_SYSTEME', 'GESTIONNAIRE_PARAM', 'GESTIONNAIRE_TARIF')")
    public ResponseEntity<FactureDTO> create(@Valid @RequestBody FactureDTO dto) {
        return new ResponseEntity<>(service.create(dto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN_SYSTEME')")
    public ResponseEntity<Void> delete(@PathVariable @org.springframework.lang.NonNull Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/validate")
    @PreAuthorize("hasAnyRole('ADMIN_SYSTEME', 'GESTIONNAIRE_PARAM', 'GESTIONNAIRE_TARIF')")
    public ResponseEntity<FactureDTO> validate(@PathVariable @org.springframework.lang.NonNull Long id) {
        return ResponseEntity.ok(service.validate(id));
    }

    @PostMapping("/{id}/retransmit")
    @PreAuthorize("hasAnyRole('ADMIN_SYSTEME', 'GESTIONNAIRE_PARAM', 'GESTIONNAIRE_TARIF')")
    public ResponseEntity<FactureDTO> retransmit(@PathVariable @org.springframework.lang.NonNull Long id) {
        return ResponseEntity.ok(service.retransmit(id));
    }

    @PostMapping("/{id}/pay")
    @PreAuthorize("hasAnyRole('ADMIN_SYSTEME', 'GESTIONNAIRE_PARAM')")
    public ResponseEntity<FactureDTO> markAsPaid(@PathVariable @org.springframework.lang.NonNull Long id) {
        return ResponseEntity.ok(service.markAsPaid(id));
    }

    @PostMapping("/{id}/avoir")
    @Operation(summary = "Générer un Avoir (Facture négative) pour annuler une facture validée")
    @PreAuthorize("hasAnyRole('ADMIN_SYSTEME', 'GESTIONNAIRE_PARAM')")
    public ResponseEntity<FactureDTO> createAvoir(@PathVariable @org.springframework.lang.NonNull Long id) {
        return ResponseEntity.ok(service.createAvoir(id));
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> getPdf(@PathVariable @org.springframework.lang.NonNull Long id) {
        byte[] pdf = service.generatePdf(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=facture-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/{id}/preview")
    @Operation(summary = "Aperçu PDF de la facture (affichage navigateur)")
    public ResponseEntity<byte[]> previewPdf(@PathVariable @org.springframework.lang.NonNull Long id) {
        byte[] pdf = service.generatePdf(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=facture-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/export/csv")
    @PreAuthorize("hasAnyRole('ADMIN_SYSTEME', 'GESTIONNAIRE_PARAM')")
    public ResponseEntity<String> exportCsv() {
        String csv = service.exportToCsv();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=factures.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }

    @GetMapping("/lookup-price")
    public ResponseEntity<Double> getPricePreview(
            @RequestParam Long prestationId,
            @RequestParam(required = false) String typeTerrain,
            @RequestParam(required = false) String natureActivite,
            @RequestParam(required = false) String categorie,
            @RequestParam(required = false) String codePort,
            @RequestParam(required = false) String codeActivite) {
        return ResponseEntity
                .ok(service.lookupPrice(prestationId, typeTerrain, natureActivite, categorie, codePort, codeActivite));
    }
}
