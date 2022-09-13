package com.example.API.test.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponse { //hcmio
    private List<Detail> detail;
    private String responseCode;
    private String message;
}
