package com.mymoney.investment.dao;

import com.mymoney.investment.dto.PortfolioDTO;
import com.mymoney.investment.enums.Assets;
import lombok.Getter;
import lombok.Setter;

import java.time.Month;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@Getter
@Setter
public class DataStub {

    public TreeMap<Month, PortfolioDTO> monthlyBalance = new TreeMap<>();
    public TreeMap<Month, Map<Assets, Double>> monthlyMarketChangeRate = new TreeMap<>();
    public PortfolioDTO initialAllocation;
    public PortfolioDTO initialSip;
    public Map<Assets, Double> desiredWeights = new HashMap<>();
    public Set<Assets> defaultAssetOrderForIO = new LinkedHashSet<>();
}
