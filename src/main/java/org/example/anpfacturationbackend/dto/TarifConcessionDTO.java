package org.example.anpfacturationbackend.dto;

import jakarta.validation.constraints.NotNull;

public class TarifConcessionDTO {
    private Long id;

    @NotNull(message = "L'ID de la prestation est obligatoire")
    private Long prestationId;

    @NotNull(message = "Le type de contrat est obligatoire")
    private String typeContrat;

    @NotNull(message = "Le montant est obligatoire")
    private Double montant;

    @NotNull(message = "L'année est obligatoire")
    private Integer anneeTarif;

    private Integer anneeDebutRevision;
    private Double tauxRevision;
    private Integer delaiRevision;
    private Boolean actif;

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

    public String getTypeContrat() {
        return typeContrat;
    }

    public void setTypeContrat(String typeContrat) {
        this.typeContrat = typeContrat;
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
