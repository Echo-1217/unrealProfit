package com.example.API.test.controller.dto.response;

import com.example.API.test.model.entity.MSTMB;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StockInfo {
    private String Stock;
    private String StockName;
    private String CurPrice;//當前價格
}
