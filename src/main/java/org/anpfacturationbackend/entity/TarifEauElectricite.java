package org.anpfacturationbackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;

@Entity
@Table(name = "tarifs_eau_electricite")
public class TarifEauElectricite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prestation_id", nullable = false)
    private Prestation prestation;

    @Column(nullable = false)
    private String codePort;

    @Column(nullable = false)
    private String libelle;

    @Column(nullable = false)
    private String codeActivite;

    @Column(nullable = false)
    @Min(value = 0, message = "Le tarif distributeur doit Ãªtre positif")
    private Double tarifDistributeur;

    @Column(nullable = false)
    @Min(value = 0, message = "Le tarif facture doit Ãªtre positif")
    private Double tarifFacture; // "Tarif" (Selling price)

    @Column(nullable = false)
    private Integer anneeTarif;

    // ParamÃ¨tres de rÃ©vision
    private Integer anneeDebutRevision;
    private Double tauxRevision;
    private Integer delaiRevision;

    @Column(nullable = false)
    private Boolean actif = true;

    public TarifEauElectricite() {
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

    @Override
    public String toString() {
        return "TarifEauElectricite{" +
                "id=" + id +
                ", codePort='" + codePort + '\'' +
                ", libelle='" + libelle + '\'' +
                ", codeActivite='" + codeActivite + '\'' +
                ", tarifDistributeur=" + tarifDistributeur +
                ", tarifFacture=" + tarifFacture +
                ", anneeTarif=" + anneeTarif +
                ", actif=" + actif +
                '}';
    }
}

