package org.example.anpfacturationbackend.service.impl;

import org.example.anpfacturationbackend.dto.TarifConcessionDTO;
import org.example.anpfacturationbackend.entity.TarifConcession;
import org.example.anpfacturationbackend.exception.ResourceNotFoundException;
import org.example.anpfacturationbackend.mapper.TarifConcessionMapper;
import org.example.anpfacturationbackend.repository.TarifConcessionRepository;
import org.example.anpfacturationbackend.service.TarifConcessionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TarifConcessionServiceImpl implements TarifConcessionService {

    private final TarifConcessionRepository repository;
    private final TarifConcessionMapper mapper;

    public TarifConcessionServiceImpl(TarifConcessionRepository repository,
            TarifConcessionMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<TarifConcessionDTO> getAll() {
        return repository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<TarifConcessionDTO> getByPrestationId(Long prestationId) {
        return repository.findByPrestationIdAndActifTrue(prestationId).stream().map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public TarifConcessionDTO getById(Long id) {
        return repository.findById(id).map(mapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("TarifConcession not found with id: " + id));
    }

    @Override
    public TarifConcessionDTO create(TarifConcessionDTO dto) {
        TarifConcession entity = mapper.toEntity(dto);
        return mapper.toDto(repository.save(entity));
    }

    @Override
    public TarifConcessionDTO update(Long id, TarifConcessionDTO dto) {
        TarifConcession existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TarifConcession not found with id: " + id));

        if (dto.getTypeContrat() != null)
            existing.setTypeContrat(dto.getTypeContrat());
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
