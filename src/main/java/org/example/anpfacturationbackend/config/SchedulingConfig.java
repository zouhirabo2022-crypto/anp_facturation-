package org.example.anpfacturationbackend.config;

import org.example.anpfacturationbackend.service.TarifRevisionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Year;

@Configuration
@EnableScheduling
public class SchedulingConfig {

    private static final Logger logger = LoggerFactory.getLogger(SchedulingConfig.class);
    private final TarifRevisionService tarifRevisionService;

    public SchedulingConfig(TarifRevisionService tarifRevisionService) {
        this.tarifRevisionService = tarifRevisionService;
    }

    /**
     * Run tariff revision annually.
     * Cron: At 01:00 AM, on day 1 of the month, only in January.
     * "0 0 1 1 1 *"
     */
    @Scheduled(cron = "0 0 1 1 1 *")
    public void scheduleAnnualRevision() {
        int currentYear = Year.now().getValue();
        logger.info("Starting scheduled annual tariff revision for year {}", currentYear);
        try {
            int count = tarifRevisionService.performAnnualRevision(currentYear);
            logger.info("Annual tariff revision completed. {} tariffs revised.", count);
        } catch (Exception e) {
            logger.error("Error during annual tariff revision", e);
        }
    }
}
