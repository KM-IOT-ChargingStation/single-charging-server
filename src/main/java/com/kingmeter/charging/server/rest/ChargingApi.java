package com.kingmeter.charging.server.rest;

import com.kingmeter.charging.socket.rest.ChargingSocketApplication;
import com.kingmeter.dto.charging.v2.rest.request.ForceUnlockRequestRestDto;
import com.kingmeter.dto.charging.v2.rest.request.ScanUnlockRequestRestDto;
import com.kingmeter.dto.charging.v2.rest.response.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/site")
@Slf4j
@RestController
public class ChargingApi {

    @Autowired
    private ChargingSocketApplication chargingSocketApplication;

    @PutMapping(value = "/unLock")
    public ScanUnlockResponseRestDto scanUnLock(@RequestBody ScanUnlockRequestRestDto requestDto) {
        return chargingSocketApplication.scanUnlock(requestDto);
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
