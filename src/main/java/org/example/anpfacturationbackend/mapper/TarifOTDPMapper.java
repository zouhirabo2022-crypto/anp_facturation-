package org.example.anpfacturationbackend.mapper;

import org.example.anpfacturationbackend.dto.TarifOTDPDTO;
import org.example.anpfacturationbackend.entity.Prestation;
import org.example.anpfacturationbackend.entity.TarifOTDP;
import org.springframework.stereotype.Component;

@Component
public class TarifOTDPMapper {

    public TarifOTDPDTO toDto(@org.springframework.lang.Nullable TarifOTDP entity) {
        if (entity == null) {
            return null;
        }
        TarifOTDPDTO dto = new TarifOTDPDTO();
        dto.setId(entity.getId());
        if (entity.getPrestation() != null) {
            dto.setPrestationId(entity.getPrestation().getId());
        }
        dto.setTypeTerrain(entity.getTypeTerrain());
        dto.setNatureActivite(entity.getNatureActivite());
        dto.setCategorie(entity.getCategorie());
        dto.setUniteBase(entity.getUniteBase());
        dto.setMontant(entity.getMontant());
        dto.setAnneeTarif(entity.getAnneeTarif());
        dto.setAnneeDebutRevision(entity.getAnneeDebutRevision());
        dto.setTauxRevision(entity.getTauxRevision());
        dto.setDelaiRevision(entity.getDelaiRevision());
        dto.setActif(entity.getActif());
        return dto;
    }

    public TarifOTDP toEntity(@org.springframework.lang.Nullable TarifOTDPDTO dto) {
        if (dto == null) {
            return null;
        }
        TarifOTDP entity = new TarifOTDP();
        entity.setId(dto.getId());

        if (dto.getPrestationId() != null) {
            Prestation prestation = new Prestation();
            prestation.setId(dto.getPrestationId());
            entity.setPrestation(prestation);
        }

        entity.setTypeTerrain(dto.getTypeTerrain());
        entity.setNatureActivite(dto.getNatureActivite());
        entity.setCategorie(dto.getCategorie());
        entity.setUniteBase(dto.getUniteBase());
        entity.setMontant(dto.getMontant());
        entity.setAnneeTarif(dto.getAnneeTarif());
        entity.setAnneeDebutRevision(dto.getAnneeDebutRevision());
        entity.setTauxRevision(dto.getTauxRevision());
        entity.setDelaiRevision(dto.getDelaiRevision());
        entity.setActif(dto.getActif());
        return entity;
    }
}
