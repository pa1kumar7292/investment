package com.mymoney.investment.controller;

import com.mymoney.investment.service.InvestmentService;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.time.Month;
import java.util.List;
import java.util.zip.DataFormatException;


@ShellComponent

public class InvestmentController {

    private final InvestmentService investmentService;


    public InvestmentController(InvestmentService investmentService) {
        this.investmentService = investmentService;
    }

    @ShellMethod("Received the initial investment amounts for each equity, debt and gold.")
    public void allocate(List<Double> allocations) throws DataFormatException {
        investmentService.allocate(allocations);
    }

    @ShellMethod(
            "Received the investment amount on a monthly basis for each equity, debt and gold.")
    public void sip(List<Double> sips) throws DataFormatException {
        investmentService.sip(sips);
    }

    @ShellMethod(
            "Received the monthly rate of change (growth or loss) for each equity, debt and gold.")
    public void change(List<Double> rates, Month month) throws DataFormatException {
        investmentService.change(rates, month);
    }

    @ShellMethod("Print the balance as on given month for each equity, debt and gold.")
    public String balance(Month month) {
        return investmentService.balance(month);
    }

    @ShellMethod(
            "Print the rebalanced amount of each fund equity, debt and gold for the last 6 months. If at-least 6 months data is not available then print CANNOT_REBALANCE.")
    public String rebalance() {
        return investmentService.reBalance();
    }

}
