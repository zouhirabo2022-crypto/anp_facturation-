package org.anpfacturationbackend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "ligne_facture")
public class LigneFacture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facture_id", nullable = false)
    private Facture facture;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prestation_id", nullable = false)
    private Prestation prestation;

    @Column(nullable = false)
    private Double quantite;

    @Column(nullable = false)
    private Double prixUnitaire;

    @Column(nullable = false)
    private Double tauxTva;

    @Column(nullable = false)
    private Double montantHt;

    @Column(nullable = false)
    private Double tauxTr = 0.0;

    @Column(nullable = false)
    private Double montantTr = 0.0;

    @Column(nullable = false)
    private Double montantTva;

    @Column(nullable = false)
    private Double montantTtc;

    // Criteria for tariff selection (stored for reference/recalculation)
    @Column(length = 50)
    private String typeTerrain;

    @Column(length = 100)
    private String natureActivite;

    @Column(length = 50)
    private String categorie;

    @Column(length = 50)
    private String codePort;

    @Column(length = 50)
    private String codeActivite;

    public LigneFacture() {
    }

    // Getters and Setters

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Facture getFacture() {
        return facture;
    }

    public void setFacture(Facture facture) {
        this.facture = facture;
    }

    public Prestation getPrestation() {
        return prestation;
    }

    public void setPrestation(Prestation prestation) {
        this.prestation = prestation;
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

    public Double getTauxTva() {
        return tauxTva;
    }

    public void setTauxTva(Double tauxTva) {
        this.tauxTva = tauxTva;
    }

    public Double getMontantHt() {
        return montantHt;
    }

    public void setMontantHt(Double montantHt) {
        this.montantHt = montantHt;
    }

    public Double getTauxTr() {
        return tauxTr;
    }

    public void setTauxTr(Double tauxTr) {
        this.tauxTr = tauxTr;
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

