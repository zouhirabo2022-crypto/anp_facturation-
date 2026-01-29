package org.example.anpfacturationbackend.mapper;

import org.example.anpfacturationbackend.dto.TarifEauElectriciteDTO;
import org.example.anpfacturationbackend.entity.Prestation;
import org.example.anpfacturationbackend.entity.TarifEauElectricite;
import org.springframework.stereotype.Component;

@Component
public class TarifEauElectriciteMapper {

    public TarifEauElectriciteDTO toDto(@org.springframework.lang.Nullable TarifEauElectricite entity) {
        if (entity == null) {
            return null;
        }
        TarifEauElectriciteDTO dto = new TarifEauElectriciteDTO();
        dto.setId(entity.getId());
        if (entity.getPrestation() != null) {
            dto.setPrestationId(entity.getPrestation().getId());
        }
        dto.setCodePort(entity.getCodePort());
        dto.setLibelle(entity.getLibelle());
        dto.setCodeActivite(entity.getCodeActivite());
        dto.setTarifDistributeur(entity.getTarifDistributeur());
        dto.setTarifFacture(entity.getTarifFacture());
        dto.setAnneeTarif(entity.getAnneeTarif());
        dto.setAnneeDebutRevision(entity.getAnneeDebutRevision());
        dto.setTauxRevision(entity.getTauxRevision());
        dto.setDelaiRevision(entity.getDelaiRevision());
        dto.setActif(entity.getActif());
        return dto;
    }

    public TarifEauElectricite toEntity(@org.springframework.lang.Nullable TarifEauElectriciteDTO dto) {
        if (dto == null) {
            return null;
        }
        TarifEauElectricite entity = new TarifEauElectricite();
        entity.setId(dto.getId());

        if (dto.getPrestationId() != null) {
            Prestation prestation = new Prestation();
            prestation.setId(dto.getPrestationId());
            entity.setPrestation(prestation);
        }

        entity.setCodePort(dto.getCodePort());
        entity.setLibelle(dto.getLibelle());
        entity.setCodeActivite(dto.getCodeActivite());
        entity.setTarifDistributeur(dto.getTarifDistributeur());
        entity.setTarifFacture(dto.getTarifFacture());
        entity.setAnneeTarif(dto.getAnneeTarif());
        entity.setAnneeDebutRevision(dto.getAnneeDebutRevision());
        entity.setTauxRevision(dto.getTauxRevision());
        entity.setDelaiRevision(dto.getDelaiRevision());
        entity.setActif(dto.getActif());
        return entity;
    }
}
