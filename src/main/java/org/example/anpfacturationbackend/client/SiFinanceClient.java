package org.example.anpfacturationbackend.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Client for SI Finance Web Services.
 * Connects to the local Stub Controller for simulation.
 */
@Component
public class SiFinanceClient {

    private static final Logger logger = LoggerFactory.getLogger(SiFinanceClient.class);
    private final RestTemplate restTemplate;

    @Value("${si.finance.url:http://localhost:8080/api/stub/si-finance}")
    private String siFinanceUrl;

    @Value("${si.finance.username:}")
    private String username;

    @Value("${si.finance.password:}")
    private String password;

    private final String stubUrl = "http://localhost:8080/api/stub/si-finance";

    public SiFinanceClient(RestTemplateBuilder builder,
                           @Value("${si.finance.timeout:5000}") int timeout) {
        this.restTemplate = builder
                .connectTimeout(java.time.Duration.ofMillis(timeout))
                .readTimeout(java.time.Duration.ofMillis(timeout))
                .build();
    }

    public Map<String, Double> getFiscalRates(String prestationCode) {
        String targetUrl = (siFinanceUrl != null && !siFinanceUrl.isEmpty()) ? siFinanceUrl : stubUrl;
        logger.debug("Fetching fiscal rates from SI Finance ({}) for: {}", targetUrl, prestationCode);
        try {
            // Add Basic Auth if configured
            if (username != null && !username.isEmpty()) {
                restTemplate.getInterceptors().add(new org.springframework.http.client.support.BasicAuthenticationInterceptor(username, password));
            }

            return restTemplate.exchange(
                targetUrl + "/rates/" + prestationCode,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Double>>() {}
            ).getBody();
        } catch (RestClientException e) {
            logger.error("Failed to call SI Finance at {}, using fallback.", targetUrl, e);
            // Return empty map to trigger fallback in FiscalRateService
            return Map.of();
        }
    }

    public boolean transmitInvoice(Map<String, Object> invoiceData) {
        String targetUrl = (siFinanceUrl != null && !siFinanceUrl.isEmpty()) ? siFinanceUrl : stubUrl;
        logger.debug("Transmitting invoice to SI Finance ({})", targetUrl);
        try {
            restTemplate.postForEntity(targetUrl + "/invoices", invoiceData, String.class);
            return true;
        } catch (RestClientException e) {
            logger.error("Failed to transmit to SI Finance", e);
            return false;
        }
    }
}
