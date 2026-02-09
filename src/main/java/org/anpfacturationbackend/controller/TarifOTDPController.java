package org.anpfacturationbackend.controller;

import jakarta.validation.Valid;
import org.anpfacturationbackend.dto.TarifOTDPDTO;
import org.anpfacturationbackend.service.TarifOTDPService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/tarifs-otdp")
@PreAuthorize("hasAnyRole('ADMIN_SYSTEME', 'GESTIONNAIRE_TARIF', 'CONSULTATION')")
public class TarifOTDPController {

    private final TarifOTDPService service;

    public TarifOTDPController(TarifOTDPService service) {
        this.service = service;
    }

    @GetMapping
    public List<TarifOTDPDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/prestation/{prestationId}")
    public List<TarifOTDPDTO> getByPrestationId(@PathVariable Long prestationId) {
        return service.getByPrestationId(prestationId);
    }

    @GetMapping("/{id}")
    public TarifOTDPDTO getById(@PathVariable @org.springframework.lang.NonNull Long id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN_SYSTEME', 'GESTIONNAIRE_TARIF')")
    public TarifOTDPDTO create(@RequestBody @Valid TarifOTDPDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_SYSTEME', 'GESTIONNAIRE_TARIF')")
    public TarifOTDPDTO update(@PathVariable @org.springframework.lang.NonNull Long id,
            @RequestBody @Valid TarifOTDPDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN_SYSTEME')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @org.springframework.lang.NonNull Long id) {
        service.delete(id);
    }
}

