package com.example.API.test.controller.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateHcmioRequest {
    private String TradeDate;
    private String BranchNo;
    private String CustSeq;
    private String BsType = "B"; //買賣別
    private String Stock; //股票代號
    private Double price;
    private Long Qty;

}
