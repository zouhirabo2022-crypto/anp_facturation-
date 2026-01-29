package org.example.anpfacturationbackend.config;

import org.example.anpfacturationbackend.entity.Role;
import org.example.anpfacturationbackend.entity.User;
import org.example.anpfacturationbackend.repository.RoleRepository;
import org.example.anpfacturationbackend.repository.UserRepository;
import org.example.anpfacturationbackend.repository.FactureRepository;
import org.example.anpfacturationbackend.repository.BulletinRepository;
import org.example.anpfacturationbackend.repository.TarifConcessionRepository;
import org.example.anpfacturationbackend.repository.TarifAutorisationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
@Profile("!test")
public class DataInitializer {

    @Bean
    CommandLineRunner init(UserRepository userRepository, RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            org.example.anpfacturationbackend.repository.ClientRepository clientRepository,
            org.example.anpfacturationbackend.repository.PrestationRepository prestationRepository,
            org.example.anpfacturationbackend.repository.TarifOTDPRepository tarifOTDPRepository,
            org.example.anpfacturationbackend.repository.TarifEauElectriciteRepository tarifEauElectriciteRepository,
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
                System.out.println("Error creating gestionnaire: " + e.getMessage());
            }

            // Initialisation d'un consultant
            try {
                Role consultationRole = roleRepository.findByName("CONSULTATION").orElseThrow();
                createUserIfNotFound(userRepository, "consultant", "cons123", consultationRole, passwordEncoder);
            } catch (Exception e) {
                System.out.println("Error creating consultant: " + e.getMessage());
            }

            // === DONNÉES DE DÉMONSTRATION ===
            /*
             * Data seeding disabled per user request.
             * No Clients, Prestations, Tarifs, Bulletins, or Factures will be created.
             */
        };
    }

    private Role createRoleIfNotFound(RoleRepository roleRepository, String name) {
        try {
            return roleRepository.findByName(name)
                    .orElseGet(() -> roleRepository.save(Role.builder().name(name).build()));
        } catch (Exception e) {
            System.out.println("Role " + name + " already exists or error: " + e.getMessage());
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
                System.out.println("User " + username + " created.");
            }
        } catch (Exception e) {
            System.out.println("User " + username + " already exists or error: " + e.getMessage());
        }
    }
}
