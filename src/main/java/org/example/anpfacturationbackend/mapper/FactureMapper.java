package org.example.anpfacturationbackend.mapper;

import org.example.anpfacturationbackend.dto.FactureDTO;
import org.example.anpfacturationbackend.entity.Client;
import org.example.anpfacturationbackend.entity.Facture;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class FactureMapper {

    private final LigneFactureMapper ligneMapper;

    public FactureMapper(LigneFactureMapper ligneMapper) {
        this.ligneMapper = ligneMapper;
    }

    public FactureDTO toDto(Facture entity) {
        if (entity == null) {
            return null;
        }
        FactureDTO dto = new FactureDTO();
        dto.setId(entity.getId());
        dto.setNumero(entity.getNumero());
        dto.setDate(entity.getDate());
        if (entity.getClient() != null) {
            dto.setClientId(entity.getClient().getId());
            dto.setClientNom(entity.getClient().getNom() + " "
                    + (entity.getClient().getPrenom() != null ? entity.getClient().getPrenom() : ""));
        }
        dto.setStatut(entity.getStatut());
        dto.setMontantHt(entity.getMontantHt());
        dto.setMontantTr(entity.getMontantTr());
        dto.setMontantTva(entity.getMontantTva());
        dto.setMontantTtc(entity.getMontantTtc());

        if (entity.getLignes() != null) {
            dto.setLignes(entity.getLignes().stream()
                    .map(ligneMapper::toDto)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    public Facture toEntity(FactureDTO dto) {
        if (dto == null) {
            return null;
        }
        Facture entity = new Facture();
        entity.setId(dto.getId());
        entity.setNumero(dto.getNumero());
        entity.setDate(dto.getDate());
        if (dto.getClientId() != null) {
            Client c = new Client();
            c.setId(dto.getClientId());
            entity.setClient(c);
        }
        entity.setStatut(dto.getStatut());

        // Lines are handled separately usually to ensure link to parent
        return entity;
    }
}
