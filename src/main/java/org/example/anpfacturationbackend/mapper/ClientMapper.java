package org.example.anpfacturationbackend.mapper;

import org.example.anpfacturationbackend.dto.ClientDTO;
import org.example.anpfacturationbackend.entity.Client;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper {

    public ClientDTO toDto(Client entity) {
        if (entity == null) {
            return null;
        }
        ClientDTO dto = new ClientDTO();
        dto.setId(entity.getId());
        dto.setNom(entity.getNom());
        dto.setPrenom(entity.getPrenom());
        dto.setAdresse(entity.getAdresse());
        dto.setEmail(entity.getEmail());
        dto.setTelephone(entity.getTelephone());
        dto.setIce(entity.getIce());
        dto.setIfClient(entity.getIfClient());
        dto.setRc(entity.getRc());
        return dto;
    }

    public Client toEntity(ClientDTO dto) {
        if (dto == null) {
            return null;
        }
        Client entity = new Client();
        entity.setId(dto.getId());
        entity.setNom(dto.getNom());
        entity.setPrenom(dto.getPrenom());
        entity.setAdresse(dto.getAdresse());
        entity.setEmail(dto.getEmail());
        entity.setTelephone(dto.getTelephone());
        entity.setIce(dto.getIce());
        entity.setIfClient(dto.getIfClient());
        entity.setRc(dto.getRc());
        return entity;
    }
}
