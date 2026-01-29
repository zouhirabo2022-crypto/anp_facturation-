package org.example.anpfacturationbackend.repository;

import org.example.anpfacturationbackend.entity.TarifEauElectricite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TarifEauElectriciteRepository extends JpaRepository<TarifEauElectricite, Long> {
    List<TarifEauElectricite> findByPrestationId(Long prestationId);

    List<TarifEauElectricite> findByPrestationIdAndActifTrue(Long prestationId);

    List<TarifEauElectricite> findByActifTrue();

    void deleteByPrestationId(Long prestationId);
}
