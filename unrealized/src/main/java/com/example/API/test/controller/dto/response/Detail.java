package com.example.API.test.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Detail {
    private String tradeDate;
    private String docSeq;
    private String stock;
    private String stockName;
    private String buyPrice;
    private String nowPrice;
    private Long qty;
    private Long remainQty;
    private Long fee;
    private Long cost;
    private Long marketValue;
    private Long unrealProfit;
    private String profitability;
}
