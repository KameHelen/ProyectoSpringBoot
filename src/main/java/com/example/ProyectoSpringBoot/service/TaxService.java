package com.example.ProyectoSpringBoot.service;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class TaxService {

    private static final BigDecimal TAX_ES = new BigDecimal("0.21"); // 21% Spain
    private static final BigDecimal TAX_US = new BigDecimal("0.10"); // 10% USA
    private static final BigDecimal TAX_DEFAULT = new BigDecimal("0.15"); // 15% Others

    public BigDecimal calculateTax(BigDecimal amount, String country) {
        if (amount == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal taxRate;
        if ("ES".equalsIgnoreCase(country)) {
            taxRate = TAX_ES;
        } else if ("US".equalsIgnoreCase(country)) {
            taxRate = TAX_US;
        } else {
            taxRate = TAX_DEFAULT;
        }

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
