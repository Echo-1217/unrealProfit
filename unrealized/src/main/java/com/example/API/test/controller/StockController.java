package com.example.API.test.controller;

import com.example.API.test.controller.dto.request.StockRequest;
import com.example.API.test.controller.dto.response.StockInfoResponse;
import com.example.API.test.controller.dto.response.Symbol;
import com.example.API.test.controller.dto.response.Symbols;
import com.example.API.test.service.MSTMBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/v1/stockInfo")
public class StockController {
    @Autowired
    private MSTMBService service;

    @PostMapping("/update")
    public StockInfoResponse updateStockPrice(@RequestBody StockRequest request) {
        try {
            return service.updatePrice(request);
        }
        catch (Exception exception){
            return new StockInfoResponse(null,"005","連線失敗");
        }
    }

    @GetMapping("/find")
    public Symbol findByStock(@RequestParam String stock) {
        try {
             return service.getStockInfo(stock);
        }
        catch (Exception exception){
            return new Symbol(null,null,null,null,"005","連線失敗");
        }
    }
}
