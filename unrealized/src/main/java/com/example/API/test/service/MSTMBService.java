package com.example.API.test.service;

import com.example.API.test.controller.dto.request.StockRequest;
import com.example.API.test.controller.dto.response.StockInfo;
import com.example.API.test.controller.dto.response.StockInfoResponse;
import com.example.API.test.controller.dto.response.Symbol;
import com.example.API.test.controller.dto.response.Symbols;
import com.example.API.test.model.MSTMBRepository;
import com.example.API.test.model.entity.MSTMB;
import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


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

        String message = checkStockRequest(request);
        if (message.length() > 1) {
            response.setResponseCode(message.substring(0, 3));//0~2
            response.setMessage(message.substring(3));//3~end
            return response;
        }

        // 如果要隨機更新單價 則不需輸入
        if (null == request.getPrice() && null != request.getStock()) {
            getNowPrice(request.getStock(), null);
        } else if (null != request.getPrice() && null != request.getStock() && request.getPrice() >= 10) {
            getNowPrice(request.getStock(), request.getPrice());
        } else if (null != request.getPrice() && null != request.getStock() && request.getPrice() < 10) {
            response.setResponseCode("002");
            response.setMessage("price 不得 < 10");
            return response;
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
        // check
        String checkResult = checkStockRequest(request);
        if (checkResult.length() > 1) {
            response.setResponseCode(checkResult.substring(0, 3));//0~2
            response.setMessage(checkResult.substring(3));//3~end
            return response;
        }

        MSTMB mstmb = repository.findByStock(request.getStock());
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

    private String checkStockRequest(StockRequest request) {
        StringBuilder checkResult = new StringBuilder();
        if (null == request.getStock() || request.getStock().isBlank() || request.getStock().isEmpty()) {
            checkResult.append("002必須輸入股票代號");
            return checkResult.toString();
        } else if (null == repository.findByStock(request.getStock())) {
            checkResult.append("001stock does not exist");
        }
        return checkResult.toString();
    }

    private String numberFormat(double value) {
        return String.format("%.2f", value);
    }

    public Double getNowPrice(String stock) {
        RestTemplate restTemplate = new RestTemplate();
        Symbols response = restTemplate.getForObject("http://systexdemo.ddns.net:443/Quote/Stock.jsp?stock=" + stock, Symbols.class);
        return Double.parseDouble(response.getSymbolList().get(0).getDealprice());
    }

    public Symbols getStockInfo(String stock) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            Symbols symbols = restTemplate.getForObject("http://systexdemo.ddns.net:443/Quote/Stock.jsp?stock=" + stock, Symbols.class);
            if (null == symbols) {
                return new Symbols(null, "002", "查無結果");
            }
            Stream<Symbol> existStock = symbols.getSymbolList().stream().filter(symbol -> 0 < Double.parseDouble(symbol.getDealprice()));
            Stream<Symbol> absent = symbols.getSymbolList().stream().filter(symbol -> 0 >= Double.parseDouble(symbol.getDealprice()));

            existStock.forEach(symbol -> {
                symbol.setResponseCode("000");
                symbol.setMessage("success");
            });

            absent.forEach(symbol -> {
                symbol.setResponseCode("001");
                symbol.setMessage("查無結果");
            });

            symbols.setResponseCode("000");
            symbols.setMessage("success");

            return symbols;

        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
            return new Symbols(null, "005", "連線失敗");
        }
    }

}
