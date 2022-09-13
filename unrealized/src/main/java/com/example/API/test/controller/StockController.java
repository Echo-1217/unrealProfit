package com.example.API.test.controller;

import com.example.API.test.controller.dto.request.StockRequest;
import com.example.API.test.controller.dto.response.StockInfo;
import com.example.API.test.controller.dto.response.StockInfoResponse;
import com.example.API.test.service.MSTMBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/stockInfo")
public class StockController {
    @Autowired
    private MSTMBService service;

    @PostMapping("/update")
    public StockInfoResponse updateStockPrice(@RequestBody StockRequest request){
        return service.updatePrice(request);
    }
    @PostMapping("/find")
    public StockInfoResponse findByStock(@RequestBody StockRequest request){
        return service.findByStock(request);
    }

}
