package org.anpfacturationbackend.repository;

import org.anpfacturationbackend.entity.Prestation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrestationRepository extends JpaRepository<Prestation, Long> {
    boolean existsByCode(String code);
    java.util.Optional<Prestation> findByCode(String code);
}

