package org.example.anpfacturationbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PrestationDTO {

    private Long id;

    @NotBlank(message = "Le code est obligatoire")
    private String code;

    @NotBlank(message = "Le libellé est obligatoire")
    private String libelle;

    @NotNull(message = "Le taux TVA est obligatoire")
    private Double tauxTva;

    @NotNull(message = "Le taux TR est obligatoire")
    private Double tauxTr;

    @NotBlank(message = "Le compte comptable est obligatoire")
    private String compteComptable;

    public PrestationDTO() {
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
}