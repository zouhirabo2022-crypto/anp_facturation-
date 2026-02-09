package org.anpfacturationbackend.scheduler;

import org.anpfacturationbackend.service.TarifRevisionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class TariffRevisionScheduler {

    private static final Logger logger = LoggerFactory.getLogger(TariffRevisionScheduler.class);

    private final TarifRevisionService revisionService;

    public TariffRevisionScheduler(TarifRevisionService revisionService) {
        this.revisionService = revisionService;
    }

    /**
     * ExÃ©cute la rÃ©vision tarifaire annuelle automatiquement.
     * PlanifiÃ© pour s'exÃ©cuter Ã  00:00:00 le 1er Janvier de chaque annÃ©e.
     */
    @Scheduled(cron = "0 0 0 1 1 ?")
    public void scheduleAnnualRevision() {
        int year = LocalDate.now().getYear();
        logger.info("Starting scheduled annual tariff revision for year: {}", year);
        
        try {
            int count = revisionService.performAnnualRevision(year);
            logger.info("Annual tariff revision completed successfully. {} tariffs revised.", count);
        } catch (Exception e) {
            logger.error("Error occurred during annual tariff revision", e);
        }
    }
}

