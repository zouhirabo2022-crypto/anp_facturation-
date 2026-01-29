package org.example.anpfacturationbackend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tarifs_autorisation")
public class TarifAutorisation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prestation_id", nullable = false)
    private Prestation prestation;

    @Column(nullable = false)
    private String libelle;

    @Column(nullable = false)
    private Double montant;

    @Column(nullable = false)
    private Integer anneeTarif;

    // Paramètres de révision (exprimée en années selon spécifs)
    private Integer anneeDebutRevision;
    private Double tauxRevision;
    private Integer delaiRevision; // en années

    @Column(nullable = false)
    private Boolean actif = true;

    public TarifAutorisation() {
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

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
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
        return "TarifAutorisation{" +
                "id=" + id +
                ", libelle='" + libelle + '\'' +
                ", montant=" + montant +
                ", anneeTarif=" + anneeTarif +
                ", actif=" + actif +
                '}';
    }
}
