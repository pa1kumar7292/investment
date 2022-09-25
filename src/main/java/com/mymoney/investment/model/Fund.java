package com.mymoney.investment.model;

import com.mymoney.investment.enums.Assets;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class Fund {

    @NonNull
    private Assets assets;
    @NonNull
    private Double amount;
}
