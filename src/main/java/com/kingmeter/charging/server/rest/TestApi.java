package com.kingmeter.charging.server.rest;


import com.kingmeter.charging.socket.test.ChargingSocketTestApplication;
import com.kingmeter.charging.socket.test.dto.TestQueryDockBikeInfoDto;
import com.kingmeter.charging.socket.test.dto.TestQueryDockInfoDto;
import com.kingmeter.charging.socket.test.dto.TestUnLockDto;
import com.kingmeter.dto.charging.v2.rest.request.OTARequestRestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RequestMapping("/test")
@Slf4j
@RestController
public class TestApi {

    @Autowired
    private ChargingSocketTestApplication chargingSocketTestApplication;


    @DeleteMapping("/{siteId}")
    public void stopUnlock(@PathVariable("siteId") long siteId) {
        chargingSocketTestApplication.stopUnlock(siteId);
    }


    @GetMapping("/batchUnlock/{siteId}")
    public String batchUnlock(@PathVariable(name = "siteId") long siteId,
                              @RequestParam(name = "times") int times,
                              @RequestParam(name = "perSite") long perSite,
                              @RequestParam(name = "perDock") long perDock) {
        return chargingSocketTestApplication.batchUnlock(siteId, times, perSite, perDock);
    }


    @DeleteMapping("/checkDockLock/{siteId}")
    public void stopCheckDockLock(@PathVariable("siteId") long siteId) {
        chargingSocketTestApplication.stopCheckDockLock(siteId);
    }


    @GetMapping("/batchCheckDockLock/{siteId}")
    public String batchCheckDockLock(@PathVariable(name = "siteId") long siteId,
                                     @RequestParam(name = "times") int times,
                                     @RequestParam(name = "perSite") long perSite,
                                     @RequestParam(name = "perDock") long perDock) {
        return chargingSocketTestApplication.batchCheckDockLock(siteId, times, perSite, perDock);
    }


    @PostMapping("/testOTA")
    public void testOTA(@RequestBody OTARequestRestDto restDto) {
        chargingSocketTestApplication.testOTA(restDto);
    }

    @DeleteMapping("/deleteOTAFLag")
    public void deleteOTAFLag(@RequestParam("siteId") long siteId) {
        chargingSocketTestApplication.deleteOTAFLag(siteId);
    }

    @PostMapping("/testScanUnLock")
    public void testScanUnLock(@RequestBody TestUnLockDto testUnLockDto) {
        chargingSocketTestApplication.testScanUnLock(testUnLockDto);
    }

    @DeleteMapping("/deleteScanUnLockFlag")
    public void deleteScanUnLockFlag(@RequestParam("siteId") long siteId) {
        chargingSocketTestApplication.deleteScanUnLockFlag(siteId);
    }

    @PostMapping("/testForceUnLock")
    public void testForceUnLock(@RequestBody TestUnLockDto testUnLockDto) {
        chargingSocketTestApplication.testForceUnLock(testUnLockDto);
    }

    @DeleteMapping("/deleteForceUnLockFlag")
    public void deleteForceUnLockFlag(@RequestParam("siteId") long siteId) {
        chargingSocketTestApplication.deleteForceUnLockFlag(siteId);
    }

    @PostMapping("/testQueryDockInfo")
    public void testQueryDockInfo(@RequestBody TestQueryDockInfoDto dockInfoDto) {
        chargingSocketTestApplication.testQueryDockInfo(dockInfoDto);
    }

    @DeleteMapping("/deleteQueryDockInfoFlag")
    public void deleteQueryDockInfoFlag(@RequestParam("siteId") long siteId) {
        chargingSocketTestApplication.deleteQueryDockInfoFlag(siteId);
    }

    @PostMapping("/testQueryDockBikeInfo")
    public void testQueryDockBikeInfo(@RequestBody TestQueryDockBikeInfoDto dockBikeInfoDto) {
        chargingSocketTestApplication.testQueryDockBikeInfo(dockBikeInfoDto);
    }

    @DeleteMapping("/deleteQueryDockBikeInfoFlag")
    public void deleteQueryDockBikeInfoFlag(@RequestParam("siteId") long siteId) {
        chargingSocketTestApplication.deleteQueryDockBikeInfoFlag(siteId);
    }
}
