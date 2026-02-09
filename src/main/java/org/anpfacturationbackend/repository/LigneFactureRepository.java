package org.anpfacturationbackend.repository;

import org.anpfacturationbackend.entity.LigneFacture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LigneFactureRepository extends JpaRepository<LigneFacture, Long> {
    boolean existsByPrestationId(Long prestationId);
}

