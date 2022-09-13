package com.example.API.test.controller.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UnrealProfitRequest {
    String branchNo;
    String custSeq;
    String stock;
    Double minLimit;
    Double maxLimit;
}
