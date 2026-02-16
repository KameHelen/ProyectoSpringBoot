package com.example.ProyectoSpringBoot.service;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Service
public class TaxService {

    private static final Map<String, BigDecimal> TAX_RATES = new HashMap<>();

    static {
        TAX_RATES.put("ES", new BigDecimal("0.21"));
        TAX_RATES.put("España", new BigDecimal("0.21"));
        TAX_RATES.put("Spain", new BigDecimal("0.21"));
        TAX_RATES.put("FR", new BigDecimal("0.20"));
        TAX_RATES.put("Francia", new BigDecimal("0.20"));
        TAX_RATES.put("France", new BigDecimal("0.20"));
        TAX_RATES.put("US", new BigDecimal("0.10"));
        TAX_RATES.put("EEUU", new BigDecimal("0.10"));
        TAX_RATES.put("USA", new BigDecimal("0.10"));
        // Default
        TAX_RATES.put("DEFAULT", new BigDecimal("0.21"));
    }

    public BigDecimal getTaxRate(String country) {
        if (country == null) {
            return TAX_RATES.get("DEFAULT");
        }
        // Normalize maybe? For now exact match or default
        // Try uppercase first
        String key = country.trim();
        if (TAX_RATES.containsKey(key)) {
            return TAX_RATES.get(key);
        }
        // Case insensitive check
        for (String k : TAX_RATES.keySet()) {
            if (k.equalsIgnoreCase(key)) {
                return TAX_RATES.get(k);
            }
        }
        return TAX_RATES.get("DEFAULT");
    }

    public BigDecimal calculateTax(BigDecimal amount, String country) {
        if (amount == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal taxRate = getTaxRate(country);
        return amount.multiply(taxRate).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateTotalWithTax(BigDecimal amount, String country) {
        if (amount == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal taxForAmount = calculateTax(amount, country);
        return amount.add(taxForAmount);
    }
}
