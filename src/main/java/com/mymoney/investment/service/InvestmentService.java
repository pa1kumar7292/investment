package com.mymoney.investment.service;

import java.time.Month;
import java.util.List;
import java.util.zip.DataFormatException;

public interface InvestmentService {
    void allocate(List<Double> allocations) throws DataFormatException;

    void sip(List<Double> sips) throws DataFormatException;

    void change(List<Double> rates, Month month) throws IllegalStateException, DataFormatException;

    String balance(Month month);

    String reBalance();

    int getSupportedAssets();
}
