package com.example.API.test.service;

import com.example.API.test.controller.dto.request.StockRequest;
import com.example.API.test.controller.dto.response.StockInfo;
import com.example.API.test.controller.dto.response.StockInfoResponse;
import com.example.API.test.model.MSTMBRepository;
import com.example.API.test.model.entity.MSTMB;
import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@Service
public class MSTMBService {
    @Autowired
    private MSTMBRepository repository;


    private void getNowPrice(String stock, Double price) {
        DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter time = DateTimeFormatter.ofPattern("HHmmss");
        MSTMB e = repository.findByStock(stock);
        if (null == price) {
            double max = e.getRefPrice() * 1.1;// 每次執行漲跌 +-10%
            double min = e.getRefPrice() * 0.9;
            e.setModDate(date.format(LocalDateTime.now()));
            e.setModTime(time.format(LocalDateTime.now()));
            //random = (Math.random()*(MAX-min+1)) + min;
            e.setCurPrice(Precision.round((Math.random() * (max - min + 1) + min), 2));
        } else {
            e.setModDate(date.format(LocalDateTime.now()));
            e.setModTime(time.format(LocalDateTime.now()));
            e.setCurPrice(price);
        }
        repository.save(e);
    }

    @CachePut(cacheNames = "stockInfo", key = "#request.getStock()")
    public StockInfoResponse updatePrice(StockRequest request) {
        StockInfoResponse response = new StockInfoResponse();
        List<StockInfo> stockInfoList = new ArrayList<>();
        List<MSTMB> mstmbList = new ArrayList<>();

        if (null == request.getStock()) {
            response.setResponseCode("002");
            response.setMessage("必須輸入股票代號");
            return response;
        }

        // 如果要隨機更新單價 則不需輸入
        if (null == request.getPrice() && null != request.getStock()) {
            getNowPrice(request.getStock(), null);
        } else if (null != request.getPrice() && null != request.getStock() && request.getPrice() > 0) {
            getNowPrice(request.getStock(), request.getPrice());
        }

        mstmbList.add(repository.findByStock(request.getStock()));
        mstmbList.forEach(s -> {
            stockInfoList.add(new StockInfo(s.getStock(), s.getStockName(), numberFormat(s.getCurPrice())));
        });
        return new StockInfoResponse(stockInfoList, "000", "success");
    }

    @Cacheable(cacheNames = "stockInfo", key = "#request.getStock()")
    public StockInfoResponse findByStock(StockRequest request) {
        StockInfoResponse response = new StockInfoResponse();
        StockInfo stockInfo = new StockInfo();
        MSTMB mstmb = repository.findByStock(request.getStock());
        if (null == mstmb) {
            response.setResponseCode("001");
            response.setMessage("查無結果");
            return response;
        }
        stockInfo.setStock(mstmb.getStock());
        stockInfo.setCurPrice(numberFormat(mstmb.getCurPrice()));
        stockInfo.setStockName(mstmb.getStockName());
        List<StockInfo> stockInfoList = new ArrayList<>();

        stockInfoList.add(stockInfo);
        response.setStockInfoList(stockInfoList);
        response.setResponseCode("000");
        response.setMessage("success");
        return response;
    }

    private String numberFormat(double value) {
        return String.format("%.2f", value);
    }
}
