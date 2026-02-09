package org.anpfacturationbackend.service;

import org.anpfacturationbackend.dto.TarifEauElectriciteDTO;
import java.util.List;

public interface TarifEauElectriciteService {
    List<TarifEauElectriciteDTO> getAll();

    List<TarifEauElectriciteDTO> getByPrestationId(Long prestationId);

    TarifEauElectriciteDTO getById(@org.springframework.lang.NonNull Long id);

    TarifEauElectriciteDTO create(TarifEauElectriciteDTO dto);

    TarifEauElectriciteDTO update(@org.springframework.lang.NonNull Long id, TarifEauElectriciteDTO dto);

    void delete(@org.springframework.lang.NonNull Long id);
}

