package org.example.anpfacturationbackend.service.impl;

import org.example.anpfacturationbackend.dto.PrestationDTO;
import org.example.anpfacturationbackend.entity.Prestation;
import org.example.anpfacturationbackend.exception.ResourceNotFoundException;
import org.example.anpfacturationbackend.mapper.PrestationMapper;
import org.example.anpfacturationbackend.repository.PrestationRepository;
import org.example.anpfacturationbackend.repository.LigneFactureRepository;
import org.example.anpfacturationbackend.repository.TarifOTDPRepository;
import org.example.anpfacturationbackend.repository.TarifEauElectriciteRepository;
import org.example.anpfacturationbackend.repository.TarifAutorisationRepository;
import org.example.anpfacturationbackend.repository.TarifConcessionRepository;
import org.example.anpfacturationbackend.service.AuditService;
import org.example.anpfacturationbackend.service.PrestationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

@Service
@Transactional
public class PrestationServiceImpl implements PrestationService {

    private final PrestationRepository repository;
    private final PrestationMapper mapper;
    private final AuditService auditService;
    private final LigneFactureRepository ligneFactureRepository;
    private final TarifOTDPRepository tarifOTDPRepository;
    private final TarifEauElectriciteRepository tarifEauElectriciteRepository;
    private final TarifAutorisationRepository tarifAutorisationRepository;
    private final TarifConcessionRepository tarifConcessionRepository;

    public PrestationServiceImpl(PrestationRepository repository, PrestationMapper mapper,
            AuditService auditService,
            LigneFactureRepository ligneFactureRepository,
            TarifOTDPRepository tarifOTDPRepository,
            TarifEauElectriciteRepository tarifEauElectriciteRepository,
            TarifAutorisationRepository tarifAutorisationRepository,
            TarifConcessionRepository tarifConcessionRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.auditService = auditService;
        this.ligneFactureRepository = ligneFactureRepository;
        this.tarifOTDPRepository = tarifOTDPRepository;
        this.tarifEauElectriciteRepository = tarifEauElectriciteRepository;
        this.tarifAutorisationRepository = tarifAutorisationRepository;
        this.tarifConcessionRepository = tarifConcessionRepository;
    }

    @Override
    public List<PrestationDTO> getAll() {
        return repository.findAll().stream().map(mapper::toDTO).toList();
    }

    @Override
    public PrestationDTO getById(@org.springframework.lang.NonNull Long id) {
        Prestation p = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prestation not found"));
        return mapper.toDTO(p);
    }

    @Override
    public PrestationDTO create(PrestationDTO dto) {
        // Validation code moved to service
        if (dto.getCode() != null && repository.existsByCode(dto.getCode())) {
            throw new IllegalArgumentException("Code prestation existe déjà");
        }
        // If code doesn't exist but we want to check, we need the method in repository.
        // Assuming existsByCode is needed.
        Prestation entity = mapper.toEntity(dto);
        // Ensure entity is not null
        Prestation saved = java.util.Objects.requireNonNull(repository.save(java.util.Objects.requireNonNull(entity)));
        auditService.log("CREATE_PRESTATION", "Prestation " + saved.getCode() + " created.");
        return mapper.toDTO(saved);
    }

    @Override
    public PrestationDTO update(@org.springframework.lang.NonNull Long id, PrestationDTO dto) {
        Prestation p = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prestation not found"));

        if (dto.getLibelle() != null) {
            p.setLibelle(dto.getLibelle());
        }
        if (dto.getTauxTva() != null) {
            p.setTauxTva(dto.getTauxTva());
        }
        if (dto.getTauxTr() != null) {
            p.setTauxTr(dto.getTauxTr());
        }
        if (dto.getCompteComptable() != null) {
            p.setCompteComptable(dto.getCompteComptable());
        }

        // Code is usually immutable, but if it needs to be updated, check for
        // uniqueness
        if (dto.getCode() != null && !dto.getCode().equals(p.getCode())) {
            if (repository.existsByCode(dto.getCode())) {
                throw new IllegalArgumentException("Code prestation existe déjà");
            }
            p.setCode(dto.getCode());
        }

        Prestation saved = java.util.Objects.requireNonNull(repository.save(p));
        auditService.log("UPDATE_PRESTATION", "Prestation " + saved.getCode() + " updated.");
        return mapper.toDTO(saved);
    }

    @Override
    public void delete(@org.springframework.lang.NonNull Long id) {
        if (!repository.existsById(id))
            throw new ResourceNotFoundException("Prestation not found");

        // Check for references in invoices
        if (ligneFactureRepository.existsByPrestationId(id)) {
            throw new DataIntegrityViolationException(
                    "Suppression impossible: la prestation est utilisée dans des factures.");
        }

        // Cascade delete configuration (Tarifs)
        tarifOTDPRepository.deleteByPrestationId(id);
        tarifEauElectriciteRepository.deleteByPrestationId(id);
        tarifAutorisationRepository.deleteByPrestationId(id);
        tarifConcessionRepository.deleteByPrestationId(id);

        repository.deleteById(id);
        auditService.log("DELETE_PRESTATION", "Prestation ID " + id + " deleted.");
    }
}