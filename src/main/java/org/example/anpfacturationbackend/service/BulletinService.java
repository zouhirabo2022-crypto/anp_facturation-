package org.example.anpfacturationbackend.service;

import org.example.anpfacturationbackend.dto.BulletinDTO;
import org.example.anpfacturationbackend.dto.FactureDTO;
import org.example.anpfacturationbackend.dto.LigneFactureDTO;
import org.example.anpfacturationbackend.entity.Bulletin;
import org.example.anpfacturationbackend.entity.Client;
import org.example.anpfacturationbackend.entity.LigneBulletin;
import org.example.anpfacturationbackend.enums.StatutBulletin;
import org.example.anpfacturationbackend.exception.ResourceNotFoundException;
import org.example.anpfacturationbackend.repository.BulletinRepository;
import org.example.anpfacturationbackend.repository.ClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BulletinService {

    private final FactureService factureService;
    private final BulletinRepository bulletinRepository;
    private final ClientRepository clientRepository;
    private final AuditService auditService;

    public BulletinService(FactureService factureService, BulletinRepository bulletinRepository, ClientRepository clientRepository, AuditService auditService) {
        this.factureService = factureService;
        this.bulletinRepository = bulletinRepository;
        this.clientRepository = clientRepository;
        this.auditService = auditService;
    }

    /**
     * Importe un bulletin métier et le sauvegarde avec le statut EN_ATTENTE.
     */
    public BulletinDTO createBulletin(BulletinDTO dto) {
        // Check if exists
        if (dto.getIdBulletinMetier() != null && bulletinRepository.findByIdBulletinMetier(dto.getIdBulletinMetier()).isPresent()) {
             throw new IllegalArgumentException("Bulletin avec ID métier " + dto.getIdBulletinMetier() + " existe déjà.");
        }

        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client not found: " + dto.getClientId()));

        Bulletin bulletin = new Bulletin();
        bulletin.setIdBulletinMetier(dto.getIdBulletinMetier());
        bulletin.setClient(client);
        bulletin.setPeriodeFacturation(dto.getPeriodeFacturation());
        bulletin.setStatut(StatutBulletin.EN_ATTENTE);

        if (dto.getLignes() != null) {
            for (BulletinDTO.LigneBulletinDTO lDto : dto.getLignes()) {
                LigneBulletin ligne = new LigneBulletin();
                ligne.setPrestationId(lDto.getPrestationId());
                ligne.setQuantite(lDto.getQuantite());
                ligne.setTypeTerrain(lDto.getTypeTerrain());
                ligne.setNatureActivite(lDto.getNatureActivite());
                ligne.setCategorie(lDto.getCategorie());
                ligne.setCodePort(lDto.getCodePort());
                ligne.setCodeActivite(lDto.getCodeActivite());
                bulletin.addLigne(ligne);
            }
        }

        Bulletin saved = bulletinRepository.save(bulletin);
        auditService.log("IMPORT_BULLETIN", "Bulletin importé: " + saved.getIdBulletinMetier());
        
        return toDto(saved);
    }

    public List<BulletinDTO> getPendingBulletins() {
        return bulletinRepository.findByStatut(StatutBulletin.EN_ATTENTE).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        if (!bulletinRepository.existsById(id)) {
            throw new ResourceNotFoundException("Bulletin not found with id: " + id);
        }
        bulletinRepository.deleteById(id);
        auditService.log("DELETE_BULLETIN", "Bulletin ID " + id + " deleted.");
    }

    /**
     * Transforme un bulletin en facture (brouillon).
     */
    public FactureDTO processBulletin(Long id) {
        Bulletin bulletin = bulletinRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bulletin not found: " + id));

        if (bulletin.getStatut() != StatutBulletin.EN_ATTENTE) {
            throw new IllegalStateException("Le bulletin n'est pas en attente.");
        }

        FactureDTO factureDTO = new FactureDTO();
        factureDTO.setClientId(bulletin.getClient().getId());

        List<LigneFactureDTO> lignes = new ArrayList<>();
        for (LigneBulletin bLigne : bulletin.getLignes()) {
            LigneFactureDTO fLigne = new LigneFactureDTO();
            fLigne.setPrestationId(bLigne.getPrestationId());
            fLigne.setQuantite(bLigne.getQuantite());
            fLigne.setTypeTerrain(bLigne.getTypeTerrain());
            fLigne.setNatureActivite(bLigne.getNatureActivite());
            fLigne.setCategorie(bLigne.getCategorie());
            fLigne.setCodePort(bLigne.getCodePort());
            fLigne.setCodeActivite(bLigne.getCodeActivite());
            lignes.add(fLigne);
        }

        factureDTO.setLignes(lignes);
        FactureDTO createdFacture = factureService.create(factureDTO);

        bulletin.setStatut(StatutBulletin.TRAITE);
        bulletinRepository.save(bulletin);
        
        auditService.log("PROCESS_BULLETIN", "Bulletin " + bulletin.getIdBulletinMetier() + " transformé en facture " + createdFacture.getNumero());

        return createdFacture;
    }

    private BulletinDTO toDto(Bulletin entity) {
        BulletinDTO dto = new BulletinDTO();
        dto.setId(entity.getId());
        dto.setIdBulletinMetier(entity.getIdBulletinMetier());
        dto.setClientId(entity.getClient().getId());
        dto.setClientNom(entity.getClient().getNom() + " " + (entity.getClient().getPrenom() != null ? entity.getClient().getPrenom() : ""));
        dto.setPeriodeFacturation(entity.getPeriodeFacturation());
        dto.setStatut(entity.getStatut().name());
        dto.setDateReception(entity.getDateReception() != null ? entity.getDateReception().toString() : null);
        
        List<BulletinDTO.LigneBulletinDTO> lignesDto = new ArrayList<>();
        for (LigneBulletin l : entity.getLignes()) {
            BulletinDTO.LigneBulletinDTO lDto = new BulletinDTO.LigneBulletinDTO();
            lDto.setPrestationId(l.getPrestationId());
            lDto.setQuantite(l.getQuantite());
            lDto.setTypeTerrain(l.getTypeTerrain());
            lDto.setNatureActivite(l.getNatureActivite());
            lDto.setCategorie(l.getCategorie());
            lDto.setCodePort(l.getCodePort());
            lDto.setCodeActivite(l.getCodeActivite());
            lignesDto.add(lDto);
        }
        dto.setLignes(lignesDto);
        return dto;
    }
}
