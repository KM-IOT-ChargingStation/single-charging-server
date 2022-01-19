package com.kingmeter.charging.server.rest.dto;


import lombok.Data;

@Data
public class SiteMapDto {
    private long siteId;
    private int ast;
    private int acm;
    private int minbsoc;
    private String uid;
}
