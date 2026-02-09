package org.anpfacturationbackend.mapper;

import org.anpfacturationbackend.dto.LigneFactureDTO;
import org.anpfacturationbackend.entity.LigneFacture;
import org.anpfacturationbackend.entity.Prestation;
import org.springframework.stereotype.Component;

@Component
public class LigneFactureMapper {

    public LigneFactureDTO toDto(LigneFacture entity) {
        if (entity == null) {
            return null;
        }
        LigneFactureDTO dto = new LigneFactureDTO();
        dto.setId(entity.getId());
        if (entity.getPrestation() != null) {
            dto.setPrestationId(entity.getPrestation().getId());
            dto.setPrestationLibelle(entity.getPrestation().getLibelle());
        }
        dto.setQuantite(entity.getQuantite());
        dto.setPrixUnitaire(entity.getPrixUnitaire());
        dto.setTauxTva(entity.getTauxTva());
        dto.setTauxTr(entity.getTauxTr());
        dto.setMontantHt(entity.getMontantHt());
        dto.setMontantTr(entity.getMontantTr());
        dto.setMontantTva(entity.getMontantTva());
        dto.setMontantTtc(entity.getMontantTtc());
        return dto;
    }

    public LigneFacture toEntity(LigneFactureDTO dto) {
        if (dto == null) {
            return null;
        }
        LigneFacture entity = new LigneFacture();
        entity.setId(dto.getId());
        if (dto.getPrestationId() != null) {
            Prestation p = new Prestation();
            p.setId(dto.getPrestationId());
            entity.setPrestation(p);
        }
        entity.setQuantite(dto.getQuantite());
        // Prix and amounts will be calculated by service usually, but we map them if
        // present
        entity.setPrixUnitaire(dto.getPrixUnitaire());
        return entity;
    }
}

