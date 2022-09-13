package com.example.API.test.model.entity;

import lombok.*;
import org.springframework.context.annotation.Primary;

import javax.persistence.*;
import java.io.Serializable;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "hcmio")
@IdClass(HCMIO.HCIMO_ID.class)
public class HCMIO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column
    private String TradeDate;
    @Id
    @Column
    private String BranchNo; //分行號碼
    @Id
    @Column
    private String CustSeq ;// 客戶代號
    @Id
    @Column
    private String DocSeq;// 委託書號
    @Column
    private String Stock; //股票代號
    @Column
    private String BsType; //買賣別
    @Column
    private Double Price;
    @Column
    private Long Qty;
    @Column
    private Long Amt;
    @Column
    private Long Fee;
    @Column
    private Long Tax;
    @Column
    private Long NetAmt;
    @Column
    private String ModDate;
    @Column
    private String ModTime;
    @Column
    private String ModUser="Echo";
    @Getter
    @Setter
    public static class HCIMO_ID implements Serializable {
        private static final long serialVersionUID = 1L;

        private String TradeDate;
        private String BranchNo; //分行號碼
        private String CustSeq;// 客戶代號
        private String DocSeq;// 委託書號
    }

}
