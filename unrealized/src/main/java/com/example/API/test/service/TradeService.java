package com.example.API.test.service;

import com.example.API.test.controller.dto.request.CreateHcmioRequest;
import com.example.API.test.controller.dto.request.UnrealProfitRequest;
import com.example.API.test.controller.dto.response.*;
import com.example.API.test.model.HCMIORepository;
import com.example.API.test.model.MSTMBRepository;
import com.example.API.test.model.TCNUDRepository;
import com.example.API.test.model.entity.CalendarUtils;
import com.example.API.test.model.entity.HCMIO;
import com.example.API.test.model.entity.MSTMB;
import com.example.API.test.model.entity.TCNUD;
import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class TradeService {
    @Autowired
    private TCNUDRepository tcnudRepository;
    @Autowired
    private MSTMBRepository mstmbRepository;
    @Autowired
    private HCMIORepository hcmioRepository;

    DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyyMMdd");
    DateTimeFormatter time = DateTimeFormatter.ofPattern("HHmmss");


    //=============================================================================
    public TransactionResponse getUnrealValue(UnrealProfitRequest request) {
        TransactionResponse response = new TransactionResponse();

        // check
        String checkResult = checkUnrealProfitRequest(request);
        if (checkResult.length() > 1) {
            response.setResponseCode(checkResult.substring(0, 3));//0~2
            response.setMessage(checkResult.substring(3));//3~end
            return response;
        }
        // ============符合格式=============
        List<String> stockList = getStockList(request);

        List<Detail> detail = new ArrayList<>();
        for (String stock : stockList) {
            detail.addAll(getDetailList(new UnrealProfitRequest(request.getBranchNo(), request.getCustSeq(), stock, request.getMinLimit(), request.getMaxLimit())));
        }

        if (detail.isEmpty()) {
            response.setResponseCode("001");
            response.setMessage("查無符合資料");
            return response;
        }
        try {
            response.setDetail(detail);
            response.setResponseCode("000");
            response.setMessage("查詢成功");
        } catch (Exception exception) {
            response.setResponseCode("005");
            response.setMessage("Service Error");
            System.out.println(Arrays.toString(exception.getStackTrace()));
            return response;
        }
        return response;

    }

    public SumUnrealProfitResponse getSum(UnrealProfitRequest request) {
        SumUnrealProfitResponse response = new SumUnrealProfitResponse();

        // check
        String checkResult = checkUnrealProfitRequest(request);
        if (checkResult.length() > 1) {
            response.setResponseCode(checkResult.substring(0, 3));//0~2
            response.setMessage(checkResult.substring(3));//3~end
            return response;
        }
        // ============符合格式=============
        List<String> stockList = getStockList(request);

        List<UnrealProfitResult> unrealProfitResultList = new ArrayList<>();
        try {
            for (String stock : stockList) {
//                mstmbService.getNowPrice(stock);// 讓股票資訊價格隨機更動
                MSTMB mstmb = mstmbRepository.findByStock(stock);
                UnrealProfitResult unrealProfitResult = new UnrealProfitResult();
                List<Detail> detailList = getDetailList(new UnrealProfitRequest(request.getBranchNo(), request.getCustSeq(), stock, request.getMinLimit(), request.getMaxLimit()));
                if (!detailList.isEmpty()) {
                    unrealProfitResult.setDetail(detailList); // 過濾後可能為空List
                    for (Detail detail : unrealProfitResult.getDetail()) {
                        unrealProfitResult.setSumRemainQty((null == unrealProfitResult.getSumRemainQty()) ? detail.getRemainQty() : unrealProfitResult.getSumRemainQty() + detail.getQty());
                        unrealProfitResult.setSumFee((null == unrealProfitResult.getSumFee()) ? detail.getFee() : unrealProfitResult.getSumFee() + detail.getFee());
                        unrealProfitResult.setSumCost((null == unrealProfitResult.getSumCost()) ? detail.getCost() : unrealProfitResult.getSumCost() + detail.getCost());
                    }// 個別彙總
                    unrealProfitResult.setStock(stock);
                    unrealProfitResult.setStockName(mstmb.getStockName());
                    unrealProfitResult.setNowPrice(numberFormat(mstmb.getCurPrice()));
                    unrealProfitResult.setSumMarketValue(calMarketValue(Double.parseDouble(unrealProfitResult.getNowPrice()), unrealProfitResult.getSumRemainQty()));
                    unrealProfitResult.setSumUnrealProfit(calUnreal(Double.parseDouble(unrealProfitResult.getNowPrice()), unrealProfitResult.getSumCost(), unrealProfitResult.getSumRemainQty()));
                    unrealProfitResult.setSumProfitability(numberFormat(calProfitability(unrealProfitResult.getSumUnrealProfit(), unrealProfitResult.getSumCost())) + "%");
                    unrealProfitResultList.add(unrealProfitResult); //彙總
                }
            }
        } catch (Exception exception) {
            response.setResponseCode("005");
            response.setMessage("Service Error");
            System.out.println(Arrays.toString(exception.getStackTrace()));
            return response;
        }
        if (unrealProfitResultList.isEmpty()) {
            response.setResponseCode("001");
            response.setMessage("查無符合資料");
            return response;
        }
        return new SumUnrealProfitResponse(
                unrealProfitResultList,
                "000",
                "success"
        );
    }


    //===============================================================================
    @Transactional
    public TransactionResponse createHCMIO(CreateHcmioRequest request) {
        Detail detail = new Detail();
        TransactionResponse response = new TransactionResponse();
        HCMIO hcmio = new HCMIO();
        MSTMB mstmb = mstmbRepository.findByStock(request.getStock());

        request.setBsType(request.getBsType().toUpperCase());

        String message = checkCreateRequest(request);
        if (message.length() > 1) {
            response.setResponseCode("002");
            response.setMessage(message);
            return response;
        }

        try {
            // 基本資料
            hcmio.setQty(request.getQty());
            hcmio.setStock(request.getStock());
            hcmio.setBsType(request.getBsType()); //定義買賣別
            hcmio.setBranchNo(request.getBranchNo());
            hcmio.setCustSeq(request.getCustSeq());

            //time
            if (null == request.getTradeDate()) {
                hcmio.setDocSeq(getNewDocSeq(date.format(LocalDateTime.now())));// 產生流水單號
                hcmio.setTradeDate(date.format(LocalDateTime.now()));
            } else {
                hcmio.setDocSeq(getNewDocSeq(request.getTradeDate()));// 產生流水單號
                hcmio.setTradeDate(request.getTradeDate());
            }

            hcmio.setModDate(date.format(LocalDateTime.now()));
            hcmio.setModTime(time.format(LocalDateTime.now()));
            //cal===============================================================
            hcmio.setPrice(Precision.round(request.getPrice(), 2));//決定買入價格
            hcmio.setAmt(calAmt(hcmio.getPrice(), hcmio.getQty()));
            hcmio.setFee((long) Precision.round((hcmio.getAmt() * 0.001425), 0));

            if (hcmio.getBsType().equals("S")) {// 賣出 目前不考慮
                hcmio.setTax((long) Precision.round((hcmio.getAmt() * 0.003), 0));
                hcmio.setNetAmt((long) Precision.round((hcmio.getAmt() - hcmio.getFee() - hcmio.getTax()), 0));

            } else {// 買入
                hcmio.setNetAmt(-1 * (long) Precision.round(hcmio.getAmt() + hcmio.getFee(), 0));
            }
            //===========================================INSERT TCNUD
            tcnudRepository.create(hcmio.getTradeDate(),
                    hcmio.getBranchNo(), hcmio.getCustSeq(), hcmio.getDocSeq(),
                    hcmio.getStock(), hcmio.getPrice(), hcmio.getQty(),
                    hcmio.getQty(), hcmio.getFee(), Math.abs(hcmio.getNetAmt()),
                    hcmio.getModDate(), hcmio.getModTime(), hcmio.getModUser());

            //====================明細金額計算完畢後================================
            double nowPrice = mstmb.getCurPrice();// 撈出現價

            detail.setTradeDate(hcmio.getTradeDate());
            detail.setDocSeq(hcmio.getDocSeq());
            detail.setStock(request.getStock());
            detail.setStockName(mstmb.getStockName());
            detail.setBuyPrice(numberFormat(request.getPrice()));
            detail.setNowPrice(numberFormat(nowPrice)); // 設定現價
            detail.setQty(request.getQty());
            detail.setRemainQty(hcmio.getQty());
            detail.setFee(hcmio.getFee());
            detail.setCost(Math.abs(hcmio.getNetAmt()));
            detail.setMarketValue(calMarketValue(nowPrice, detail.getRemainQty()));
            detail.setUnrealProfit(calUnreal(nowPrice, detail.getCost(), detail.getRemainQty()));
            detail.setProfitability(numberFormat(calProfitability(detail.getUnrealProfit(), detail.getCost())) + "%");

            response.setDetail(List.of(detail));
            response.setResponseCode("000");
            response.setMessage("委託成功");
            hcmioRepository.save(hcmio); //==============存入DB
        } catch (Exception exception) {
            response.setResponseCode("005");
            response.setMessage("Service Error");
            System.out.println(Arrays.toString(exception.getStackTrace()));
        }
        return response;
    }

    public SettlementAmountResponse getSettlementAmount(UnrealProfitRequest request) throws ParseException {

        SettlementAmountResponse response = new SettlementAmountResponse();
        CalendarUtils cu = new CalendarUtils();
        cu.init(cu);// 設定今年度國定假日(平日)
        Calendar today = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");// formatter 定義格式

        //若今天查詢日期是假日
        if (cu.isHoliday(today)) {
            response.setResponseCode("003");
            response.setMessage("非交易時間");
            return response;
        }

        //檢查request格式
        if (null == request.getBranchNo() || request.getBranchNo().isEmpty() || request.getBranchNo().isBlank() || null == request.getCustSeq() || request.getCustSeq().isBlank() || request.getCustSeq().isEmpty()) {
            response.setResponseCode("002");
            response.setMessage("格式錯誤");
        }

        // 計算交易日
        int T = 0; //
        while (T < 2) {
            today.add(Calendar.DATE, -1);
            if (!cu.isHoliday(today)) {// 判定是否為國定假日(平日)，六日
                T++;
            }
        }
//        if (null == tcnudRepository.getSumCost(request.getBranchNo().toUpperCase(), request.getCustSeq(), dateFormat.format(today.getTime()))) { // 資料庫運算
//            response.setResponseCode("001");
//            response.setMessage("查無符合資料");
//            return response;
//        }
        if (null == tcnudRepository.findByBCT(request.getBranchNo().toUpperCase(),request.getCustSeq(),dateFormat.format(today.getTime()))) { //內部運算
            response.setResponseCode("001");
            response.setMessage("查無符合資料");
            return response;
        }
//        response.setSettlementAmount(tcnudRepository.getSumCost(request.getBranchNo().toUpperCase(), request.getCustSeq(), dateFormat.format(today.getTime())));
        response.setSettlementAmount(calSumCost(request,dateFormat.format(today.getTime())));
        response.setTcnudList(tcnudRepository.findByBCT(request.getBranchNo().toUpperCase(), request.getCustSeq(), dateFormat.format(today.getTime())));
        response.setResponseCode("000");
        response.setMessage("Success");
        return response;
    }

    private String checkCreateRequest(CreateHcmioRequest request) {
        StringBuilder message = new StringBuilder();
        request.setBranchNo(request.getBranchNo().toUpperCase());
        if (null == request.getPrice() || null == request.getBranchNo() || null == request.getCustSeq() || null == request.getQty() || null == request.getStock() || request.getStock().isEmpty() || request.getStock().isBlank() || request.getCustSeq().isEmpty() || request.getCustSeq().isBlank() || request.getBranchNo().isBlank() || request.getBranchNo().isEmpty()) {
            message.append("資料不完整 ");
            return message.toString();
        }
        if (null == mstmbRepository.findByStock(request.getStock())) {
            message.append("沒有這支股票 ");
        }
        if (request.getQty() < 1 && null != request.getQty()) {
            message.append("股票買賣數量必須大於等於 1 ");
        }
        if (!request.getBsType().equals("B") && !request.getBsType().equals("S")) {
            message.append("買賣別必須為 'S' , 'B' ");
        }
        if (!request.getPrice().isNaN() && null != request.getPrice() && request.getPrice() < 10.0) { // min price = 10
            message.append("價格必須為正數且>10 ");
        }
        return message.toString();
    }

    // 自動生成委託書號
    private String getNewDocSeq(String date) {//流水單號

        String lastDocSeq = hcmioRepository.getLastDocSeq(date);

        String lastDocSeqEng;
        int lastDocSeqInt;

        if (null == lastDocSeq) {
            lastDocSeqEng = "AA";
            lastDocSeqInt = 0;
        } else {
            lastDocSeqEng = lastDocSeq.substring(0, 2);//取英文  beginIndex=0 endIndex=2 --> 0~1
            lastDocSeqInt = Integer.parseInt(lastDocSeq.substring(2, 5));//取數字  2~4
        }

        List<Integer> engToAscii = lastDocSeqEng.chars().boxed().collect(Collectors.toList());//英文轉ascii
        //數字+1
        lastDocSeqInt++;
        //進位處理
        if (lastDocSeqInt > 999) {//如果超過999則歸1且英文進位
            lastDocSeqInt = 1;//歸1
            engToAscii.set(1, engToAscii.get(1) + 1);//英文進位
            if (engToAscii.get(1) > 90) {//如果超過Z
                engToAscii.set(1, 65);//歸A
                engToAscii.set(0, engToAscii.get(0) + 1);//進位
                if (engToAscii.get(0) > 90 && engToAscii.get(0) < 97) {//如果超過Z
                    engToAscii.set(0, 97);//跳a,aA001~zZ999
                }
            }
        }
        //數值轉字串之檢查
        String newDocSeqInt = Integer.toString(lastDocSeqInt);//數值轉字串
        StringBuilder newDocSeqEng = new StringBuilder();
        for (int ascii : engToAscii) {
            newDocSeqEng.append(Character.toString(ascii));//list英文ascii轉字串
        }

        String newDocSeq = "";
        if (lastDocSeqInt < 10) {//如果數值為個位數
            newDocSeq = newDocSeqEng + "00" + newDocSeqInt;//前面補00
        } else if (lastDocSeqInt < 100) {//如果數值為十位數
            newDocSeq = newDocSeqEng + "0" + newDocSeqInt;//前面補0
        } else {
            newDocSeq = newDocSeqEng + newDocSeqInt;
        }
        return newDocSeq;
    }

    private List<String> getStockList(UnrealProfitRequest request) {//不管有沒有給 stock 都回傳相關值
        List<String> stockList = tcnudRepository.getStockList(request.getBranchNo(), request.getCustSeq());
        if (stockList.isEmpty()) {
            return null;
        }
        if (null != request.getStock() && !request.getStock().isEmpty() && !request.getStock().isBlank()) {
            stockList = new ArrayList<>();
            stockList.add(request.getStock());
        }
        return stockList;
    }

    private String checkUnrealProfitRequest(UnrealProfitRequest request) {

        StringBuilder checkResult = new StringBuilder();
        if (null == request.getBranchNo() || request.getBranchNo().isBlank() || request.getBranchNo().isEmpty()) {
            checkResult.append("002BranchNo is null,empty or blank ");
        } else {
            request.setBranchNo(request.getBranchNo().toUpperCase());
        }
        if (null == request.getCustSeq() || request.getCustSeq().isEmpty() || request.getCustSeq().isBlank()) {
            checkResult.append("002CustSeq is null,empty or blank ");
        } else if (null != request.getMaxLimit() && 0 == request.getMaxLimit()) {
            checkResult.append("002max can not be 0 ");
        } else if (null != request.getMinLimit() && 0 == request.getMinLimit()) {
            checkResult.append("002min can not be 0 ");
        } else if (null != request.getStock() && !request.getStock().isEmpty() && !request.getStock().isBlank()) {
            if (null == mstmbRepository.findByStock(request.getStock())) {
                checkResult.append("001stock does not exist");
            }
        } else if (tcnudRepository.getStockList(request.getBranchNo(), request.getCustSeq()).isEmpty()) {
            checkResult.append("001查無資料");
        }
        return checkResult.toString();
    }

    private List<Detail> getDetailList(UnrealProfitRequest request) {
        List<TCNUD> tcnudList = tcnudRepository.findByBCS(request.getBranchNo(), request.getCustSeq(), request.getStock());
        List<Detail> detailRespons = new ArrayList<>();
        MSTMB mstmb = mstmbRepository.findByStock(request.getStock());
        String stockName = mstmb.getStockName();
        double nowPrice = mstmb.getCurPrice();
        for (TCNUD tcnud : tcnudList) {
            detailRespons.add(new Detail(
                    tcnud.getTradeDate(),
                    tcnud.getDocSeq(),
                    tcnud.getStock(),
                    stockName,
                    numberFormat(tcnud.getPrice()),
                    numberFormat(nowPrice),
                    tcnud.getQty(),
                    tcnud.getRemainQty(),
                    tcnud.getFee(),
                    tcnud.getCost(),
                    calMarketValue(nowPrice, tcnud.getRemainQty()),
                    calUnreal(nowPrice, tcnud.getCost(), tcnud.getQty()),
                    numberFormat(calProfitability(calUnreal(nowPrice, tcnud.getCost(), tcnud.getQty()), tcnud.getCost())) + "%"
            ));
        }
        if (null == request.getMaxLimit() && null == request.getMinLimit()) {
            return detailRespons;// 未實現損益明細-All
        }
        List<Detail> filterList = new ArrayList<>();
        for (Detail response : detailRespons) {
            double range = Double.parseDouble(response.getProfitability().substring(0, response.getProfitability().length() - 1));
            if (null != request.getMaxLimit() && null == request.getMinLimit()) {// 只有max
                if (range < request.getMaxLimit()) {
                    filterList.add(response);
                }
            } else if (null != request.getMinLimit() && null == request.getMaxLimit()) {// 只有min
                if (range > request.getMinLimit()) {
                    filterList.add(response);
                }
            } else if (null != request.getMaxLimit() && null != request.getMinLimit()) {// 兩個都有
                if (range > request.getMinLimit() && range < request.getMaxLimit()) {
                    filterList.add(response);
                }
            }
        }
        return filterList;// 未實現損益明細-過濾後
    }

    private Long calUnreal(double nowPrice, Long cost, Long qty) {
        return (long) Precision.round((nowPrice * qty) - cost - calFee(calAmt(nowPrice, qty)) - calTax(calAmt(nowPrice, qty)), 0);
    }

    private Long calMarketValue(double nowPrice, Long qty) {
        return (calAmt(nowPrice, qty) - calFee(calAmt(nowPrice, qty)) - calTax(calAmt(nowPrice, qty)));
    }

    private Long calAmt(Double price, Long qty) {
        return (long) Precision.round(price * qty, 0);
    }

    private Long calFee(Long amt) {
        return (long) Precision.round(amt * 0.001425, 0);
    }

    private Long calTax(Long amt) {
        return (long) Precision.round(amt * 0.003, 0);
    }

    private String numberFormat(double value) {
        return String.format("%.2f", value);
    }

    private double calProfitability(Long unrealProfit, Long cost) {
        return Precision.round((unrealProfit.doubleValue() / cost.doubleValue()) * 100, 2);
    }

    private Long calSumCost(UnrealProfitRequest request,String  date){
        List<TCNUD> tcnudList= tcnudRepository.findByBCT(request.getBranchNo().toUpperCase(),request.getCustSeq(),date);
        Long sum=0L;
        for (TCNUD tcnud:tcnudList){
            sum+=tcnud.getCost();
        }
        return sum;
    }
}
