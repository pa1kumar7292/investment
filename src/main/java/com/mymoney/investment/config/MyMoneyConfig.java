package com.mymoney.investment.config;

import com.mymoney.investment.dao.DataStub;
import com.mymoney.investment.enums.Assets;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyMoneyConfig {

    @Bean
    public DataStub dataStub() {
        DataStub stub = new DataStub();
        stub.defaultAssetOrderForIO.add(Assets.EQUITY);
        stub.defaultAssetOrderForIO.add(Assets.DEBT);
        stub.defaultAssetOrderForIO.add(Assets.GOLD);
        return stub;
    }
}
