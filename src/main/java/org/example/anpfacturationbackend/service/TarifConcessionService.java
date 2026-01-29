package org.example.anpfacturationbackend.service;

import org.example.anpfacturationbackend.dto.TarifConcessionDTO;
import java.util.List;

public interface TarifConcessionService {
    List<TarifConcessionDTO> getAll();

    List<TarifConcessionDTO> getByPrestationId(Long prestationId);

    TarifConcessionDTO getById(Long id);

    TarifConcessionDTO create(TarifConcessionDTO dto);

    TarifConcessionDTO update(Long id, TarifConcessionDTO dto);

    void delete(Long id);
}
