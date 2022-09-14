package com.example.API.test.controller.dto.response;

import com.example.API.test.model.entity.TCNUD;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SettlementAmountResponse {
    private Long SettlementAmount;
    private List<TCNUD> tcnudList;
    private String responseCode;
    private String message;
}
