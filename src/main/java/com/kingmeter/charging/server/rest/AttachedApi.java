package com.kingmeter.charging.server.rest;

import com.kingmeter.charging.server.rest.dto.IpAndPortDto;
import com.kingmeter.charging.server.rest.dto.SiteMapDto;
import com.kingmeter.charging.socket.rest.*;
import com.kingmeter.dto.charging.v2.rest.request.ClearMalfunctionRequestRestDto;
import com.kingmeter.dto.charging.v2.rest.request.SiteSettingRequestRestDto;
import com.kingmeter.dto.charging.v2.rest.response.*;
import com.kingmeter.socket.framework.util.CacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@RequestMapping("/attach")
@RestController
public class AttachedApi {

    @Autowired
    private ChargingSocketApplication chargingSocketApplication;


    @PutMapping(value = "/changeIpAndPort")
    public void changeIpAndPort(@RequestBody IpAndPortDto restDto) {
        chargingSocketApplication.changeIpAndPort(restDto.getSiteId(),
                4,restDto.getPassword(),
                restDto.getUrl(),restDto.getSocketPort(),
                restDto.getCompanyCode(),restDto.getTimezone());
    }

    @PutMapping("/setting")
    public SiteSettingResponseRestDto siteSetting(@RequestBody SiteSettingRequestRestDto requestDto) {
        return chargingSocketApplication.siteSetting(requestDto.getSiteId(),
                requestDto.getDataInterval(), requestDto.getEmptyInterval(),
                requestDto.getRepeatTime(), requestDto.getBeep(),
                requestDto.getMonitor());
    }


    @PutMapping("/clearMalfunction")
    public MalfunctionClearResponseRestDto clearMalfunction(
            @RequestBody ClearMalfunctionRequestRestDto requestDto
    ) {
        return chargingSocketApplication.clearMalfunction(requestDto.getSiteId(),
                requestDto.getDockId(), requestDto.getError());
    }

    @GetMapping("/connection")
    public List<ConnectionDto> queryConnection() {
        return chargingSocketApplication.queryConnection();
    }

    @GetMapping("/count")
    public long count(){
        return CacheUtil.getInstance().getDeviceIdAndChannelMap().size();
    }

    @GetMapping("/{siteId}")
    public Map<String, String> getDeviceMap(@PathVariable("siteId") long siteId){
        Map<String, String> siteMap = CacheUtil.getInstance().getDeviceInfoMap().get(siteId);
        return siteMap;
    }

    @PutMapping("/all")
    public String setDeviceMap(@RequestBody SiteMapDto siteMapDto){
        long siteId = siteMapDto.getSiteId();
        ConcurrentMap<Long,Map<String, String>> deviceMap = CacheUtil.getInstance().getDeviceInfoMap();
        Map<String, String> siteMap = deviceMap.get(siteId);
        siteMap.put("ast",String.valueOf(siteMapDto.getAst()));
        siteMap.put("acm",String.valueOf(siteMapDto.getAcm()));
        siteMap.put("minbsoc",String.valueOf(siteMapDto.getMinbsoc()));
        siteMap.put("uid",siteMapDto.getUid());
        deviceMap.put(siteId,siteMap);
        CacheUtil.getInstance().setDeviceInfoMap(deviceMap);
        return "okay";
    }

}
