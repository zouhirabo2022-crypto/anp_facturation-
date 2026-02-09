package org.anpfacturationbackend.mapper;

import org.anpfacturationbackend.dto.PrestationDTO;
import org.anpfacturationbackend.entity.Prestation;
import org.springframework.stereotype.Component;

@Component
public class PrestationMapper {
    public PrestationDTO toDTO(Prestation entity) {
        if (entity == null) {
            return null;
        }
        PrestationDTO dto = new PrestationDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setLibelle(entity.getLibelle());
        dto.setTauxTva(entity.getTauxTva());
        dto.setTauxTr(entity.getTauxTr());
        dto.setCompteComptable(entity.getCompteComptable());
        return dto;
    }

    public Prestation toEntity(PrestationDTO dto) {
        if (dto == null) {
            return null;
        }
        Prestation entity = new Prestation();
        entity.setId(dto.getId());
        entity.setCode(dto.getCode());
        entity.setLibelle(dto.getLibelle());
        entity.setTauxTva(dto.getTauxTva());
        entity.setTauxTr(dto.getTauxTr());
        entity.setCompteComptable(dto.getCompteComptable());
        return entity;
    }
}
