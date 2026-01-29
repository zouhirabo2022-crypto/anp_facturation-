package org.example.anpfacturationbackend.controller;

import jakarta.validation.Valid;
import org.example.anpfacturationbackend.dto.PrestationDTO;
import org.example.anpfacturationbackend.service.PrestationService;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/prestations")
@PreAuthorize("isAuthenticated()")
public class PrestationController {

    private final PrestationService service;

    public PrestationController(PrestationService service) {
        this.service = service;
    }

    @GetMapping
    public List<PrestationDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public PrestationDTO getById(@PathVariable @org.springframework.lang.NonNull Long id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN_SYSTEME', 'GESTIONNAIRE_PARAM', 'GESTIONNAIRE_TARIF')")
    public PrestationDTO create(@RequestBody @Valid PrestationDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_SYSTEME', 'GESTIONNAIRE_PARAM', 'GESTIONNAIRE_TARIF')")
    public PrestationDTO update(@PathVariable @org.springframework.lang.NonNull Long id,
            @RequestBody @Valid PrestationDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_SYSTEME', 'GESTIONNAIRE_PARAM', 'GESTIONNAIRE_TARIF')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @org.springframework.lang.NonNull Long id) {
        service.delete(id);
    }
}
