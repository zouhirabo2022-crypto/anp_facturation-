package org.anpfacturationbackend.controller;

import jakarta.validation.Valid;
import org.anpfacturationbackend.dto.TarifEauElectriciteDTO;
import org.anpfacturationbackend.service.TarifEauElectriciteService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/tarifs-eau-electricite")
@PreAuthorize("hasAnyRole('ADMIN_SYSTEME', 'GESTIONNAIRE_TARIF', 'CONSULTATION')")
public class TarifEauElectriciteController {

    private final TarifEauElectriciteService service;

    public TarifEauElectriciteController(TarifEauElectriciteService service) {
        this.service = service;
    }

    @GetMapping
    public List<TarifEauElectriciteDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/prestation/{prestationId}")
    public List<TarifEauElectriciteDTO> getByPrestationId(@PathVariable Long prestationId) {
        return service.getByPrestationId(prestationId);
    }

    @GetMapping("/{id}")
    public TarifEauElectriciteDTO getById(@PathVariable @org.springframework.lang.NonNull Long id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN_SYSTEME', 'GESTIONNAIRE_TARIF')")
    public TarifEauElectriciteDTO create(@RequestBody @Valid TarifEauElectriciteDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_SYSTEME', 'GESTIONNAIRE_TARIF')")
    public TarifEauElectriciteDTO update(@PathVariable @org.springframework.lang.NonNull Long id,
            @RequestBody @Valid TarifEauElectriciteDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN_SYSTEME')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @org.springframework.lang.NonNull Long id) {
        service.delete(id);
    }
}

