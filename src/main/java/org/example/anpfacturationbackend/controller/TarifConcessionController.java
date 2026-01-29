package org.example.anpfacturationbackend.controller;

import jakarta.validation.Valid;
import org.example.anpfacturationbackend.dto.TarifConcessionDTO;
import org.example.anpfacturationbackend.service.TarifConcessionService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tarifs-concession")
@PreAuthorize("hasAnyRole('ADMIN_SYSTEME', 'GESTIONNAIRE_TARIF', 'CONSULTATION')")
public class TarifConcessionController {

    private final TarifConcessionService service;

    public TarifConcessionController(TarifConcessionService service) {
        this.service = service;
    }

    @GetMapping
    public List<TarifConcessionDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/prestation/{prestationId}")
    public List<TarifConcessionDTO> getByPrestationId(@PathVariable Long prestationId) {
        return service.getByPrestationId(prestationId);
    }

    @GetMapping("/{id}")
    public TarifConcessionDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN_SYSTEME', 'GESTIONNAIRE_TARIF')")
    public TarifConcessionDTO create(@RequestBody @Valid TarifConcessionDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_SYSTEME', 'GESTIONNAIRE_TARIF')")
    public TarifConcessionDTO update(@PathVariable Long id, @RequestBody @Valid TarifConcessionDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN_SYSTEME')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
