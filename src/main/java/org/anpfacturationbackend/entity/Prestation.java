package org.anpfacturationbackend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "prestations", uniqueConstraints = @UniqueConstraint(columnNames = "code"))
public class Prestation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String libelle;

    @Column(nullable = false)
    private Double tauxTva;

    @Column(nullable = false)
    private Double tauxTr;

    @Column(nullable = false)
    private String compteComptable;

    public Prestation() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
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

    public String getCompteComptable() {
        return compteComptable;
    }

    public void setCompteComptable(String compteComptable) {
        this.compteComptable = compteComptable;
    }

    @Override
    public String toString() {
        return "Prestation{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", libelle='" + libelle + '\'' +
                ", tauxTva=" + tauxTva +
                ", tauxTr=" + tauxTr +
                ", compteComptable='" + compteComptable + '\'' +
                '}';
    }
}

