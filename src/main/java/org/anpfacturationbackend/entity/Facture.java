package org.anpfacturationbackend.entity;

import jakarta.persistence.*;
import org.anpfacturationbackend.enums.StatutFacture;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "facture")
public class Facture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String numero;

    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutFacture statut;

    @Column(nullable = false)
    private Double montantHt;

    @Column(nullable = false)
    private Double montantTr = 0.0;

    @Column(nullable = false)
    private Double montantTva;

    @Column(nullable = false)
    private Double montantTtc;

    @Column
    private String transmissionStatut = "PENDING";

    @Column
    private java.time.LocalDateTime dateTransmission;

    @OneToMany(mappedBy = "facture", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LigneFacture> lignes = new ArrayList<>();

    public Facture() {
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public StatutFacture getStatut() {
        return statut;
    }

    public void setStatut(StatutFacture statut) {
        this.statut = statut;
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

    public List<LigneFacture> getLignes() {
        return lignes;
    }

    public void setLignes(List<LigneFacture> lignes) {
        this.lignes = lignes;
    }

    public void addLigne(LigneFacture ligne) {
        lignes.add(ligne);
        ligne.setFacture(this);
    }

    public void removeLigne(LigneFacture ligne) {
        lignes.remove(ligne);
        ligne.setFacture(null);
    }

    public String getTransmissionStatut() {
        return transmissionStatut;
    }

    public void setTransmissionStatut(String transmissionStatut) {
        this.transmissionStatut = transmissionStatut;
    }

    public java.time.LocalDateTime getDateTransmission() {
        return dateTransmission;
    }

    public void setDateTransmission(java.time.LocalDateTime dateTransmission) {
        this.dateTransmission = dateTransmission;
    }
}

