package com.kingmeter.charging.server.rest;

import com.alibaba.fastjson.JSON;
import com.kingmeter.charging.socket.rest.ChargingSocketApplication;
import com.kingmeter.dto.charging.v2.rest.request.*;
import com.kingmeter.dto.charging.v2.rest.response.*;
import com.kingmeter.socket.framework.application.SocketApplication;
import com.kingmeter.socket.framework.util.CacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RequestMapping("/site")
@Slf4j
@RestController
public class ChargingApi {

    @Autowired
    private ChargingSocketApplication chargingSocketApplication;

    @Autowired
    private SocketApplication socketApplication;


    @PutMapping(value = "/unLock")
    public ScanUnlockResponseRestDto scanUnLock(@RequestBody ScanUnlockRequestRestDto requestDto) {
        String key = "scan_" + requestDto.getUserId();
        CacheUtil.getInstance().getDeviceResultMap().remove(key);

        chargingSocketApplication.scanUnlock(requestDto);

        //4,wait for lock response
        Map<String, String> result = socketApplication.waitForMapResult(key);
        return JSON.parseObject(result.get("ScanUnlock"), ScanUnlockResponseRestDto.class);
    }

    @GetMapping("/queryFirmwareVersion")
    public QueryDockInfoResponseRestDto sendQueryAllDockInfoCommand(
            @RequestParam long siteId,
            @RequestParam long dockId) {
        return chargingSocketApplication.dealWithQueryDockInfo(siteId, dockId);
    }



    //2,强制开锁
    @PutMapping("/forceUnLock")
    public ForceUnLockResponseRestDto forceUnLock(@RequestBody ForceUnlockRequestRestDto requestDto) {
        return chargingSocketApplication.foreUnlock(requestDto.getSiteId(),
                requestDto.getDockId(), requestDto.getUserId());
    }

    /**
     * 锁检测
     */
    @GetMapping("/checkDockLock")
    public QueryDockLockStatusResponseRestDto queryDockLockStatus(
            @RequestParam long siteId,
            @RequestParam long dockId,
            @RequestParam String userId) {
        return chargingSocketApplication.queryDockLockStatus(siteId, dockId, userId);
    }



    @GetMapping("/queryDockAndBikeInfo")
    public QueryDockBikeInfoRequestRestDto queryDockAndBikeInfo(
            @RequestParam("dockId") long dockId,
            @RequestParam("siteId") long siteId) {
        return chargingSocketApplication.queryDockAndBikeInfo(siteId, dockId);
    }



}
