package org.example.anpfacturationbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class TarifOTDPDTO {

    private Long id;

    @NotNull(message = "L'ID de la prestation est obligatoire")
    private Long prestationId;

    @NotBlank(message = "Le type de terrain est obligatoire")
    private String typeTerrain;

    @NotBlank(message = "La nature d'activité est obligatoire")
    private String natureActivite;

    @NotBlank(message = "La catégorie (pêche/autre) est obligatoire")
    private String categorie;

    @NotBlank(message = "L'unité de base est obligatoire")
    private String uniteBase;

    @NotNull(message = "Le montant est obligatoire")
    @PositiveOrZero(message = "Le montant doit être positif ou nul")
    private Double montant;

    @NotNull(message = "L'année tarif est obligatoire")
    private Integer anneeTarif;

    private Integer anneeDebutRevision;
    private Double tauxRevision;
    private Integer delaiRevision;
    private Boolean actif;

    public TarifOTDPDTO() {
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

    public String getUniteBase() {
        return uniteBase;
    }

    public void setUniteBase(String uniteBase) {
        this.uniteBase = uniteBase;
    }

    public Double getMontant() {
        return montant;
    }

    public void setMontant(Double montant) {
        this.montant = montant;
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
