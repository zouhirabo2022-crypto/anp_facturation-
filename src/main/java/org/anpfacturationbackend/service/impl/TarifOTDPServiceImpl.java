package org.anpfacturationbackend.service.impl;

import org.anpfacturationbackend.dto.TarifOTDPDTO;

import org.anpfacturationbackend.entity.TarifOTDP;
import org.anpfacturationbackend.exception.ResourceNotFoundException;
import org.anpfacturationbackend.mapper.TarifOTDPMapper;
import org.anpfacturationbackend.repository.PrestationRepository;
import org.anpfacturationbackend.repository.TarifOTDPRepository;
import org.anpfacturationbackend.service.TarifOTDPService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TarifOTDPServiceImpl implements TarifOTDPService {

    private final TarifOTDPRepository repository;
    private final PrestationRepository prestationRepository;
    private final TarifOTDPMapper mapper;

    public TarifOTDPServiceImpl(TarifOTDPRepository repository,
            PrestationRepository prestationRepository,
            TarifOTDPMapper mapper) {
        this.repository = repository;
        this.prestationRepository = prestationRepository;
        this.mapper = mapper;
    }

    @Override
    public List<TarifOTDPDTO> getAll() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TarifOTDPDTO> getByPrestationId(Long prestationId) {
        return repository.findByPrestationId(prestationId).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public TarifOTDPDTO getById(@org.springframework.lang.NonNull Long id) {
        TarifOTDP entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarif not found with id: " + id));
        return mapper.toDto(entity);
    }

    @Override
    public TarifOTDPDTO create(TarifOTDPDTO dto) {
        if (dto.getPrestationId() == null) {
            throw new IllegalArgumentException("Prestation ID is required");
        }
        Long prestationId = dto.getPrestationId();

        // Verify prestation exists
        if (!prestationRepository.existsById(java.util.Objects.requireNonNull(prestationId))) {
            throw new ResourceNotFoundException("Prestation not found with id: " + prestationId);
        }

        TarifOTDP entity = mapper.toEntity(dto);
        return mapper.toDto(repository.save(java.util.Objects.requireNonNull(entity)));
    }

    @Override
    public TarifOTDPDTO update(@org.springframework.lang.NonNull Long id, TarifOTDPDTO dto) {
        TarifOTDP existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarif not found with id: " + id));

        if (dto.getPrestationId() != null) {
            Long pId = dto.getPrestationId();
            org.anpfacturationbackend.entity.Prestation p = prestationRepository
                    .findById(java.util.Objects.requireNonNull(pId))
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Prestation not found with id: " + pId));
            existing.setPrestation(p);
        }

        if (dto.getTypeTerrain() != null)
            existing.setTypeTerrain(dto.getTypeTerrain());
        if (dto.getNatureActivite() != null)
            existing.setNatureActivite(dto.getNatureActivite());
        if (dto.getCategorie() != null)
            existing.setCategorie(dto.getCategorie());
        if (dto.getUniteBase() != null)
            existing.setUniteBase(dto.getUniteBase());
        if (dto.getMontant() != null)
            existing.setMontant(dto.getMontant());
        if (dto.getAnneeTarif() != null)
            existing.setAnneeTarif(dto.getAnneeTarif());

        if (dto.getAnneeDebutRevision() != null)
            existing.setAnneeDebutRevision(dto.getAnneeDebutRevision());
        if (dto.getTauxRevision() != null)
            existing.setTauxRevision(dto.getTauxRevision());
        if (dto.getDelaiRevision() != null)
            existing.setDelaiRevision(dto.getDelaiRevision());

        return mapper.toDto(repository.save(java.util.Objects.requireNonNull(existing)));
    }

    @Override
    public void delete(@org.springframework.lang.NonNull Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Tarif not found with id: " + id);
        }
        repository.deleteById(id);
    }
}
