package com.mymoney.investment.service;

import org.springframework.stereotype.Service;

import java.time.Month;
import java.util.List;
import java.util.zip.DataFormatException;

@Service
public class InvestmentServiceImpl implements InvestmentService {
    @Override
    public void allocate(List<Double> allocations) throws DataFormatException {

    }

    @Override
    public void sip(List<Double> sips) throws DataFormatException {

    }

    @Override
    public void change(List<Double> rates, Month month) throws IllegalStateException, DataFormatException {

    }

    @Override
    public String balance(Month month) {
        return null;
    }

    @Override
    public String reBalance() {
        return null;
    }

    @Override
    public int getSupportedAssets() {
        return 0;
    }
}
