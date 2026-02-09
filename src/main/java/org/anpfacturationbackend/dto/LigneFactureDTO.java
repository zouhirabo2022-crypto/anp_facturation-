package org.anpfacturationbackend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public class LigneFactureDTO {
    private Long id;

    @NotNull(message = "L'ID de la prestation est obligatoire")
    private Long prestationId;

    private String prestationLibelle; // Read-only for display

    @NotNull(message = "La quantitÃ© est obligatoire")
    @Positive(message = "La quantitÃ© doit Ãªtre positive")
    private Double quantite;

    @PositiveOrZero(message = "Le prix unitaire doit Ãªtre positif ou nul")
    private Double prixUnitaire;

    // Criteria for tariff selection
    private String typeTerrain;
    private String natureActivite;
    private String categorie;
    private String codePort;
    private String codeActivite;

    private Double tauxTva;
    private Double tauxTr;
    private Double montantHt;
    private Double montantTr;
    private Double montantTva;
    private Double montantTtc;

    public LigneFactureDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPrestationId() {
        return prestationId;
    }

    public void setPrestationId(Long prestationId) {
        this.prestationId = prestationId;
    }

    public String getPrestationLibelle() {
        return prestationLibelle;
    }

    public void setPrestationLibelle(String prestationLibelle) {
        this.prestationLibelle = prestationLibelle;
    }

    public Double getQuantite() {
        return quantite;
    }

    public void setQuantite(Double quantite) {
        this.quantite = quantite;
    }

    public Double getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(Double prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public String getTypeTerrain() {
        return typeTerrain;
    }

    public void setTypeTerrain(String typeTerrain) {
        this.typeTerrain = typeTerrain;
    }

    public String getNatureActivite() {
        return natureActivite;
    }

    public void setNatureActivite(String natureActivite) {
        this.natureActivite = natureActivite;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getCodePort() {
        return codePort;
    }

    public void setCodePort(String codePort) {
        this.codePort = codePort;
    }

    public String getCodeActivite() {
        return codeActivite;
    }

    public void setCodeActivite(String codeActivite) {
        this.codeActivite = codeActivite;
    }

    public Double getTauxTva() {
        return tauxTva;
    }

    public void setTauxTva(Double tauxTva) {
        this.tauxTva = tauxTva;
    }

    public Double getTauxTr() {
        return tauxTr;
    }

    public void setTauxTr(Double tauxTr) {
        this.tauxTr = tauxTr;
    }

    public Double getMontantHt() {
        return montantHt;
    }

    public void setMontantHt(Double montantHt) {
        this.montantHt = montantHt;
    }

    public Double getMontantTr() {
        return montantTr;
    }

    public void setMontantTr(Double montantTr) {
        this.montantTr = montantTr;
    }

    public Double getMontantTva() {
        return montantTva;
    }

    public void setMontantTva(Double montantTva) {
        this.montantTva = montantTva;
    }

    public Double getMontantTtc() {
        return montantTtc;
    }

    public void setMontantTtc(Double montantTtc) {
        this.montantTtc = montantTtc;
    }
}

