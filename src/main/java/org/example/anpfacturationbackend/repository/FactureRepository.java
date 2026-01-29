package org.example.anpfacturationbackend.repository;

import org.example.anpfacturationbackend.entity.Facture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FactureRepository extends JpaRepository<Facture, Long> {
    List<Facture> findByClientId(Long clientId);
    boolean existsByNumero(String numero);

    @org.springframework.data.jpa.repository.Query("SELECT SUM(f.montantHt) FROM Facture f")
    Double sumTotalHt();

    @org.springframework.data.jpa.repository.Query("SELECT SUM(f.montantTr) FROM Facture f")
    Double sumTotalTr();

    @org.springframework.data.jpa.repository.Query("SELECT SUM(f.montantTva) FROM Facture f")
    Double sumTotalTva();

    @org.springframework.data.jpa.repository.Query("SELECT SUM(f.montantTtc) FROM Facture f")
    Double sumTotalTtc();

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(f) FROM Facture f WHERE f.statut = :statut")
    Long countByStatut(@org.springframework.data.repository.query.Param("statut") org.example.anpfacturationbackend.enums.StatutFacture statut);

    @org.springframework.data.jpa.repository.Query("SELECT SUM(f.montantTtc) FROM Facture f WHERE f.statut = :statut")
    Double sumMontantTtcByStatut(@org.springframework.data.repository.query.Param("statut") org.example.anpfacturationbackend.enums.StatutFacture statut);

    @org.springframework.data.jpa.repository.Query("SELECT MAX(f.numero) FROM Facture f WHERE f.numero LIKE :pattern")
    String findMaxNumeroByPattern(@org.springframework.data.repository.query.Param("pattern") String pattern);
}
