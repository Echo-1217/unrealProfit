package com.example.API.test.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tcnud")
@IdClass(TCNUD.TCNUD_ID.class)
public class TCNUD implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column
    private String TradeDate;
    @Id
    @Column
    private String BranchNo; //分行號碼
    @Id
    @Column
    private String CustSeq;// 客戶代號
    @Id
    @Column
    private String DocSeq;// 委託書號
    @Column
    private String Stock; //股票代號
    @Column
    private Double Price;
    @Column
    private Long Qty;
    @Column
    private Long RemainQty;
    @Column
    private Long Fee;
    @Column
    private Long Cost;
    @Column
    private String ModDate;
    @Column
    private String ModTime;
    @Column
    private String ModUser;
    @Getter
    @Setter
    public static class TCNUD_ID implements Serializable {
        private static final long serialVersionUID = 1L;

        private String TradeDate;
        private String BranchNo; //分行號碼
        private String CustSeq;// 客戶代號
        private String DocSeq;// 委託書號
    }}
