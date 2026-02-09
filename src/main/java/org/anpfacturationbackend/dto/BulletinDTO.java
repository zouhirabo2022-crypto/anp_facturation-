package org.anpfacturationbackend.dto;

import java.util.List;

public class BulletinDTO {
    private Long id;
    private String idBulletinMetier;
    private Long clientId;
    private String clientNom;
    private String periodeFacturation;
    private String statut;
    private String dateReception;
    private List<LigneBulletinDTO> lignes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdBulletinMetier() {
        return idBulletinMetier;
    }

    public void setIdBulletinMetier(String idBulletinMetier) {
        this.idBulletinMetier = idBulletinMetier;
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

    public String getPeriodeFacturation() {
        return periodeFacturation;
    }

    public void setPeriodeFacturation(String periodeFacturation) {
        this.periodeFacturation = periodeFacturation;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getDateReception() {
        return dateReception;
    }

    public void setDateReception(String dateReception) {
        this.dateReception = dateReception;
    }

    public List<LigneBulletinDTO> getLignes() {
        return lignes;
    }

    public void setLignes(List<LigneBulletinDTO> lignes) {
        this.lignes = lignes;
    }

    public static class LigneBulletinDTO {
        private Long prestationId;
        private Double quantite;
        // Search criteria
        private String typeTerrain;
        private String natureActivite;
        private String categorie;
        private String codePort;
        private String codeActivite;

        public Long getPrestationId() {
            return prestationId;
        }

        public void setPrestationId(Long prestationId) {
            this.prestationId = prestationId;
        }

        public Double getQuantite() {
            return quantite;
        }

        public void setQuantite(Double quantite) {
            this.quantite = quantite;
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
    }
}

