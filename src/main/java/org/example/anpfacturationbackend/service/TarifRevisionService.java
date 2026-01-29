package org.example.anpfacturationbackend.service;

import org.example.anpfacturationbackend.entity.TarifEauElectricite;
import org.example.anpfacturationbackend.entity.TarifOTDP;
import org.example.anpfacturationbackend.entity.TarifAutorisation;
import org.example.anpfacturationbackend.entity.TarifConcession;
import org.example.anpfacturationbackend.repository.TarifEauElectriciteRepository;
import org.example.anpfacturationbackend.repository.TarifOTDPRepository;
import org.example.anpfacturationbackend.repository.TarifAutorisationRepository;
import org.example.anpfacturationbackend.repository.TarifConcessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TarifRevisionService {

    private final TarifOTDPRepository tarifOTDPRepository;
    private final TarifEauElectriciteRepository tarifEauElectriciteRepository;
    private final TarifAutorisationRepository tarifAutorisationRepository;
    private final TarifConcessionRepository tarifConcessionRepository;
    private final AuditService auditService;

    public TarifRevisionService(TarifOTDPRepository tarifOTDPRepository,
            TarifEauElectriciteRepository tarifEauElectriciteRepository,
            TarifAutorisationRepository tarifAutorisationRepository,
            TarifConcessionRepository tarifConcessionRepository,
            AuditService auditService) {
        this.tarifOTDPRepository = tarifOTDPRepository;
        this.tarifEauElectriciteRepository = tarifEauElectriciteRepository;
        this.tarifAutorisationRepository = tarifAutorisationRepository;
        this.tarifConcessionRepository = tarifConcessionRepository;
        this.auditService = auditService;
    }

    /**
     * Exécute la révision annuelle des tarifs.
     * Cette méthode doit être appelée par un scheduler (cron) ou manuellement par
     * un admin.
     * 
     * @param targetYear L'année pour laquelle générer les nouveaux tarifs
     *                   (généralement l'année courante).
     * @return Le nombre de tarifs révisés.
     */
    public int performAnnualRevision(int targetYear) {
        int count = 0;
        count += reviseTarifsOTDP(targetYear);
        count += reviseTarifsEauElectricite(targetYear);
        count += reviseTarifsAutorisation(targetYear);
        count += reviseTarifsConcession(targetYear);
        return count;
    }

    public int reviseTarifsOTDP(int targetYear) {
        List<TarifOTDP> activeTarifs = tarifOTDPRepository.findByActifTrue();
        int count = 0;

        for (TarifOTDP oldTarif : activeTarifs) {
            if (shouldRevise(oldTarif.getAnneeTarif(), oldTarif.getAnneeDebutRevision(), oldTarif.getDelaiRevision(),
                    targetYear)) {
                TarifOTDP newTarif = new TarifOTDP();
                newTarif.setPrestation(oldTarif.getPrestation());
                newTarif.setTypeTerrain(oldTarif.getTypeTerrain());
                newTarif.setNatureActivite(oldTarif.getNatureActivite());
                newTarif.setCategorie(oldTarif.getCategorie());
                newTarif.setUniteBase(oldTarif.getUniteBase());
                newTarif.setAnneeTarif(targetYear);

                double taux = oldTarif.getTauxRevision() != null ? oldTarif.getTauxRevision() : 0.0;
                double nouveauMontant = oldTarif.getMontant() * (1 + (taux / 100.0));
                newTarif.setMontant(nouveauMontant);

                newTarif.setAnneeDebutRevision(oldTarif.getAnneeDebutRevision());
                newTarif.setTauxRevision(oldTarif.getTauxRevision());
                newTarif.setDelaiRevision(oldTarif.getDelaiRevision());
                newTarif.setActif(true);

                oldTarif.setActif(false);
                tarifOTDPRepository.save(oldTarif);
                tarifOTDPRepository.save(newTarif);

                auditService.log("REVISION_TARIF",
                        "Révision OTDP: " + oldTarif.getId() + " -> " + newTarif.getId() + " (" + nouveauMontant + ")");
                count++;
            }
        }
        return count;
    }

    public int reviseTarifsEauElectricite(int targetYear) {
        List<TarifEauElectricite> activeTarifs = tarifEauElectriciteRepository.findByActifTrue();
        int count = 0;

        for (TarifEauElectricite oldTarif : activeTarifs) {
            if (shouldRevise(oldTarif.getAnneeTarif(), oldTarif.getAnneeDebutRevision(), oldTarif.getDelaiRevision(),
                    targetYear)) {
                TarifEauElectricite newTarif = new TarifEauElectricite();
                newTarif.setPrestation(oldTarif.getPrestation());
                newTarif.setCodePort(oldTarif.getCodePort());
                newTarif.setLibelle(oldTarif.getLibelle());
                newTarif.setCodeActivite(oldTarif.getCodeActivite());
                newTarif.setTarifDistributeur(oldTarif.getTarifDistributeur());
                newTarif.setAnneeTarif(targetYear);

                double taux = oldTarif.getTauxRevision() != null ? oldTarif.getTauxRevision() : 0.0;
                double nouveauTarifFacture = oldTarif.getTarifFacture() * (1 + (taux / 100.0));
                newTarif.setTarifFacture(nouveauTarifFacture);

                newTarif.setAnneeDebutRevision(oldTarif.getAnneeDebutRevision());
                newTarif.setTauxRevision(oldTarif.getTauxRevision());
                newTarif.setDelaiRevision(oldTarif.getDelaiRevision());
                newTarif.setActif(true);

                oldTarif.setActif(false);
                tarifEauElectriciteRepository.save(oldTarif);
                tarifEauElectriciteRepository.save(newTarif);

                auditService.log("REVISION_TARIF", "Révision Eau/Elec: " + oldTarif.getId() + " -> " + newTarif.getId()
                        + " (" + nouveauTarifFacture + ")");
                count++;
            }
        }
        return count;
    }

    public int reviseTarifsAutorisation(int targetYear) {
        List<TarifAutorisation> activeTarifs = tarifAutorisationRepository.findByActifTrue();
        int count = 0;

        for (TarifAutorisation oldTarif : activeTarifs) {
            if (shouldRevise(oldTarif.getAnneeTarif(), oldTarif.getAnneeDebutRevision(), oldTarif.getDelaiRevision(),
                    targetYear)) {
                TarifAutorisation newTarif = new TarifAutorisation();
                newTarif.setPrestation(oldTarif.getPrestation());
                newTarif.setLibelle(oldTarif.getLibelle());
                newTarif.setAnneeTarif(targetYear);

                double taux = oldTarif.getTauxRevision() != null ? oldTarif.getTauxRevision() : 0.0;
                double nouveauMontant = oldTarif.getMontant() * (1 + (taux / 100.0));
                newTarif.setMontant(nouveauMontant);

                newTarif.setAnneeDebutRevision(oldTarif.getAnneeDebutRevision());
                newTarif.setTauxRevision(oldTarif.getTauxRevision());
                newTarif.setDelaiRevision(oldTarif.getDelaiRevision());
                newTarif.setActif(true);

                oldTarif.setActif(false);
                tarifAutorisationRepository.save(oldTarif);
                tarifAutorisationRepository.save(newTarif);

                auditService.log("REVISION_TARIF", "Révision Autorisation: " + oldTarif.getId() + " -> "
                        + newTarif.getId() + " (" + nouveauMontant + ")");
                count++;
            }
        }
        return count;
    }

    public int reviseTarifsConcession(int targetYear) {
        List<TarifConcession> activeTarifs = tarifConcessionRepository.findByActifTrue();
        int count = 0;

        for (TarifConcession oldTarif : activeTarifs) {
            if (shouldRevise(oldTarif.getAnneeTarif(), oldTarif.getAnneeDebutRevision(), oldTarif.getDelaiRevision(),
                    targetYear)) {
                TarifConcession newTarif = new TarifConcession();
                newTarif.setPrestation(oldTarif.getPrestation());
                newTarif.setTypeContrat(oldTarif.getTypeContrat());
                newTarif.setAnneeTarif(targetYear);

                double taux = oldTarif.getTauxRevision() != null ? oldTarif.getTauxRevision() : 0.0;
                double nouveauMontant = oldTarif.getMontant() * (1 + (taux / 100.0));
                newTarif.setMontant(nouveauMontant);

                newTarif.setAnneeDebutRevision(oldTarif.getAnneeDebutRevision());
                newTarif.setTauxRevision(oldTarif.getTauxRevision());
                newTarif.setDelaiRevision(oldTarif.getDelaiRevision());
                newTarif.setActif(true);

                oldTarif.setActif(false);
                tarifConcessionRepository.save(oldTarif);
                tarifConcessionRepository.save(newTarif);

                auditService.log("REVISION_TARIF", "Révision Concession: " + oldTarif.getId() + " -> "
                        + newTarif.getId() + " (" + nouveauMontant + ")");
                count++;
            }
        }
        return count;
    }

    private boolean shouldRevise(Integer anneeTarif, Integer anneeDebut, Integer delai, int targetYear) {
        if (anneeTarif == null || targetYear <= anneeTarif)
            return false;
        if (delai == null || delai <= 0)
            return false;
        return (targetYear - anneeTarif) >= delai;
    }
}
