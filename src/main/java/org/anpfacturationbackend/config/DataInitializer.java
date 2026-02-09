package org.anpfacturationbackend.config;

import org.anpfacturationbackend.entity.Role;
import org.anpfacturationbackend.entity.User;
import org.anpfacturationbackend.repository.RoleRepository;
import org.anpfacturationbackend.repository.UserRepository;
import org.anpfacturationbackend.repository.FactureRepository;
import org.anpfacturationbackend.repository.BulletinRepository;
import org.anpfacturationbackend.repository.TarifConcessionRepository;
import org.anpfacturationbackend.repository.TarifAutorisationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
@Profile("!test")
public class DataInitializer {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    CommandLineRunner init(UserRepository userRepository, RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            org.anpfacturationbackend.repository.ClientRepository clientRepository,
            org.anpfacturationbackend.repository.PrestationRepository prestationRepository,
            org.anpfacturationbackend.repository.TarifOTDPRepository tarifOTDPRepository,
            org.anpfacturationbackend.repository.TarifEauElectriciteRepository tarifEauElectriciteRepository,
            TarifConcessionRepository tarifConcessionRepository,
            TarifAutorisationRepository tarifAutorisationRepository,
            FactureRepository factureRepository,
            BulletinRepository bulletinRepository) {
        return args -> {
            // Initialisation des rôles
            Role adminRole = createRoleIfNotFound(roleRepository, "ADMIN_SYSTEME");
            createRoleIfNotFound(roleRepository, "GESTIONNAIRE_PARAM");
            createRoleIfNotFound(roleRepository, "GESTIONNAIRE_TARIF");
            createRoleIfNotFound(roleRepository, "CONSULTATION");

            // Initialisation de l'utilisateur admin par défaut
            createUserIfNotFound(userRepository, "admin", "admin123", adminRole, passwordEncoder);

            // Initialisation d'un gestionnaire
            try {
                Role gestionnaireRole = roleRepository.findByName("GESTIONNAIRE_TARIF").orElseThrow();
                createUserIfNotFound(userRepository, "gestionnaire", "gest123", gestionnaireRole, passwordEncoder);
            } catch (Exception e) {
                logger.error("Error creating gestionnaire: {}", e.getMessage());
            }

            // Initialisation d'un consultant
            try {
                Role consultationRole = roleRepository.findByName("CONSULTATION").orElseThrow();
                createUserIfNotFound(userRepository, "consultant", "cons123", consultationRole, passwordEncoder);
            } catch (Exception e) {
                logger.error("Error creating consultant: {}", e.getMessage());
            }

            // === DONNÉES DE DÉMONSTRATION ===
            if (clientRepository.count() == 0) {
                org.anpfacturationbackend.entity.Client client = new org.anpfacturationbackend.entity.Client();
                client.setNom("Marsa Maroc");
                client.setPrenom("");
                client.setAdresse("Port de Casablanca");
                client.setEmail("contact@marsamaroc.co.ma");
                client.setTelephone("0522000000");
                client.setIce("123456789");
                client.setIfClient("987654321");
                client.setRc("12345");
                clientRepository.save(client);
                logger.info("Client 'Marsa Maroc' created.");
            }

            if (prestationRepository.count() == 0) {
                org.anpfacturationbackend.entity.Prestation p1 = new org.anpfacturationbackend.entity.Prestation();
                p1.setCode("P001");
                p1.setLibelle("Occupation Temporaire du Domaine Public");
                p1.setTauxTva(20.0);
                p1.setTauxTr(0.0);
                p1.setCompteComptable("712100");
                prestationRepository.save(p1);
                logger.info("Prestation 'OTDP' created.");

                if (tarifOTDPRepository.count() == 0) {
                    org.anpfacturationbackend.entity.TarifOTDP t1 = new org.anpfacturationbackend.entity.TarifOTDP();
                    t1.setPrestation(p1);
                    t1.setTypeTerrain("NU");
                    t1.setNatureActivite("COMMERCIALE");
                    t1.setCategorie("STANDARD");
                    t1.setMontant(50.0);
                    t1.setAnneeTarif(2024);
                    t1.setActif(true);
                    tarifOTDPRepository.save(t1);
                    logger.info("Tarif OTDP created.");
                }
            }
        };
    }

    private Role createRoleIfNotFound(RoleRepository roleRepository, String name) {
        try {
            return roleRepository.findByName(name)
                    .orElseGet(() -> roleRepository.save(Role.builder().name(name).build()));
        } catch (Exception e) {
            logger.warn("Role {} already exists or error: {}", name, e.getMessage());
            return roleRepository.findByName(name).orElse(null);
        }
    }

    private void createUserIfNotFound(UserRepository userRepository, String username, String password, Role role,
            PasswordEncoder passwordEncoder) {
        try {
            if (userRepository.findByUsername(username).isEmpty()) {
                User user = User.builder()
                        .username(username)
                        .password(passwordEncoder.encode(password))
                        .enabled(true)
                        .roles(Set.of(role))
                        .build();
                userRepository.save(user);
                logger.info("User {} created.", username);
            }
        } catch (Exception e) {
            logger.warn("User {} already exists or error: {}", username, e.getMessage());
        }
    }
}
