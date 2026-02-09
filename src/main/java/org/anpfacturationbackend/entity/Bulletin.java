package org.anpfacturationbackend.entity;

import jakarta.persistence.*;
import org.anpfacturationbackend.enums.StatutBulletin;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bulletins")
public class Bulletin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_bulletin_metier", unique = true)
    private String idBulletinMetier; // L'ID venant du systÃ¨me mÃ©tier

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(name = "periode_facturation")
    private String periodeFacturation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutBulletin statut = StatutBulletin.EN_ATTENTE;

    @Column(name = "date_reception", nullable = false)
    private LocalDateTime dateReception = LocalDateTime.now();

    @OneToMany(mappedBy = "bulletin", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LigneBulletin> lignes = new ArrayList<>();

    public Bulletin() {
    }

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

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getPeriodeFacturation() {
        return periodeFacturation;
    }

    public void setPeriodeFacturation(String periodeFacturation) {
        this.periodeFacturation = periodeFacturation;
    }

    public StatutBulletin getStatut() {
        return statut;
    }

    public void setStatut(StatutBulletin statut) {
        this.statut = statut;
    }

    public LocalDateTime getDateReception() {
        return dateReception;
    }

    public void setDateReception(LocalDateTime dateReception) {
        this.dateReception = dateReception;
    }

    public List<LigneBulletin> getLignes() {
        return lignes;
    }

    public void setLignes(List<LigneBulletin> lignes) {
        this.lignes = lignes;
    }

    public void addLigne(LigneBulletin ligne) {
        lignes.add(ligne);
        ligne.setBulletin(this);
    }

    public void removeLigne(LigneBulletin ligne) {
        lignes.remove(ligne);
        ligne.setBulletin(null);
    }

    @Override
    public String toString() {
        return "Bulletin{" +
                "id=" + id +
                ", idBulletinMetier='" + idBulletinMetier + '\'' +
                ", periodeFacturation='" + periodeFacturation + '\'' +
                ", statut=" + statut +
                ", dateReception=" + dateReception +
                '}';
    }
}

