package org.anpfacturationbackend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "lignes_bulletin")
public class LigneBulletin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bulletin_id", nullable = false)
    private Bulletin bulletin;

    @Column(name = "prestation_id")
    private Long prestationId; // On stocke l'ID, la rÃ©solution se fera au moment de la facturation

    @Column(nullable = false)
    private Double quantite;

    // CritÃ¨res de recherche pour la tarification
    private String typeTerrain;
    private String natureActivite;
    private String categorie;
    private String codePort;
    private String codeActivite;

    public LigneBulletin() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Bulletin getBulletin() {
        return bulletin;
    }

    public void setBulletin(Bulletin bulletin) {
        this.bulletin = bulletin;
    }

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

    @Override
    public String toString() {
        return "LigneBulletin{" +
                "id=" + id +
                ", prestationId=" + prestationId +
                ", quantite=" + quantite +
                ", typeTerrain='" + typeTerrain + '\'' +
                ", natureActivite='" + natureActivite + '\'' +
                ", categorie='" + categorie + '\'' +
                ", codePort='" + codePort + '\'' +
                ", codeActivite='" + codeActivite + '\'' +
                '}';
    }
}

