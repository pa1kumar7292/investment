package com.mymoney.investment.service;

import com.google.common.collect.Maps;
import com.google.common.collect.Streams;
import com.mymoney.investment.dao.DataStub;
import com.mymoney.investment.dto.PortfolioDTO;
import com.mymoney.investment.enums.Assets;
import com.mymoney.investment.model.Fund;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.zip.DataFormatException;

@Service
@Slf4j
public class InvestmentServiceImpl implements InvestmentService {

    private final DataStub dataStub;

    public static final String CANNOT_REBALANCE = "CANNOT_REBALANCE";

    public InvestmentServiceImpl(DataStub dataStub) {
        this.dataStub = dataStub;
    }

    @Override
    public void allocate(List<Double> allocations) throws DataFormatException {
        log.info("Funds allocation started");
        if (Objects.nonNull(dataStub.initialAllocation)) {
            throw new IllegalStateException("The funds are already allocated");
        }
        dataStub.initialAllocation = createMyMoneyFundsWithDefaultOrdering(allocations);
        dataStub.desiredWeights = calculateDesiredWeight();

        log.info("Portfolio initialized with initial allocation of {} and desired weights of {}",
                dataStub.initialAllocation,
                dataStub.desiredWeights);
    }

    private PortfolioDTO createMyMoneyFundsWithDefaultOrdering(List<Double> allocations)
            throws DataFormatException {
        InvestmentHelper.validateInputs(dataStub.defaultAssetOrderForIO, allocations);
        List<Fund> fundEntityList =
                Streams.zip(dataStub.defaultAssetOrderForIO.stream(), allocations.stream(), Fund::new)
                        .collect(Collectors.toList());
        return new PortfolioDTO(fundEntityList);
    }

    private Map<Assets, Double> calculateDesiredWeight() {
        if (Objects.isNull(dataStub.initialAllocation)) {
            throw new IllegalStateException("The funds are not yet Allocated");
        }
        return dataStub.initialAllocation.getFunds().stream()
                .collect(
                        Collectors.toMap(
                                Fund::getAssets,
                                e -> e.getAmount() * 100 / dataStub.initialAllocation.getTotalInvestment()));
    }



    @Override
    public void sip(List<Double> sips) throws DataFormatException {
        if (Objects.nonNull(dataStub.initialSip)) {
            throw new IllegalStateException("The SIP is already started once");
        }
        dataStub.initialSip = createMyMoneyFundsWithDefaultOrdering(sips);
        log.debug("Portfolio initialized with a monthly sip of {} ", dataStub.initialSip);
    }

    @Override
    public void change(List<Double> rates, Month month) throws IllegalStateException, DataFormatException {
        if (Objects.nonNull(dataStub.monthlyMarketChangeRate.getOrDefault(month, null))) {
            throw new IllegalStateException(
                    "The Rate of Change for month " + month.name() + " is already registered");
        }
        if (Objects.isNull(rates) || Objects.isNull(month)) {
            throw new InputMismatchException("One of the supplied parameter is null.");
        }
        if (rates.size() != dataStub.defaultAssetOrderForIO.size()) {
            throw new DataFormatException("The input is not in the desired format");
        }
        Map<Assets, Double> change =
                Streams.zip(dataStub.defaultAssetOrderForIO.stream(), rates.stream(), Maps::immutableEntry)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        dataStub.monthlyMarketChangeRate.put(month, change);
    }

    @Override
    public String balance(Month month) {
        updateBalance();
        PortfolioDTO fund =
                Optional.ofNullable(dataStub.monthlyBalance.get(month))
                        .orElseThrow(() -> new IllegalStateException(
                                                "The balance is requested for the month  "
                                                        + month.name()
                                                        + " no data"));
        return fund.toString();
    }

    private void updateBalance() {
        Map.Entry<Month, PortfolioDTO> lastCalculatedBalance =
                dataStub.monthlyBalance.lastEntry();
        Map.Entry<Month, Map<Assets, Double>> lastKnownChange =
                dataStub.monthlyMarketChangeRate.lastEntry();
        if (Objects.isNull(lastKnownChange)) {
            throw new IllegalStateException("Rate of Change is not defined");
        }
        if (Objects.isNull(lastCalculatedBalance)) {
            log.info("Calculating balance for the given month");
            PortfolioDTO myMoneyFund =
                    calculateBalance(
                            dataStub.initialAllocation,
                            null,
                            dataStub.monthlyMarketChangeRate.get(Month.JANUARY));
            dataStub.monthlyBalance.put(Month.JANUARY, myMoneyFund);
            lastCalculatedBalance = dataStub.monthlyBalance.lastEntry();
        }
        if (lastCalculatedBalance.getKey() != lastKnownChange.getKey()) {
            Month startMonth = lastCalculatedBalance.getKey();
            Month endMonth = lastKnownChange.getKey();
            for (int index = startMonth.getValue(); index < endMonth.getValue(); index++) {
                Month lastUpdatedMonth = Month.of(index);
                Month currentCalculationMonth = Month.of(index + 1);
                log.debug("Calculating balance for month of {}", currentCalculationMonth);
                PortfolioDTO carryOverBalance =
                        dataStub.monthlyBalance.get(lastUpdatedMonth).clone();
                Map<Assets, Double> changeRate =
                        dataStub.monthlyMarketChangeRate.get(currentCalculationMonth);
                PortfolioDTO availableBalance =
                        calculateBalance(carryOverBalance, dataStub.initialSip, changeRate);
                if (shouldReBalance(currentCalculationMonth)) {
                    availableBalance = doReBalance(availableBalance);
                }
                dataStub.monthlyBalance.putIfAbsent(currentCalculationMonth, availableBalance);
            }
        }
    }

    private PortfolioDTO calculateBalance(
            PortfolioDTO carryOverBalance,
            PortfolioDTO monthlySip,
            Map<Assets, Double> changeRate) {
        log.debug(
                "Updating current balance of {}, with a sip of {} and market change rate of {}",
                carryOverBalance,
                monthlySip,
                changeRate);
        PortfolioDTO balAfterSip = applySipInvestment(carryOverBalance, monthlySip);
        return applyMarketChange(balAfterSip, changeRate);
    }

    private PortfolioDTO applyMarketChange(
            PortfolioDTO carryOverBalance, Map<Assets, Double> changeRate) {
        List<Fund> funds = carryOverBalance.getFunds();
        funds.forEach(
                entity -> {
                    double rate = changeRate.get(entity.getAssets());
                    double updatedAmount = entity.getAmount() * (1 + rate / 100);
                    entity.setAmount(Math.floor(updatedAmount));
                });
        return carryOverBalance;
    }

    private PortfolioDTO applySipInvestment(
            PortfolioDTO carryOverBalance, PortfolioDTO initialSip) {
        List<Fund> funds = carryOverBalance.getFunds();
        if (Objects.nonNull(initialSip)) {
            IntStream.range(0, funds.size())
                    .forEach(
                            index -> {
                                Fund fundEntity = funds.get(index);
                                double sipAmount = initialSip.getFunds().get(index).getAmount();
                                fundEntity.setAmount(Math.floor(fundEntity.getAmount() + sipAmount));
                            });
        }
        return carryOverBalance;
    }

    @Override
    public String reBalance() {
        updateBalance();
        Month lastUpdatedMonth = dataStub.monthlyBalance.lastEntry().getKey();
        Month lastRebalancedMonth = getLastReBalancedMonth(lastUpdatedMonth);
        PortfolioDTO balance = dataStub.monthlyBalance.getOrDefault(lastRebalancedMonth, null);
        return Objects.nonNull(balance) ? balance.toString() : CANNOT_REBALANCE;
    }

    private Month getLastReBalancedMonth(Month month) {
        return month == Month.DECEMBER ? month : Month.JUNE;
    }

    private boolean shouldReBalance(Month month) {
        // Assumption#3: The re-balancing happens on 6 and 12 months.
        return month.equals(Month.JUNE) || month.equals(Month.DECEMBER);
    }

    private PortfolioDTO doReBalance(PortfolioDTO currentFunds) {
        List<Fund> funds = currentFunds.getFunds();
        double totalInvestment = currentFunds.getTotalInvestment();
        funds.forEach(
                entity -> {
                    double desiredWeight = dataStub.desiredWeights.get(entity.getAssets());
                    entity.setAmount(Math.floor(totalInvestment * desiredWeight / 100));
                });
        log.info("Re-balanced the current total balance of {} to desired weights of {} to {}",
                currentFunds.getTotalInvestment(),
                dataStub.desiredWeights,
                currentFunds);
        return currentFunds;
    }

    @Override
    public int getSupportedAssets() {
        return dataStub.defaultAssetOrderForIO.size();
    }
}
