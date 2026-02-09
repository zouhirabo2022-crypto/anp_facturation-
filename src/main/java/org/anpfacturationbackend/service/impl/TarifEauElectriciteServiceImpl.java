package org.anpfacturationbackend.service.impl;

import org.anpfacturationbackend.dto.TarifEauElectriciteDTO;
import org.anpfacturationbackend.entity.Prestation;
import org.anpfacturationbackend.entity.TarifEauElectricite;
import org.anpfacturationbackend.exception.ResourceNotFoundException;
import org.anpfacturationbackend.mapper.TarifEauElectriciteMapper;
import org.anpfacturationbackend.repository.PrestationRepository;
import org.anpfacturationbackend.repository.TarifEauElectriciteRepository;
import org.anpfacturationbackend.service.TarifEauElectriciteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TarifEauElectriciteServiceImpl implements TarifEauElectriciteService {

    private final TarifEauElectriciteRepository repository;
    private final PrestationRepository prestationRepository;
    private final TarifEauElectriciteMapper mapper;

    public TarifEauElectriciteServiceImpl(TarifEauElectriciteRepository repository,
            PrestationRepository prestationRepository,
            TarifEauElectriciteMapper mapper) {
        this.repository = repository;
        this.prestationRepository = prestationRepository;
        this.mapper = mapper;
    }

    @Override
    public List<TarifEauElectriciteDTO> getAll() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TarifEauElectriciteDTO> getByPrestationId(Long prestationId) {
        return repository.findByPrestationId(prestationId).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public TarifEauElectriciteDTO getById(@org.springframework.lang.NonNull Long id) {
        TarifEauElectricite entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarif not found with id: " + id));
        return mapper.toDto(entity);
    }

    @Override
    public TarifEauElectriciteDTO create(TarifEauElectriciteDTO dto) {
        if (dto.getPrestationId() == null) {
            throw new IllegalArgumentException("Prestation ID is required");
        }

        // Verify prestation exists
        if (!prestationRepository.existsById(java.util.Objects.requireNonNull(dto.getPrestationId()))) {
            throw new ResourceNotFoundException("Prestation not found with id: " + dto.getPrestationId());
        }

        TarifEauElectricite entity = mapper.toEntity(dto);
        return mapper.toDto(repository.save(java.util.Objects.requireNonNull(entity)));
    }

    @Override
    public TarifEauElectriciteDTO update(@org.springframework.lang.NonNull Long id, TarifEauElectriciteDTO dto) {
        TarifEauElectricite existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarif not found with id: " + id));

        if (dto.getPrestationId() != null) {
            Long pId = dto.getPrestationId();
            Prestation p = prestationRepository.findById(java.util.Objects.requireNonNull(pId))
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Prestation not found with id: " + pId));
            existing.setPrestation(p);
        }

        if (dto.getCodePort() != null) existing.setCodePort(dto.getCodePort());
        if (dto.getLibelle() != null) existing.setLibelle(dto.getLibelle());
        if (dto.getCodeActivite() != null) existing.setCodeActivite(dto.getCodeActivite());
        if (dto.getTarifDistributeur() != null) existing.setTarifDistributeur(dto.getTarifDistributeur());
        if (dto.getTarifFacture() != null) existing.setTarifFacture(dto.getTarifFacture());
        if (dto.getAnneeTarif() != null) existing.setAnneeTarif(dto.getAnneeTarif());
        
        if (dto.getAnneeDebutRevision() != null) existing.setAnneeDebutRevision(dto.getAnneeDebutRevision());
        if (dto.getTauxRevision() != null) existing.setTauxRevision(dto.getTauxRevision());
        if (dto.getDelaiRevision() != null) existing.setDelaiRevision(dto.getDelaiRevision());

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

