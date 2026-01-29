package org.example.anpfacturationbackend.mapper;

import org.example.anpfacturationbackend.dto.TarifAutorisationDTO;
import org.example.anpfacturationbackend.entity.Prestation;
import org.example.anpfacturationbackend.entity.TarifAutorisation;
import org.example.anpfacturationbackend.repository.PrestationRepository;
import org.springframework.stereotype.Component;

@Component
public class TarifAutorisationMapper {

    private final PrestationRepository prestationRepository;

    public TarifAutorisationMapper(PrestationRepository prestationRepository) {
        this.prestationRepository = prestationRepository;
    }

    public TarifAutorisationDTO toDto(TarifAutorisation entity) {
        if (entity == null)
            return null;
        TarifAutorisationDTO dto = new TarifAutorisationDTO();
        dto.setId(entity.getId());
        dto.setPrestationId(entity.getPrestation() != null ? entity.getPrestation().getId() : null);
        dto.setLibelle(entity.getLibelle());
        dto.setMontant(entity.getMontant());
        dto.setAnneeTarif(entity.getAnneeTarif());
        dto.setAnneeDebutRevision(entity.getAnneeDebutRevision());
        dto.setTauxRevision(entity.getTauxRevision());
        dto.setDelaiRevision(entity.getDelaiRevision());
        dto.setActif(entity.getActif());
        return dto;
    }

    public TarifAutorisation toEntity(TarifAutorisationDTO dto) {
        if (dto == null)
            return null;
        TarifAutorisation entity = new TarifAutorisation();
        entity.setId(dto.getId());
        if (dto.getPrestationId() != null) {
            Prestation prestation = prestationRepository.findById(dto.getPrestationId()).orElse(null);
            entity.setPrestation(prestation);
        }
        entity.setLibelle(dto.getLibelle());
        entity.setMontant(dto.getMontant());
        entity.setAnneeTarif(dto.getAnneeTarif());
        entity.setAnneeDebutRevision(dto.getAnneeDebutRevision());
        entity.setTauxRevision(dto.getTauxRevision());
        entity.setDelaiRevision(dto.getDelaiRevision());
        entity.setActif(dto.getActif() != null ? dto.getActif() : true);
        return entity;
    }
}
