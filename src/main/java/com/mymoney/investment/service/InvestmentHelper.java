package com.mymoney.investment.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class InvestmentHelper {

    private final InvestmentService investmentService;

    public InvestmentHelper(InvestmentService investmentService) {
        this.investmentService = investmentService;
    }


    public List<String> processInvestment(String file) throws IOException {

        return null;
    }

    private String processLine(String line) {

        return null;
    }

    public static boolean isNullOrEmpty(String inputString) {
        if (inputString == null || inputString. isEmpty())
            return true;
        return false;
    }



}
