package org.example.anpfacturationbackend.service;

import org.example.anpfacturationbackend.dto.FactureDTO;
import java.util.List;

public interface FactureService {
    List<FactureDTO> getAll();

    FactureDTO getById(@org.springframework.lang.NonNull Long id);

    FactureDTO create(FactureDTO dto);

    // update is tricky for invoices, usually we can only update if draft.
    // delete also.
    void delete(@org.springframework.lang.NonNull Long id);

    FactureDTO validate(@org.springframework.lang.NonNull Long id);

    FactureDTO retransmit(@org.springframework.lang.NonNull Long id);

    FactureDTO markAsPaid(@org.springframework.lang.NonNull Long id);

    FactureDTO createAvoir(@org.springframework.lang.NonNull Long id);

    String exportToCsv();

    Double lookupPrice(Long prestationId, String typeTerrain, String natureActivite, String categorie, String codePort,
            String codeActivite);

    byte[] generatePdf(@org.springframework.lang.NonNull Long id);

    org.example.anpfacturationbackend.dto.DashboardStatsDTO getDashboardStats();
}
