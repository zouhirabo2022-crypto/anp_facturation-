package org.anpfacturationbackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;

@Entity
@Table(name = "tarifs_otdp")
public class TarifOTDP {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prestation_id", nullable = false)
    private org.anpfacturationbackend.entity.Prestation prestation;

    @Column(nullable = false)
    private String typeTerrain;

    @Column(nullable = false)
    private String natureActivite;

    // "Tarif pÃªche / autre" - Using a string to categorize (e.g., "PECHE",
    // "AUTRE")
    @Column(nullable = false)
    private String categorie;

    @Column(nullable = true)
    private String uniteBase;

    @Column(nullable = false)
    @Min(value = 0, message = "Le montant doit Ãªtre positif")
    private Double montant; // The actual tariff value

    @Column(nullable = false)
    private Integer anneeTarif;

    // ParamÃ¨tres de rÃ©vision
    private Integer anneeDebutRevision;
    private Double tauxRevision;
    private Integer delaiRevision;

    @Column(nullable = false)
    private Boolean actif = true;

    public TarifOTDP() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Prestation getPrestation() {
        return prestation;
    }

    public void setPrestation(Prestation prestation) {
        this.prestation = prestation;
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

    @Override
    public String toString() {
        return "TarifOTDP{" +
                "id=" + id +
                ", typeTerrain='" + typeTerrain + '\'' +
                ", natureActivite='" + natureActivite + '\'' +
                ", categorie='" + categorie + '\'' +
                ", montant=" + montant +
                ", anneeTarif=" + anneeTarif +
                ", actif=" + actif +
                '}';
    }
}
