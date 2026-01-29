package org.example.anpfacturationbackend.mapper;

import org.example.anpfacturationbackend.dto.TarifConcessionDTO;
import org.example.anpfacturationbackend.entity.Prestation;
import org.example.anpfacturationbackend.entity.TarifConcession;
import org.example.anpfacturationbackend.repository.PrestationRepository;
import org.springframework.stereotype.Component;

@Component
public class TarifConcessionMapper {

    private final PrestationRepository prestationRepository;

    public TarifConcessionMapper(PrestationRepository prestationRepository) {
        this.prestationRepository = prestationRepository;
    }

    public TarifConcessionDTO toDto(TarifConcession entity) {
        if (entity == null)
            return null;
        TarifConcessionDTO dto = new TarifConcessionDTO();
        dto.setId(entity.getId());
        dto.setPrestationId(entity.getPrestation() != null ? entity.getPrestation().getId() : null);
        dto.setTypeContrat(entity.getTypeContrat());
        dto.setMontant(entity.getMontant());
        dto.setAnneeTarif(entity.getAnneeTarif());
        dto.setAnneeDebutRevision(entity.getAnneeDebutRevision());
        dto.setTauxRevision(entity.getTauxRevision());
        dto.setDelaiRevision(entity.getDelaiRevision());
        dto.setActif(entity.getActif());
        return dto;
    }

    public TarifConcession toEntity(TarifConcessionDTO dto) {
        if (dto == null)
            return null;
        TarifConcession entity = new TarifConcession();
        entity.setId(dto.getId());
        if (dto.getPrestationId() != null) {
            Prestation prestation = prestationRepository.findById(dto.getPrestationId()).orElse(null);
            entity.setPrestation(prestation);
        }
        entity.setTypeContrat(dto.getTypeContrat());
        entity.setMontant(dto.getMontant());
        entity.setAnneeTarif(dto.getAnneeTarif());
        entity.setAnneeDebutRevision(dto.getAnneeDebutRevision());
        entity.setTauxRevision(dto.getTauxRevision());
        entity.setDelaiRevision(dto.getDelaiRevision());
        entity.setActif(dto.getActif() != null ? dto.getActif() : true);
        return entity;
    }
}
