package org.anpfacturationbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.anpfacturationbackend.dto.BulletinDTO;
import org.anpfacturationbackend.dto.FactureDTO;
import org.anpfacturationbackend.service.BulletinService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bulletins")
@Tag(name = "Bulletin MÃ©tier", description = "API pour l'importation des bulletins de prestation")
public class BulletinController {

    private final BulletinService bulletinService;
    private final org.anpfacturationbackend.service.CsvImportService csvImportService;

    public BulletinController(BulletinService bulletinService,
            org.anpfacturationbackend.service.CsvImportService csvImportService) {
        this.bulletinService = bulletinService;
        this.csvImportService = csvImportService;
    }

    @PostMapping(value = "/import/csv", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Importer des bulletins via CSV")
    public ResponseEntity<java.util.Map<String, Object>> importCsv(
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        try {
            return ResponseEntity.ok(csvImportService.importBulletins(file));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/import")
    @Operation(summary = "Importer un bulletin mÃ©tier (mise en attente)")
    public ResponseEntity<BulletinDTO> importBulletin(@RequestBody BulletinDTO bulletinDTO) {
        BulletinDTO created = bulletinService.createBulletin(bulletinDTO);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/pending")
    @Operation(summary = "RÃ©cupÃ©rer la liste des bulletins en attente")
    public ResponseEntity<List<BulletinDTO>> getPendingBulletins() {
        return ResponseEntity.ok(bulletinService.getPendingBulletins());
    }

    @PostMapping("/{id}/process")
    @Operation(summary = "Transformer un bulletin en attente en brouillon de facture")
    public ResponseEntity<FactureDTO> processBulletin(@PathVariable Long id) {
        return ResponseEntity.ok(bulletinService.processBulletin(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un bulletin")
    @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        bulletinService.delete(id);
    }
}
