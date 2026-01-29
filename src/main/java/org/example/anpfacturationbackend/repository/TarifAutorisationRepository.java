package org.example.anpfacturationbackend.repository;

import org.example.anpfacturationbackend.entity.TarifAutorisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TarifAutorisationRepository extends JpaRepository<TarifAutorisation, Long> {
    List<TarifAutorisation> findByPrestationIdAndActifTrue(Long prestationId);

    List<TarifAutorisation> findByActifTrue();

    void deleteByPrestationId(Long prestationId);
}
