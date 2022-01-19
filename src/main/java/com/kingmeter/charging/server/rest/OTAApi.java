package com.kingmeter.charging.server.rest;


import com.kingmeter.charging.socket.rest.ChargingSocketApplication;
import com.kingmeter.dto.charging.v2.rest.request.OTARequestRestDto;
import com.kingmeter.dto.charging.v2.rest.response.OTAResponseRestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/ota")
@RestController
public class OTAApi {

    @Autowired
    private ChargingSocketApplication chargingSocketApplication;


    @PutMapping("/firmware")
    public OTAResponseRestDto upgradeMultipleDevice(@RequestBody OTARequestRestDto restDto) {

       return chargingSocketApplication.upgradeMultipleDevice(restDto);
    }

}
