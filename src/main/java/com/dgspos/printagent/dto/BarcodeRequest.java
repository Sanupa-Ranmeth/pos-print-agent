package com.dgspos.printagent.dto;

import lombok.Data;

@Data
public class BarcodeRequest {
    private String sku;
    private String productName;
    private int quantity;

    private String barcodeBase64;
}
