package com.example.API.test.controller;

import com.example.API.test.controller.dto.request.CreateHcmioRequest;
import com.example.API.test.controller.dto.request.UnrealProfitRequest;
import com.example.API.test.controller.dto.response.SumUnrealProfitResponse;
import com.example.API.test.controller.dto.response.TransactionResponse;
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
    public TransactionResponse createHcmio(@RequestBody CreateHcmioRequest request) {
        return this.tradeService.createHCMIO(request);
    }

    @PostMapping("/detail")
    public TransactionResponse getUnrealized(@RequestBody UnrealProfitRequest request){
        return this.tradeService.getUnrealValue(request);
    }

    @PostMapping("/sum")
    public SumUnrealProfitResponse getSumUnrealized(@RequestBody UnrealProfitRequest request) {
        return  this.tradeService.getSum(request);
    }
    @PostMapping("/settlement")
    public Long getSettlementAmount(@RequestBody UnrealProfitRequest request) throws ParseException {
        return this.tradeService.getSettlementAmount(request);
    }
}
