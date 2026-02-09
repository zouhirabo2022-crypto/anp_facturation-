package org.anpfacturationbackend.service;

import org.anpfacturationbackend.dto.TarifAutorisationDTO;
import java.util.List;

public interface TarifAutorisationService {
    List<TarifAutorisationDTO> getAll();

    List<TarifAutorisationDTO> getByPrestationId(Long prestationId);

    TarifAutorisationDTO getById(Long id);

    TarifAutorisationDTO create(TarifAutorisationDTO dto);

    TarifAutorisationDTO update(Long id, TarifAutorisationDTO dto);

    void delete(Long id);
}

