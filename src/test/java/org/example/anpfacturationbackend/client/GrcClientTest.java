package org.example.anpfacturationbackend.client;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(GrcClient.class)
@TestPropertySource(properties = {
        "grc.url=http://localhost:8080/api/stub/grc",
        "grc.username=grcuser",
        "grc.password=grcpass"
})
class GrcClientTest {

    @Autowired
    private GrcClient client;

    @Autowired
    private MockRestServiceServer server;

    @Value("${grc.url}")
    private String grcUrl;

    @Test
    void transmitInvoice_ShouldAddBasicAuthHeader() {
        // Given
        Map<String, Object> invoiceData = new HashMap<>();
        invoiceData.put("numero", "F123");
        String expectedUrl = grcUrl + "/invoices";

        String auth = "grcuser:grcpass";
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        String authHeader = "Basic " + encodedAuth;

        server.expect(requestTo(expectedUrl))
                .andExpect(header(HttpHeaders.AUTHORIZATION, authHeader))
                .andRespond(withSuccess());

        // When
        boolean result = client.transmitInvoice(invoiceData);

        // Then
        assertThat(result).isTrue();
    }
}
