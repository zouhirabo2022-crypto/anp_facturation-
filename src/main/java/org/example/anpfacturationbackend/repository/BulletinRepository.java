package org.example.anpfacturationbackend.repository;

import org.example.anpfacturationbackend.entity.Bulletin;
import org.example.anpfacturationbackend.enums.StatutBulletin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BulletinRepository extends JpaRepository<Bulletin, Long> {
    List<Bulletin> findByStatut(StatutBulletin statut);
    Optional<Bulletin> findByIdBulletinMetier(String idBulletinMetier);
}
