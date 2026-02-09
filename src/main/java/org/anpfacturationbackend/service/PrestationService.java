package org.anpfacturationbackend.service;

import org.anpfacturationbackend.dto.PrestationDTO;
import java.util.List;

public interface PrestationService {
    List<PrestationDTO> getAll();

    PrestationDTO getById(@org.springframework.lang.NonNull Long id);

    PrestationDTO create(PrestationDTO dto);

    PrestationDTO update(@org.springframework.lang.NonNull Long id, PrestationDTO dto);

    void delete(@org.springframework.lang.NonNull Long id);
}
