package com.dgspos.printagent.dto;

import lombok.Data;

@Data
public class Company {

    private String name;
    private String phone;
    private String address;

    private String footerText;

    private String logoBase64;
}
