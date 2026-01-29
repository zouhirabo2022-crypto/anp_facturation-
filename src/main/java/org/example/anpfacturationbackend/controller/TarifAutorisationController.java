package org.example.anpfacturationbackend.controller;

import jakarta.validation.Valid;
import org.example.anpfacturationbackend.dto.TarifAutorisationDTO;
import org.example.anpfacturationbackend.service.TarifAutorisationService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tarifs-autorisation")
@PreAuthorize("hasAnyRole('ADMIN_SYSTEME', 'GESTIONNAIRE_TARIF', 'CONSULTATION')")
public class TarifAutorisationController {

    private final TarifAutorisationService service;

    public TarifAutorisationController(TarifAutorisationService service) {
        this.service = service;
    }

    @GetMapping
    public List<TarifAutorisationDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/prestation/{prestationId}")
    public List<TarifAutorisationDTO> getByPrestationId(@PathVariable Long prestationId) {
        return service.getByPrestationId(prestationId);
    }

    @GetMapping("/{id}")
    public TarifAutorisationDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN_SYSTEME', 'GESTIONNAIRE_TARIF')")
    public TarifAutorisationDTO create(@RequestBody @Valid TarifAutorisationDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_SYSTEME', 'GESTIONNAIRE_TARIF')")
    public TarifAutorisationDTO update(@PathVariable Long id, @RequestBody @Valid TarifAutorisationDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN_SYSTEME')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
