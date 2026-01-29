package org.example.anpfacturationbackend.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class SchemaFixer {

    private static final Logger logger = LoggerFactory.getLogger(SchemaFixer.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void fixSchema() {
        try {
            // Fix tarifs_otdp
            jdbcTemplate.execute("ALTER TABLE tarifs_otdp ADD COLUMN IF NOT EXISTS actif BOOLEAN DEFAULT TRUE");
            jdbcTemplate.execute("ALTER TABLE tarifs_otdp ADD COLUMN IF NOT EXISTS annee_tarif INTEGER DEFAULT 2024");
            jdbcTemplate.execute("ALTER TABLE tarifs_otdp ADD COLUMN IF NOT EXISTS annee_debut_revision INTEGER");
            jdbcTemplate.execute("ALTER TABLE tarifs_otdp ADD COLUMN IF NOT EXISTS taux_revision DOUBLE PRECISION");
            jdbcTemplate.execute("ALTER TABLE tarifs_otdp ADD COLUMN IF NOT EXISTS delai_revision INTEGER");
            jdbcTemplate.execute("ALTER TABLE tarifs_otdp ADD COLUMN IF NOT EXISTS categorie VARCHAR(255)");
            jdbcTemplate.execute("ALTER TABLE tarifs_otdp ADD COLUMN IF NOT EXISTS type_terrain VARCHAR(255)");
            jdbcTemplate.execute("ALTER TABLE tarifs_otdp ADD COLUMN IF NOT EXISTS nature_activite VARCHAR(255)");
            jdbcTemplate.execute("ALTER TABLE tarifs_otdp ADD COLUMN IF NOT EXISTS unite_base VARCHAR(255)");

            // Fix tarifs_eau_electricite
            jdbcTemplate.execute("ALTER TABLE tarifs_eau_electricite ADD COLUMN IF NOT EXISTS actif BOOLEAN DEFAULT TRUE");
            jdbcTemplate.execute("ALTER TABLE tarifs_eau_electricite ADD COLUMN IF NOT EXISTS annee_tarif INTEGER DEFAULT 2024");
            jdbcTemplate.execute("ALTER TABLE tarifs_eau_electricite ADD COLUMN IF NOT EXISTS annee_debut_revision INTEGER");
            jdbcTemplate.execute("ALTER TABLE tarifs_eau_electricite ADD COLUMN IF NOT EXISTS taux_revision DOUBLE PRECISION");
            jdbcTemplate.execute("ALTER TABLE tarifs_eau_electricite ADD COLUMN IF NOT EXISTS delai_revision INTEGER");
            jdbcTemplate.execute("ALTER TABLE tarifs_eau_electricite ADD COLUMN IF NOT EXISTS code_port VARCHAR(255)");
            jdbcTemplate.execute("ALTER TABLE tarifs_eau_electricite ADD COLUMN IF NOT EXISTS libelle VARCHAR(255)");
            jdbcTemplate.execute("ALTER TABLE tarifs_eau_electricite ADD COLUMN IF NOT EXISTS code_activite VARCHAR(255)");
            jdbcTemplate.execute("ALTER TABLE tarifs_eau_electricite ADD COLUMN IF NOT EXISTS tarif_distributeur DOUBLE PRECISION");
            jdbcTemplate.execute("ALTER TABLE tarifs_eau_electricite ADD COLUMN IF NOT EXISTS tarif_facture DOUBLE PRECISION");

            // Fix tarifs_autorisation
            jdbcTemplate.execute("ALTER TABLE tarifs_autorisation ADD COLUMN IF NOT EXISTS actif BOOLEAN DEFAULT TRUE");
            jdbcTemplate.execute("ALTER TABLE tarifs_autorisation ADD COLUMN IF NOT EXISTS annee_tarif INTEGER DEFAULT 2024");
            jdbcTemplate.execute("ALTER TABLE tarifs_autorisation ADD COLUMN IF NOT EXISTS annee_debut_revision INTEGER");
            jdbcTemplate.execute("ALTER TABLE tarifs_autorisation ADD COLUMN IF NOT EXISTS taux_revision DOUBLE PRECISION");
            jdbcTemplate.execute("ALTER TABLE tarifs_autorisation ADD COLUMN IF NOT EXISTS delai_revision INTEGER");

            // Fix tarifs_concession
            jdbcTemplate.execute("ALTER TABLE tarifs_concession ADD COLUMN IF NOT EXISTS actif BOOLEAN DEFAULT TRUE");
            jdbcTemplate.execute("ALTER TABLE tarifs_concession ADD COLUMN IF NOT EXISTS annee_tarif INTEGER DEFAULT 2024");
            jdbcTemplate.execute("ALTER TABLE tarifs_concession ADD COLUMN IF NOT EXISTS annee_debut_revision INTEGER");
            jdbcTemplate.execute("ALTER TABLE tarifs_concession ADD COLUMN IF NOT EXISTS taux_revision DOUBLE PRECISION");
            jdbcTemplate.execute("ALTER TABLE tarifs_concession ADD COLUMN IF NOT EXISTS delai_revision INTEGER");
            jdbcTemplate.execute("ALTER TABLE tarifs_concession ADD COLUMN IF NOT EXISTS type_contrat VARCHAR(255)");
            
            logger.info("SchemaFixer: Verified columns (actif, annee_tarif, revisions, business fields) in tariff tables.");
        } catch (Exception e) {
            logger.error("SchemaFixer: Error updating schema: {}", e.getMessage(), e);
        }
    }
}
