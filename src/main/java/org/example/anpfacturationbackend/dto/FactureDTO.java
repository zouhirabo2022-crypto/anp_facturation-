package org.example.anpfacturationbackend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.example.anpfacturationbackend.enums.StatutFacture;

import java.time.LocalDate;
import java.util.List;

public class FactureDTO {
    private Long id;
    private String numero;
    private LocalDate date;

    @NotNull(message = "L'ID du client est obligatoire")
    private Long clientId;

    private String clientNom; // Read-only

    private StatutFacture statut;

    private Double montantHt;
    private Double montantTr;
    private Double montantTva;
    private Double montantTtc;

    @NotEmpty(message = "La facture doit contenir au moins une ligne")
    @Valid
    private List<LigneFactureDTO> lignes;

    public FactureDTO() {
    }

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

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getClientNom() {
        return clientNom;
    }

    public void setClientNom(String clientNom) {
        this.clientNom = clientNom;
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

    public List<LigneFactureDTO> getLignes() {
        return lignes;
    }

    public void setLignes(List<LigneFactureDTO> lignes) {
        this.lignes = lignes;
    }
}
