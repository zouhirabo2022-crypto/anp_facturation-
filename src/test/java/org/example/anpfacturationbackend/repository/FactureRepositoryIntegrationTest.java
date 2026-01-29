package org.example.anpfacturationbackend.repository;

import org.example.anpfacturationbackend.entity.Client;
import org.example.anpfacturationbackend.entity.Facture;
import org.example.anpfacturationbackend.enums.StatutFacture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect"
})
class FactureRepositoryIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private FactureRepository factureRepository;

    @Autowired
    private ClientRepository clientRepository;

    @BeforeEach
    void setUp() {
        factureRepository.deleteAll();
        clientRepository.deleteAll();

        Client client = new Client();
        client.setNom("Client Test");
        client.setAdresse("123 Test St");
        client.setTelephone("0612345678");
        client.setIce("123456789");
        client = clientRepository.save(client);

        Facture f1 = new Facture();
        f1.setNumero("F2024001");
        f1.setMontantHt(100.0);
        f1.setMontantTva(20.0);
        f1.setMontantTtc(120.0);
        f1.setStatut(StatutFacture.PAYEE);
        f1.setDate(LocalDate.now());
        f1.setClient(client);
        factureRepository.save(f1);

        Facture f2 = new Facture();
        f2.setNumero("F2024002");
        f2.setMontantHt(200.0);
        f2.setMontantTva(40.0);
        f2.setMontantTtc(240.0);
        f2.setStatut(StatutFacture.BROUILLON);
        f2.setDate(LocalDate.now());
        f2.setClient(client);
        factureRepository.save(f2);
    }

    @Test
    void testSumTotalHt() {
        Double totalHt = factureRepository.sumTotalHt();
        assertThat(totalHt).isEqualTo(300.0);
    }

    @Test
    void testSumTotalTtc() {
        Double totalTtc = factureRepository.sumTotalTtc();
        assertThat(totalTtc).isEqualTo(360.0);
    }

    @Test
    void testCountByStatut() {
        Long countPayee = factureRepository.countByStatut(StatutFacture.PAYEE);
        assertThat(countPayee).isEqualTo(1L);

        Long countBrouillon = factureRepository.countByStatut(StatutFacture.BROUILLON);
        assertThat(countBrouillon).isEqualTo(1L);
    }

    @Test
    void testSumMontantTtcByStatut() {
        Double sumPayee = factureRepository.sumMontantTtcByStatut(StatutFacture.PAYEE);
        assertThat(sumPayee).isEqualTo(120.0);

        Double sumBrouillon = factureRepository.sumMontantTtcByStatut(StatutFacture.BROUILLON);
        assertThat(sumBrouillon).isEqualTo(240.0);
    }

    @Test
    void testFindMaxNumeroByPattern() {
        String maxNumero = factureRepository.findMaxNumeroByPattern("F2024%");
        assertThat(maxNumero).isEqualTo("F2024002");
    }
}
