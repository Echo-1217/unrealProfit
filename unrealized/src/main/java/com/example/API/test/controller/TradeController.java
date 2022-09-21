package com.example.API.test.controller;

import com.example.API.test.controller.dto.request.CreateHcmioRequest;
import com.example.API.test.controller.dto.request.UnrealProfitRequest;
import com.example.API.test.controller.dto.response.SettlementAmountResponse;
import com.example.API.test.controller.dto.response.SumUnrealProfitResponse;
import com.example.API.test.controller.dto.response.DetailResponse;
import com.example.API.test.service.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("api/v1/unreal")
public class TradeController {

    @Autowired
    private TradeService tradeService;

    @PostMapping("/add")
    public DetailResponse createHcmio(@RequestBody CreateHcmioRequest request) {
        try {
            return this.tradeService.createHCMIO(request);
        }
        catch (Exception exception){
            return new DetailResponse(null,"005","連線失敗");
        }
    }

    @PostMapping("/detail")
    public DetailResponse getUnrealized(@RequestBody UnrealProfitRequest request) {
        try {
            return this.tradeService.getUnrealValue(request);
        }
       catch (Exception exception){
            return new DetailResponse(null,"005","連線失敗");
       }
    }

    @PostMapping("/sum")
    public SumUnrealProfitResponse getSumUnrealized(@RequestBody UnrealProfitRequest request) {
        try {
            return this.tradeService.getSum(request);
        }
       catch (Exception exception){
           return new SumUnrealProfitResponse(null,"005","連線失敗");
       }
    }

    @PostMapping("/settlement")
    public SettlementAmountResponse getSettlementAmount(@RequestBody UnrealProfitRequest request) throws ParseException {
        try {
            return this.tradeService.getSettlementAmount(request);
        }
        catch (Exception exception){
            return new SettlementAmountResponse(null,null,"005","連線失敗");
        }
    }
}
