package org.anpfacturationbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class TarifEauElectriciteDTO {

    private Long id;

    @NotNull(message = "L'ID de la prestation est obligatoire")
    private Long prestationId;

    @NotBlank(message = "Le code port est obligatoire")
    private String codePort;

    @NotBlank(message = "Le libellÃ© est obligatoire")
    private String libelle;

    @NotBlank(message = "Le code activitÃ© est obligatoire")
    private String codeActivite;

    @NotNull(message = "Le tarif distributeur est obligatoire")
    @PositiveOrZero(message = "Le tarif distributeur doit Ãªtre positif ou nul")
    private Double tarifDistributeur;

    @NotNull(message = "Le tarif facturÃ© est obligatoire")
    @PositiveOrZero(message = "Le tarif facturÃ© doit Ãªtre positif ou nul")
    private Double tarifFacture;

    @NotNull(message = "L'annÃ©e tarif est obligatoire")
    private Integer anneeTarif;

    private Integer anneeDebutRevision;
    private Double tauxRevision;
    private Integer delaiRevision;
    private Boolean actif;

    public TarifEauElectriciteDTO() {
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

    public String getCodePort() {
        return codePort;
    }

    public void setCodePort(String codePort) {
        this.codePort = codePort;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getCodeActivite() {
        return codeActivite;
    }

    public void setCodeActivite(String codeActivite) {
        this.codeActivite = codeActivite;
    }

    public Double getTarifDistributeur() {
        return tarifDistributeur;
    }

    public void setTarifDistributeur(Double tarifDistributeur) {
        this.tarifDistributeur = tarifDistributeur;
    }

    public Double getTarifFacture() {
        return tarifFacture;
    }

    public void setTarifFacture(Double tarifFacture) {
        this.tarifFacture = tarifFacture;
    }

    public Integer getAnneeTarif() {
        return anneeTarif;
    }

    public void setAnneeTarif(Integer anneeTarif) {
        this.anneeTarif = anneeTarif;
    }

    public Integer getAnneeDebutRevision() {
        return anneeDebutRevision;
    }

    public void setAnneeDebutRevision(Integer anneeDebutRevision) {
        this.anneeDebutRevision = anneeDebutRevision;
    }

    public Double getTauxRevision() {
        return tauxRevision;
    }

    public void setTauxRevision(Double tauxRevision) {
        this.tauxRevision = tauxRevision;
    }

    public Integer getDelaiRevision() {
        return delaiRevision;
    }

    public void setDelaiRevision(Integer delaiRevision) {
        this.delaiRevision = delaiRevision;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }
}

