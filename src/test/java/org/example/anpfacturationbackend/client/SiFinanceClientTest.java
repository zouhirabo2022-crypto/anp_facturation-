package org.example.anpfacturationbackend.client;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(SiFinanceClient.class)
class SiFinanceClientTest {

    @Autowired
    private SiFinanceClient client;

    @Autowired
    private MockRestServiceServer server;

    @Value("${si.finance.url:http://localhost:8080/api/stub/si-finance}")
    private String siFinanceUrl;

    @Test
    void getFiscalRates_ShouldCallExternalService() {
        // Given
        String prestationCode = "P001";
        String expectedUrl = siFinanceUrl + "/rates/" + prestationCode;
        
        server.expect(requestTo(expectedUrl))
                .andRespond(withSuccess("{\"TVA\": 20.0, \"TR\": 5.0}", MediaType.APPLICATION_JSON));

        // When
        Map<String, Double> rates = client.getFiscalRates(prestationCode);

        // Then
        assertThat(rates).containsEntry("TVA", 20.0);
        assertThat(rates).containsEntry("TR", 5.0);
    }

    @Test
    void getFiscalRates_ShouldReturnEmptyMap_WhenServiceFails() {
        // Given
        String prestationCode = "P001";
        String expectedUrl = siFinanceUrl + "/rates/" + prestationCode;

        server.expect(requestTo(expectedUrl))
                .andRespond(org.springframework.test.web.client.response.MockRestResponseCreators.withServerError());

        // When
        Map<String, Double> rates = client.getFiscalRates(prestationCode);

        // Then
        assertThat(rates).isEmpty();
    }
}
