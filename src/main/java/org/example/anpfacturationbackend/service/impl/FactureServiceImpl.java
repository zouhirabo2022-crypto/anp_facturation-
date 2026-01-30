package org.example.anpfacturationbackend.service.impl;

import org.example.anpfacturationbackend.dto.FactureDTO;
import org.example.anpfacturationbackend.dto.LigneFactureDTO;
import org.example.anpfacturationbackend.entity.*;
import org.example.anpfacturationbackend.enums.StatutFacture;
import org.example.anpfacturationbackend.exception.ResourceNotFoundException;
import org.example.anpfacturationbackend.mapper.FactureMapper;
import org.example.anpfacturationbackend.repository.*;
import org.example.anpfacturationbackend.service.FactureService;
import org.example.anpfacturationbackend.service.AuditService;
import org.example.anpfacturationbackend.service.ExternalIntegrationService;
import org.example.anpfacturationbackend.service.FiscalRateService;
import org.example.anpfacturationbackend.service.PdfService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FactureServiceImpl implements FactureService {

    private final FactureRepository factureRepository;
    private final ClientRepository clientRepository;
    private final PrestationRepository prestationRepository;
    private final TarifOTDPRepository tarifOTDPRepository;
    private final TarifEauElectriciteRepository tarifEauElectriciteRepository;
    private final TarifAutorisationRepository tarifAutorisationRepository;
    private final TarifConcessionRepository tarifConcessionRepository;
    private final PdfService pdfService;
    private final AuditService auditService;
    private final FiscalRateService fiscalRateService;
    private final ExternalIntegrationService externalIntegrationService;
    private final org.example.anpfacturationbackend.service.EmailService emailService;
    private final FactureMapper mapper;

    public FactureServiceImpl(FactureRepository factureRepository,
            ClientRepository clientRepository,
            PrestationRepository prestationRepository,
            TarifOTDPRepository tarifOTDPRepository,
            TarifEauElectriciteRepository tarifEauElectriciteRepository,
            TarifAutorisationRepository tarifAutorisationRepository,
            TarifConcessionRepository tarifConcessionRepository,
            PdfService pdfService,
            AuditService auditService,
            FiscalRateService fiscalRateService,
            ExternalIntegrationService externalIntegrationService,
            org.example.anpfacturationbackend.service.EmailService emailService,
            FactureMapper mapper) {
        this.factureRepository = factureRepository;
        this.clientRepository = clientRepository;
        this.prestationRepository = prestationRepository;
        this.tarifOTDPRepository = tarifOTDPRepository;
        this.tarifEauElectriciteRepository = tarifEauElectriciteRepository;
        this.tarifAutorisationRepository = tarifAutorisationRepository;
        this.tarifConcessionRepository = tarifConcessionRepository;
        this.pdfService = pdfService;
        this.auditService = auditService;
        this.fiscalRateService = fiscalRateService;
        this.externalIntegrationService = externalIntegrationService;
        this.emailService = emailService;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FactureDTO> getAll() {
        return factureRepository
                .findAll(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC,
                        "id"))
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public FactureDTO getById(@org.springframework.lang.NonNull Long id) {
        Facture entity = factureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Facture not found with id: " + id));
        return mapper.toDto(entity);
    }

    @Override
    public FactureDTO create(FactureDTO dto) {
        if (dto.getClientId() == null) {
            throw new IllegalArgumentException("Client ID is required");
        }
        Long clientId = dto.getClientId();
        Client client = clientRepository.findById(java.util.Objects.requireNonNull(clientId))
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + clientId));

        Facture facture = new Facture();
        facture.setClient(client);
        facture.setDate(LocalDate.now());
        facture.setStatut(StatutFacture.BROUILLON);
        facture.setNumero("FACT-" + System.currentTimeMillis()); // Simple generation

        BigDecimal totalHt = BigDecimal.ZERO;
        BigDecimal totalTr = BigDecimal.ZERO;
        BigDecimal totalTva = BigDecimal.ZERO;
        BigDecimal totalTtc = BigDecimal.ZERO;

        for (LigneFactureDTO lineDto : dto.getLignes()) {
            Long prestationId = lineDto.getPrestationId();
            if (prestationId == null) {
                continue;
            }
            Prestation prestation = prestationRepository.findById(java.util.Objects.requireNonNull(prestationId))
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Prestation not found with id: " + prestationId));

            Double quantity = lineDto.getQuantite();
            if (quantity == null || quantity <= 0)
                continue;

            Double unitPrice = lineDto.getPrixUnitaire();

            // 1. Try to find OTDP Tariff
            if (unitPrice == null && lineDto.getTypeTerrain() != null) {
                List<TarifOTDP> otdps = tarifOTDPRepository.findByPrestationIdAndActifTrue(prestation.getId());
                TarifOTDP match = otdps.stream()
                        .filter(t -> t.getTypeTerrain().equals(lineDto.getTypeTerrain()))
                        .filter(t -> lineDto.getNatureActivite() == null
                                || t.getNatureActivite().equals(lineDto.getNatureActivite()))
                        .filter(t -> lineDto.getCategorie() == null || t.getCategorie().equals(lineDto.getCategorie()))
                        .findFirst()
                        .orElse(null);

                if (match != null) {
                    unitPrice = match.getMontant();
                }
            }

            // 2. Try to find Concession Tariff (if not found in OTDP)
            if (unitPrice == null && lineDto.getTypeTerrain() != null) {
                List<TarifConcession> tarifs = tarifConcessionRepository
                        .findByPrestationIdAndActifTrue(prestation.getId());
                TarifConcession match = tarifs.stream()
                        .filter(t -> t.getTypeContrat().equals(lineDto.getTypeTerrain())) // Mapping typeTerrain to
                                                                                          // typeContrat
                        .findFirst()
                        .orElse(null);
                if (match != null) {
                    unitPrice = match.getMontant();
                }
            }

            // 3. Try to find Eau/Elec Tariff
            if (unitPrice == null && lineDto.getCodePort() != null) {
                List<TarifEauElectricite> tariffs = tarifEauElectriciteRepository
                        .findByPrestationIdAndActifTrue(prestation.getId());
                TarifEauElectricite match = tariffs.stream()
                        .filter(t -> t.getCodePort().equals(lineDto.getCodePort()))
                        .filter(t -> lineDto.getCodeActivite() == null
                                || t.getCodeActivite().equals(lineDto.getCodeActivite()))
                        .findFirst()
                        .orElse(null);

                if (match != null) {
                    // Use Tarif Facture as default price
                    unitPrice = match.getTarifFacture();
                }
            }

            // 4. Try to find Autorisation Tariff
            if (unitPrice == null && lineDto.getPrestationId() != null) {
                // If it's a simple prestation name/libelle match
                List<TarifAutorisation> tarifs = tarifAutorisationRepository
                        .findByPrestationIdAndActifTrue(prestation.getId());
                if (!tarifs.isEmpty()) {
                    unitPrice = tarifs.get(0).getMontant(); // Simplification: pick first active
                }
            }

            // 5. Fallback
            if (unitPrice == null) {
                unitPrice = 0.0;
            }

            LigneFacture ligne = createLigne(facture, prestation, quantity, unitPrice);

            totalHt = totalHt.add(BigDecimal.valueOf(ligne.getMontantHt()));
            totalTr = totalTr.add(BigDecimal.valueOf(ligne.getMontantTr()));
            totalTva = totalTva.add(BigDecimal.valueOf(ligne.getMontantTva()));
            totalTtc = totalTtc.add(BigDecimal.valueOf(ligne.getMontantTtc()));

            facture.addLigne(ligne);
        }

        facture.setMontantHt(totalHt.doubleValue());
        facture.setMontantTr(totalTr.doubleValue());
        facture.setMontantTva(totalTva.doubleValue());
        facture.setMontantTtc(totalTtc.doubleValue());

        Facture saved = java.util.Objects.requireNonNull(factureRepository.save(facture));
        auditService.log("CREATE_FACTURE", "Invoice " + saved.getNumero() + " created for client " + client.getNom());
        return mapper.toDto(saved);
    }

    private LigneFacture createLigne(Facture facture, Prestation prestation, Double quantity, Double unitPrice) {
        LigneFacture ligne = new LigneFacture();
        ligne.setFacture(facture);
        ligne.setPrestation(prestation);
        ligne.setQuantite(quantity);
        ligne.setPrixUnitaire(unitPrice);

        ligne.setTauxTva(fiscalRateService.getTvaRate(prestation));
        ligne.setTauxTr(fiscalRateService.getTrRate(prestation));

        BigDecimal qty = BigDecimal.valueOf(quantity);
        BigDecimal price = BigDecimal.valueOf(unitPrice);

        // HT = Qty * Price
        BigDecimal ht = qty.multiply(price).setScale(2, RoundingMode.HALF_UP);

        // TR = HT * (Rate / 100)
        BigDecimal trRate = BigDecimal.valueOf(ligne.getTauxTr()).divide(BigDecimal.valueOf(100));
        BigDecimal tr = ht.multiply(trRate).setScale(2, RoundingMode.HALF_UP);

        // TVA = (HT + TR) * (Rate / 100)
        BigDecimal tvaRate = BigDecimal.valueOf(ligne.getTauxTva()).divide(BigDecimal.valueOf(100));
        BigDecimal baseTva = ht.add(tr);
        BigDecimal tva = baseTva.multiply(tvaRate).setScale(2, RoundingMode.HALF_UP);

        // TTC = HT + TR + TVA
        BigDecimal ttc = ht.add(tr).add(tva).setScale(2, RoundingMode.HALF_UP);

        ligne.setMontantHt(ht.doubleValue());
        ligne.setMontantTr(tr.doubleValue());
        ligne.setMontantTva(tva.doubleValue());
        ligne.setMontantTtc(ttc.doubleValue());

        return ligne;
    }

    @Override
    public void delete(@org.springframework.lang.NonNull Long id) {
        if (!factureRepository.existsById(id)) {
            throw new ResourceNotFoundException("Facture not found with id: " + id);
        }

        // if (facture.getStatut() != StatutFacture.BROUILLON) {
        // throw new IllegalStateException("Impossible de supprimer une facture validée
        // ou payée.");
        // }

        factureRepository.deleteById(id);
        auditService.log("DELETE_FACTURE", "Invoice ID " + id + " deleted.");
    }

    @Override
    public FactureDTO validate(@org.springframework.lang.NonNull Long id) {
        Facture facture = factureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Facture not found with id: " + id));

        if (facture.getStatut() != StatutFacture.BROUILLON) {
            throw new IllegalStateException("Only BROUILLON invoices can be validated");
        }

        facture.setStatut(StatutFacture.VALIDEE);
        facture.setNumero(generateOfficialNumber());

        Facture saved = factureRepository.save(facture);

        auditService.log("VALIDATE_FACTURE", "Invoice " + saved.getNumero() + " validated.");

        // Automatic transmission after validation
        externalIntegrationService.transmitInvoice(saved);
        factureRepository.save(saved); // Update transmission status

        // Send Email with PDF
        sendInvoiceEmail(saved);

        return mapper.toDto(saved);
    }

    private void sendInvoiceEmail(Facture facture) {
        try {
            byte[] pdfBytes = pdfService.generateFacturePdf(facture);
            String subject = "Facture - " + facture.getNumero();
            String body = "Bonjour,\n\nVeuillez trouver ci-joint votre facture n°" + facture.getNumero()
                    + ".\n\nCordialement,\nANP Facturation";

            // Check if client has email
            if (facture.getClient().getEmail() != null && !facture.getClient().getEmail().isEmpty()) {
                emailService.sendMessageWithAttachment(facture.getClient().getEmail(), subject, body, pdfBytes,
                        facture.getNumero() + ".pdf");
                auditService.log("EMAIL_SENT", "Email sent for invoice " + facture.getNumero());
            } else {
                auditService.log("EMAIL_SKIPPED", "No email for client " + facture.getClient().getNom());
            }
        } catch (Exception e) {
            auditService.log("EMAIL_ERROR",
                    "Failed to send email for invoice " + facture.getNumero() + ": " + e.getMessage());
            // Do not fail the main process if email fails
        }
    }

    private synchronized String generateOfficialNumber() {
        int year = LocalDate.now().getYear();
        String prefix = "FACT-" + year + "-";
        String maxNumero = factureRepository.findMaxNumeroByPattern(prefix + "%");

        long nextSequence = 1;
        if (maxNumero != null) {
            // maxNumero format: FACT-YYYY-XXXX
            try {
                String sequencePart = maxNumero.substring(maxNumero.lastIndexOf('-') + 1);
                nextSequence = Long.parseLong(sequencePart) + 1;
            } catch (NumberFormatException e) {
                // Fallback if parsing fails, though with synchronized it should be safe unless
                // manual edits happened
                nextSequence = factureRepository.count() + 1;
            }
        }

        return String.format("%s%04d", prefix, nextSequence);
    }

    @Override
    public FactureDTO retransmit(@org.springframework.lang.NonNull Long id) {
        Facture facture = factureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Facture not found with id: " + id));

        if (facture.getStatut() == StatutFacture.BROUILLON) {
            throw new IllegalStateException("Draft invoices cannot be transmitted. Please validate first.");
        }

        auditService.log("RETRANSMIT_FACTURE", "Manual retransmission triggered for invoice " + facture.getNumero());
        externalIntegrationService.transmitInvoice(facture);
        Facture saved = factureRepository.save(facture);

        // Also resend email
        sendInvoiceEmail(saved);

        return mapper.toDto(saved);
    }

    @Override
    public FactureDTO markAsPaid(@org.springframework.lang.NonNull Long id) {
        Facture facture = factureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Facture not found with id: " + id));

        if (facture.getStatut() != StatutFacture.VALIDEE) {
            throw new IllegalStateException("Only validated invoices can be marked as paid.");
        }

        facture.setStatut(StatutFacture.PAYEE);
        Facture saved = factureRepository.save(facture);

        auditService.log("PAY_FACTURE", "Invoice " + saved.getNumero() + " marked as PAID.");

        return mapper.toDto(saved);
    }

    @Override
    public FactureDTO createAvoir(@org.springframework.lang.NonNull Long id) {
        // 1. Retrieve original invoice
        Facture original = factureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Facture not found with id: " + id));

        // 2. Verify status
        if (original.getStatut() != StatutFacture.VALIDEE && original.getStatut() != StatutFacture.PAYEE) {
            throw new IllegalStateException(
                    "Cannot create a Credit Note for an invoice that is not Validated or Paid.");
        }

        // 3. Create new Facture (Avoir)
        Facture avoir = new Facture();
        avoir.setClient(original.getClient());
        avoir.setDate(LocalDate.now());
        avoir.setStatut(StatutFacture.VALIDEE); // Avoirs are immediately valid
        avoir.setNumero(generateOfficialNumber()); // New sequence number

        BigDecimal totalHt = BigDecimal.ZERO;
        BigDecimal totalTr = BigDecimal.ZERO;
        BigDecimal totalTva = BigDecimal.ZERO;
        BigDecimal totalTtc = BigDecimal.ZERO;

        // 4. Clone lines with negative quantities
        for (LigneFacture originalLine : original.getLignes()) {
            Double negativeQty = originalLine.getQuantite() * -1;
            LigneFacture newLine = createLigne(avoir, originalLine.getPrestation(), negativeQty,
                    originalLine.getPrixUnitaire());

            totalHt = totalHt.add(BigDecimal.valueOf(newLine.getMontantHt()));
            totalTr = totalTr.add(BigDecimal.valueOf(newLine.getMontantTr()));
            totalTva = totalTva.add(BigDecimal.valueOf(newLine.getMontantTva()));
            totalTtc = totalTtc.add(BigDecimal.valueOf(newLine.getMontantTtc()));

            avoir.addLigne(newLine);
        }

        avoir.setMontantHt(totalHt.doubleValue());
        avoir.setMontantTr(totalTr.doubleValue());
        avoir.setMontantTva(totalTva.doubleValue());
        avoir.setMontantTtc(totalTtc.doubleValue());

        // 5. Save and Audit
        Facture savedAvoir = factureRepository.save(avoir);
        auditService.log("CREATE_AVOIR",
                "Credit Note " + savedAvoir.getNumero() + " created based on " + original.getNumero());

        // 6. Send Email (Optional but good practice)
        sendInvoiceEmail(savedAvoir);

        return mapper.toDto(savedAvoir);
    }

    @Override
    public String exportToCsv() {
        List<Facture> factures = factureRepository.findAll();
        StringBuilder csv = new StringBuilder();
        csv.append("Numero;Client;Date;Montant HT;Montant TR;Montant TVA;Montant TTC;Statut;Transmis\n");

        for (Facture f : factures) {
            csv.append(f.getNumero()).append(";")
                    .append(f.getClient().getNom()).append(";")
                    .append(f.getDate()).append(";")
                    .append(f.getMontantHt()).append(";")
                    .append(f.getMontantTr()).append(";")
                    .append(f.getMontantTva()).append(";")
                    .append(f.getMontantTtc()).append(";")
                    .append(f.getStatut()).append(";")
                    .append(f.getTransmissionStatut() != null ? f.getTransmissionStatut() : "-").append("\n");
        }
        return csv.toString();
    }

    @Override
    @Transactional(readOnly = true)
    public Double lookupPrice(Long prestationId, String typeTerrain, String natureActivite, String categorie,
            String codePort, String codeActivite) {
        // 1. Try to find OTDP Tariff
        if (typeTerrain != null && !typeTerrain.isEmpty()) {
            List<TarifOTDP> otdps = tarifOTDPRepository.findByPrestationIdAndActifTrue(prestationId);
            TarifOTDP match = otdps.stream()
                    .filter(t -> t.getTypeTerrain().equals(typeTerrain))
                    .filter(t -> natureActivite == null || natureActivite.isEmpty()
                            || t.getNatureActivite().equals(natureActivite))
                    .filter(t -> categorie == null || categorie.isEmpty() || t.getCategorie().equals(categorie))
                    .findFirst()
                    .orElse(null);

            if (match != null) {
                return match.getMontant();
            }
        }

        // 2. Try to find Eau/Elec Tariff
        if (codePort != null && !codePort.isEmpty()) {
            List<TarifEauElectricite> tariffs = tarifEauElectriciteRepository
                    .findByPrestationIdAndActifTrue(prestationId);
            TarifEauElectricite match = tariffs.stream()
                    .filter(t -> t.getCodePort().equals(codePort))
                    .filter(t -> codeActivite == null || codeActivite.isEmpty()
                            || t.getCodeActivite().equals(codeActivite))
                    .findFirst()
                    .orElse(null);

            if (match != null) {
                return match.getTarifFacture();
            }
        }

        // 3. Try Autorisation
        List<TarifAutorisation> auts = tarifAutorisationRepository.findByPrestationIdAndActifTrue(prestationId);
        if (!auts.isEmpty())
            return auts.get(0).getMontant();

        // 4. Try Concession
        List<TarifConcession> concs = tarifConcessionRepository.findByPrestationIdAndActifTrue(prestationId);
        if (!concs.isEmpty())
            return concs.get(0).getMontant();

        return null;
    }

    @Override
    public byte[] generatePdf(@org.springframework.lang.NonNull Long id) {
        Facture facture = factureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Facture not found with id: " + id));
        try {
            return pdfService.generateFacturePdf(facture);
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public org.example.anpfacturationbackend.dto.DashboardStatsDTO getDashboardStats() {
        org.example.anpfacturationbackend.dto.DashboardStatsDTO stats = new org.example.anpfacturationbackend.dto.DashboardStatsDTO();

        stats.setTotalHt(factureRepository.sumTotalHt() != null ? factureRepository.sumTotalHt() : 0.0);
        stats.setTotalTr(factureRepository.sumTotalTr() != null ? factureRepository.sumTotalTr() : 0.0);
        stats.setTotalTva(factureRepository.sumTotalTva() != null ? factureRepository.sumTotalTva() : 0.0);
        stats.setTotalTtc(factureRepository.sumTotalTtc() != null ? factureRepository.sumTotalTtc() : 0.0);

        stats.setCountBrouillon(factureRepository.countByStatut(StatutFacture.BROUILLON));
        stats.setCountValidee(factureRepository.countByStatut(StatutFacture.VALIDEE));
        stats.setCountPayee(factureRepository.countByStatut(StatutFacture.PAYEE));

        Double amountBrouillon = factureRepository.sumMontantTtcByStatut(StatutFacture.BROUILLON);
        stats.setAmountBrouillon(amountBrouillon != null ? amountBrouillon : 0.0);

        Double amountValidee = factureRepository.sumMontantTtcByStatut(StatutFacture.VALIDEE);
        stats.setAmountValidee(amountValidee != null ? amountValidee : 0.0);

        Double amountPayee = factureRepository.sumMontantTtcByStatut(StatutFacture.PAYEE);
        stats.setAmountPayee(amountPayee != null ? amountPayee : 0.0);

        return stats;
    }
}
