package org.example.anpfacturationbackend.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Map;

@Component
public class GrcClient {

    private static final Logger logger = LoggerFactory.getLogger(GrcClient.class);
    private final RestTemplate restTemplate;

    @Value("${grc.url:http://localhost:8080/api/stub/grc}")
    private String grcUrl;

    @Value("${grc.username:}")
    private String username;

    @Value("${grc.password:}")
    private String password;

    public GrcClient(RestTemplateBuilder builder,
            @Value("${grc.timeout:5000}") int timeout) {
        this.restTemplate = builder
                .connectTimeout(Duration.ofMillis(timeout))
                .readTimeout(Duration.ofMillis(timeout))
                .build();
    }

    public boolean transmitInvoice(Map<String, Object> invoiceData) {
        logger.debug("Transmitting invoice to GRC ({})", grcUrl);
        try {
            if (username != null && !username.isEmpty()) {
                restTemplate.getInterceptors().add(
                        new org.springframework.http.client.support.BasicAuthenticationInterceptor(username, password));
            }
            restTemplate.postForEntity(grcUrl + "/invoices", invoiceData, String.class);
            return true;
        } catch (RestClientException e) {
            logger.error("Failed to transmit to GRC at {}", grcUrl, e);
            return false;
        }
    }
}
