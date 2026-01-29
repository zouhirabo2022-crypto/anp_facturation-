package org.example.anpfacturationbackend.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.example.anpfacturationbackend.dto.BulletinDTO;
import org.example.anpfacturationbackend.entity.Prestation;
import org.example.anpfacturationbackend.repository.PrestationRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@Service
public class CsvImportService {

    private final BulletinService bulletinService;
    private final PrestationRepository prestationRepository;

    public CsvImportService(BulletinService bulletinService, PrestationRepository prestationRepository) {
        this.bulletinService = bulletinService;
        this.prestationRepository = prestationRepository;
    }

    public Map<String, Object> importBulletins(MultipartFile file) throws IOException, CsvException {
        Map<String, Object> report = new HashMap<>();
        int successCount = 0;
        int failureCount = 0;
        List<String> errors = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
             CSVReader csvReader = new CSVReader(reader)) {

            List<String[]> rows = csvReader.readAll();
            if (rows.isEmpty()) {
                throw new IllegalArgumentException("Le fichier CSV est vide.");
            }

            // Headers: id_bulletin, client_id, periode, code_prestation, quantite, type_terrain, nature_activite, categorie, code_port, code_activite
            String[] headers = rows.get(0);
            Map<String, Integer> headerMap = new HashMap<>();
            for (int i = 0; i < headers.length; i++) {
                headerMap.put(headers[i].trim().toLowerCase(), i);
            }
            
            // Validate required headers
            String[] required = {"id_bulletin", "client_id", "code_prestation", "quantite"};
            for (String req : required) {
                if (!headerMap.containsKey(req)) {
                     throw new IllegalArgumentException("Colonne manquante: " + req);
                }
            }

            // Group by Bulletin ID
            Map<String, List<String[]>> groupedRows = new LinkedHashMap<>(); // LinkedHashMap to preserve order
            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);
                if (row.length < 2) continue; // Skip empty rows
                
                String bulletinId = getValue(row, headerMap, "id_bulletin");
                if (bulletinId == null || bulletinId.isEmpty()) continue;
                
                groupedRows.computeIfAbsent(bulletinId, k -> new ArrayList<>()).add(row);
            }

            // Process each group
            for (Map.Entry<String, List<String[]>> entry : groupedRows.entrySet()) {
                String bulletinId = entry.getKey();
                List<String[]> bulletinRows = entry.getValue();
                
                try {
                    BulletinDTO dto = new BulletinDTO();
                    dto.setIdBulletinMetier(bulletinId);
                    
                    // Take common fields from the first row
                    String[] firstRow = bulletinRows.get(0);
                    String clientIdStr = getValue(firstRow, headerMap, "client_id");
                    if (clientIdStr == null) throw new IllegalArgumentException("Client ID manquant");
                    dto.setClientId(Long.parseLong(clientIdStr));
                    dto.setPeriodeFacturation(getValue(firstRow, headerMap, "periode"));
                    
                    List<BulletinDTO.LigneBulletinDTO> lignes = new ArrayList<>();
                    for (String[] row : bulletinRows) {
                        BulletinDTO.LigneBulletinDTO ligne = new BulletinDTO.LigneBulletinDTO();
                        String codePresta = getValue(row, headerMap, "code_prestation");
                        if (codePresta == null) throw new IllegalArgumentException("Code prestation manquant");
                        
                        Prestation p = prestationRepository.findByCode(codePresta)
                                .orElseThrow(() -> new IllegalArgumentException("Prestation inconnue: " + codePresta));
                        
                        ligne.setPrestationId(p.getId());
                        
                        String qteStr = getValue(row, headerMap, "quantite");
                        ligne.setQuantite(qteStr != null ? Double.parseDouble(qteStr) : 0.0);
                        
                        // Optional fields
                        ligne.setTypeTerrain(getValue(row, headerMap, "type_terrain"));
                        ligne.setNatureActivite(getValue(row, headerMap, "nature_activite"));
                        ligne.setCategorie(getValue(row, headerMap, "categorie"));
                        ligne.setCodePort(getValue(row, headerMap, "code_port"));
                        ligne.setCodeActivite(getValue(row, headerMap, "code_activite"));
                        
                        lignes.add(ligne);
                    }
                    dto.setLignes(lignes);
                    
                    bulletinService.createBulletin(dto);
                    successCount++;
                    
                } catch (Exception e) {
                    failureCount++;
                    errors.add("Bulletin " + bulletinId + ": " + e.getMessage());
                }
            }
        }

        report.put("success", successCount);
        report.put("failure", failureCount);
        report.put("errors", errors);
        return report;
    }

    private String getValue(String[] row, Map<String, Integer> map, String key) {
        Integer index = map.get(key);
        if (index == null || index >= row.length) return null;
        String val = row[index].trim();
        return val.isEmpty() ? null : val;
    }
}
