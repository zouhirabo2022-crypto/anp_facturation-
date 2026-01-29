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
public class PrestClient {

    private static final Logger logger = LoggerFactory.getLogger(PrestClient.class);
    private final RestTemplate restTemplate;

    @Value("${prest.url:http://localhost:8080/api/stub/prest}")
    private String prestUrl;

    @Value("${prest.username:}")
    private String username;

    @Value("${prest.password:}")
    private String password;

    public PrestClient(RestTemplateBuilder builder,
            @Value("${prest.timeout:5000}") int timeout) {
        this.restTemplate = builder
                .connectTimeout(Duration.ofMillis(timeout))
                .readTimeout(Duration.ofMillis(timeout))
                .build();
    }

    public boolean transmitInvoice(Map<String, Object> invoiceData) {
        logger.debug("Transmitting invoice to PREST ({})", prestUrl);
        try {
            if (username != null && !username.isEmpty()) {
                restTemplate.getInterceptors().add(
                        new org.springframework.http.client.support.BasicAuthenticationInterceptor(username, password));
            }
            restTemplate.postForEntity(prestUrl + "/invoices", invoiceData, String.class);
            return true;
        } catch (RestClientException e) {
            logger.error("Failed to transmit to PREST at {}", prestUrl, e);
            return false;
        }
    }
}
