package org.example.anpfacturationbackend.service;

import org.example.anpfacturationbackend.dto.TarifOTDPDTO;
import java.util.List;

public interface TarifOTDPService {
    List<TarifOTDPDTO> getAll();

    List<TarifOTDPDTO> getByPrestationId(Long prestationId);

    TarifOTDPDTO getById(@org.springframework.lang.NonNull Long id);

    TarifOTDPDTO create(TarifOTDPDTO dto);

    TarifOTDPDTO update(@org.springframework.lang.NonNull Long id, TarifOTDPDTO dto);

    void delete(@org.springframework.lang.NonNull Long id);
}
