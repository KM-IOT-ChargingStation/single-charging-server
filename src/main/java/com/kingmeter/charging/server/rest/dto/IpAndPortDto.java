package com.kingmeter.charging.server.rest.dto;

import lombok.Data;

@Data
public class IpAndPortDto {
    private long siteId;
    private String password;
    private String url;
    private int socketPort;
    private String companyCode;
    private int timezone;
}
