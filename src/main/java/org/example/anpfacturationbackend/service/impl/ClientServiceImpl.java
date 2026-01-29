package org.example.anpfacturationbackend.service.impl;

import org.example.anpfacturationbackend.dto.ClientDTO;
import org.example.anpfacturationbackend.entity.Client;
import org.example.anpfacturationbackend.exception.ResourceNotFoundException;
import org.example.anpfacturationbackend.mapper.ClientMapper;
import org.example.anpfacturationbackend.repository.ClientRepository;
import org.example.anpfacturationbackend.service.ClientService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClientServiceImpl implements ClientService {

    private final ClientRepository repository;
    private final ClientMapper mapper;

    public ClientServiceImpl(ClientRepository repository, ClientMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<ClientDTO> getAll() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ClientDTO getById(@org.springframework.lang.NonNull Long id) {
        Client client = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));
        return mapper.toDto(client);
    }

    @Override
    public ClientDTO create(ClientDTO dto) {
        Client client = mapper.toEntity(dto);
        // Ensure repository returns non-null for the mapper
        Client saved = java.util.Objects.requireNonNull(repository.save(client));
        return mapper.toDto(saved);
    }

    @Override
    public ClientDTO update(@org.springframework.lang.NonNull Long id, ClientDTO dto) {
        Client existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));

        if (dto.getNom() != null) {
            existing.setNom(dto.getNom());
        }
        if (dto.getPrenom() != null) {
            existing.setPrenom(dto.getPrenom());
        }
        if (dto.getAdresse() != null) {
            existing.setAdresse(dto.getAdresse());
        }
        if (dto.getEmail() != null) {
            existing.setEmail(dto.getEmail());
        }
        if (dto.getTelephone() != null) {
            existing.setTelephone(dto.getTelephone());
        }

        Client saved = java.util.Objects.requireNonNull(repository.save(existing));
        return mapper.toDto(saved);
    }

    @Override
    public void delete(@org.springframework.lang.NonNull Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Client not found with id: " + id);
        }
        repository.deleteById(id);
    }
}
