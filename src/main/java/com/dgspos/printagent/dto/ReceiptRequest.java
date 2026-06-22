package com.dgspos.printagent.dto;

import lombok.Data;

import java.util.List;

@Data
public class ReceiptRequest {
    private String invoiceNumber;

    private Company company;

    private List<Item> items;

    private double subtotal;
    private double discount;
    private double total;

    private String barcodeBase64;

    private String paymentMethod;
    private double paidAmount;
    private double changeAmount;
}
