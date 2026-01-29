package org.example.anpfacturationbackend.repository;

import org.example.anpfacturationbackend.entity.TarifOTDP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TarifOTDPRepository extends JpaRepository<TarifOTDP, Long> {
    List<TarifOTDP> findByPrestationId(Long prestationId);

    List<TarifOTDP> findByPrestationIdAndActifTrue(Long prestationId);

    List<TarifOTDP> findByActifTrue();

    void deleteByPrestationId(Long prestationId);
}
