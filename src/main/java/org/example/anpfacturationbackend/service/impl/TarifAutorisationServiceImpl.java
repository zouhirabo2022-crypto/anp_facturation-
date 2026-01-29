package org.example.anpfacturationbackend.service.impl;

import org.example.anpfacturationbackend.dto.TarifAutorisationDTO;
import org.example.anpfacturationbackend.entity.TarifAutorisation;
import org.example.anpfacturationbackend.exception.ResourceNotFoundException;
import org.example.anpfacturationbackend.mapper.TarifAutorisationMapper;
import org.example.anpfacturationbackend.repository.TarifAutorisationRepository;
import org.example.anpfacturationbackend.service.TarifAutorisationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TarifAutorisationServiceImpl implements TarifAutorisationService {

    private final TarifAutorisationRepository repository;
    private final TarifAutorisationMapper mapper;

    public TarifAutorisationServiceImpl(TarifAutorisationRepository repository,
            TarifAutorisationMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<TarifAutorisationDTO> getAll() {
        return repository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<TarifAutorisationDTO> getByPrestationId(Long prestationId) {
        return repository.findByPrestationIdAndActifTrue(prestationId).stream().map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public TarifAutorisationDTO getById(Long id) {
        return repository.findById(id).map(mapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("TarifAutorisation not found with id: " + id));
    }

    @Override
    public TarifAutorisationDTO create(TarifAutorisationDTO dto) {
        TarifAutorisation entity = mapper.toEntity(dto);
        return mapper.toDto(repository.save(entity));
    }

    @Override
    public TarifAutorisationDTO update(Long id, TarifAutorisationDTO dto) {
        TarifAutorisation existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TarifAutorisation not found with id: " + id));

        if (dto.getLibelle() != null)
            existing.setLibelle(dto.getLibelle());
        if (dto.getMontant() != null)
            existing.setMontant(dto.getMontant());
        if (dto.getAnneeTarif() != null)
            existing.setAnneeTarif(dto.getAnneeTarif());
        if (dto.getTauxRevision() != null)
            existing.setTauxRevision(dto.getTauxRevision());
        if (dto.getDelaiRevision() != null)
            existing.setDelaiRevision(dto.getDelaiRevision());
        if (dto.getActif() != null)
            existing.setActif(dto.getActif());

        return mapper.toDto(repository.save(existing));
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
