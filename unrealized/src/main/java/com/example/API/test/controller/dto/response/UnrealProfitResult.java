package com.example.API.test.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UnrealProfitResult {
    private String stock;
    private String stockName;
    private String nowPrice;
    private Long sumRemainQty;
    private Long sumFee;
    private Long sumCost;
    private Long sumMarketValue;
    private Long sumUnrealProfit;
    private String sumProfitability;
    private List<Detail> detail;
}
