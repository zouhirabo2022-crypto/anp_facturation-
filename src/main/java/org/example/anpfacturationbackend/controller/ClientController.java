package org.example.anpfacturationbackend.controller;

import jakarta.validation.Valid;
import org.example.anpfacturationbackend.dto.ClientDTO;
import org.example.anpfacturationbackend.service.ClientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/clients")
@PreAuthorize("hasAnyRole('ADMIN_SYSTEME', 'GESTIONNAIRE_PARAM', 'GESTIONNAIRE_TARIF', 'CONSULTATION')")
public class ClientController {

    private final ClientService service;

    public ClientController(ClientService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<ClientDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientDTO> getById(@PathVariable @org.springframework.lang.NonNull Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN_SYSTEME', 'GESTIONNAIRE_PARAM', 'GESTIONNAIRE_TARIF')")
    public ResponseEntity<ClientDTO> create(@Valid @RequestBody ClientDTO dto) {
        return new ResponseEntity<>(service.create(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_SYSTEME', 'GESTIONNAIRE_PARAM', 'GESTIONNAIRE_TARIF')")
    public ResponseEntity<ClientDTO> update(@PathVariable @org.springframework.lang.NonNull Long id,
            @Valid @RequestBody ClientDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN_SYSTEME')")
    public ResponseEntity<Void> delete(@PathVariable @org.springframework.lang.NonNull Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
