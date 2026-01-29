package org.example.anpfacturationbackend.service;

import org.example.anpfacturationbackend.dto.ClientDTO;
import java.util.List;

public interface ClientService {
    List<ClientDTO> getAll();

    ClientDTO getById(@org.springframework.lang.NonNull Long id);

    ClientDTO create(ClientDTO dto);

    ClientDTO update(@org.springframework.lang.NonNull Long id, ClientDTO dto);

    void delete(@org.springframework.lang.NonNull Long id);
}
