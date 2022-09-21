package com.example.API.test.service;

import com.example.API.test.controller.dto.request.UnrealProfitRequest;
import com.example.API.test.model.TCNUDRepository;
import com.example.API.test.model.entity.TCNUD;
import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class CalService {
    @Autowired
    TCNUDRepository tcnudRepository;

    public Long calUnreal(double nowPrice, Long cost, Long qty) {
        return (long) Precision.round((nowPrice * qty) - cost - calFee(calAmt(nowPrice, qty)) - calTax(calAmt(nowPrice, qty)), 0);
    }

    public Long calMarketValue(double nowPrice, Long qty) {
        return (calAmt(nowPrice, qty) - calFee(calAmt(nowPrice, qty)) - calTax(calAmt(nowPrice, qty)));
    }

    public Long calAmt(Double price, Long qty) {
        return (long) Precision.round(price * qty, 0);
    }

    public Long calFee(Long amt) {
        return (long) Precision.round(amt * 0.001425, 0);
    }

    public Long calTax(Long amt) {
        return (long) Precision.round(amt * 0.003, 0);
    }


    public double calProfitability(Long unrealProfit, Long cost) {
        return Precision.round((unrealProfit.doubleValue() / cost.doubleValue()) * 100, 2);
    }

    public Long calSumCost(UnrealProfitRequest request, String date) {
        List<TCNUD> tcnudList = tcnudRepository.findByBCT(request.getBranchNo().toUpperCase(), request.getCustSeq(), date);
        Long sum = 0L;
        for (TCNUD tcnud : tcnudList) {
            sum += tcnud.getCost();
        }
        return sum;
    }
}
