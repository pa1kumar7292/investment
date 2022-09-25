package com.mymoney.investment.dto;

import com.mymoney.investment.model.Fund;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@RequiredArgsConstructor
public class PortfolioDTO implements Cloneable {
    @NonNull
    private final List<Fund> funds;

    @Override
    public PortfolioDTO clone() {
        return new PortfolioDTO(
                funds.stream()
                        .map(e -> new Fund(e.getAssets(), e.getAmount()))
                        .collect(Collectors.toList()));
    }

    @Override
    public String toString() {
        return funds.stream()
                .map(entity -> Integer.toString((int) Math.floor(entity.getAmount())))
                .collect(Collectors.joining(" "));
    }

    /**
     * @return total investment across all asset
     */
    public double getTotalInvestment() {
        return funds.stream().mapToDouble(Fund::getAmount).sum();
    }
}
