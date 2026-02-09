package org.anpfacturationbackend.service;

import org.anpfacturationbackend.client.SiFinanceClient;
import org.anpfacturationbackend.entity.Prestation;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class FiscalRateService {

    private final SiFinanceClient siFinanceClient;

    public FiscalRateService(SiFinanceClient siFinanceClient) {
        this.siFinanceClient = siFinanceClient;
    }

    public double getTvaRate(Prestation prestation) {
        Map<String, Double> rates = siFinanceClient.getFiscalRates(prestation.getCode());
        if (rates == null || rates.isEmpty()) {
            return prestation.getTauxTva();
        }
        return rates.getOrDefault("TVA", prestation.getTauxTva());
    }

    public double getTrRate(Prestation prestation) {
        Map<String, Double> rates = siFinanceClient.getFiscalRates(prestation.getCode());
        if (rates == null || rates.isEmpty()) {
            return prestation.getTauxTr();
        }
        return rates.getOrDefault("TR", prestation.getTauxTr());
    }
}

