package com.dgspos.printagent.dto;

import lombok.Data;

@Data
public class Item {
    private String name;

    private int quantity;

    private double price;

    private double total;

    private double saving;
}
