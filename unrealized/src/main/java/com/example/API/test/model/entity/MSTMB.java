package com.example.API.test.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="mstmb")
public class MSTMB {
    @Id
    private String Stock;
    @Column
    private String StockName;
    @Column
    private Character MarketType;//市場別 T-上市 O-上櫃 R-興櫃
    @Column
    private double CurPrice;//當前價格
    @Column
    private double RefPrice;//建議價格
    @Column
    private String Currency;//幣別
    @Column
    private String ModDate;
    @Column
    private String ModTime;
    @Column
    private String ModUser="Echo";


}
