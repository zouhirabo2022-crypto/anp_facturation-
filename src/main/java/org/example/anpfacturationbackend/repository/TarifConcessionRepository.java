package org.example.anpfacturationbackend.repository;

import org.example.anpfacturationbackend.entity.TarifConcession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TarifConcessionRepository extends JpaRepository<TarifConcession, Long> {
    List<TarifConcession> findByPrestationIdAndActifTrue(Long prestationId);

    List<TarifConcession> findByActifTrue();

    void deleteByPrestationId(Long prestationId);
}
