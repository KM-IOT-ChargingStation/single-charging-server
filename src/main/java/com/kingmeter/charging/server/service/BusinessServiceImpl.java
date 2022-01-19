package com.kingmeter.charging.server.service;

import com.alibaba.fastjson.JSONObject;
import com.kingmeter.charging.socket.acl.BusinessService;
import com.kingmeter.charging.socket.business.tracker.DockInfo4OTADto;
import com.kingmeter.common.ResponseData;
import com.kingmeter.dto.charging.v2.rest.business.*;
import com.kingmeter.dto.charging.v2.rest.business.vo.DockStateInfoFromDockDataUploadBSVO;
import com.kingmeter.dto.charging.v2.rest.business.vo.DockStateInfoFromHeartBeatDataUploadBSVO;
import com.kingmeter.dto.charging.v2.socket.in.*;
import com.kingmeter.dto.charging.v2.socket.in.vo.DockStateInfoFromDockDataUploadVO;
import com.kingmeter.dto.charging.v2.socket.in.vo.DockStateInfoFromHeartBeatVO;
import com.kingmeter.dto.charging.v2.socket.out.BikeInDockResponseDto;
import com.kingmeter.dto.charging.v2.socket.out.LoginPermissionDto;
import com.kingmeter.dto.charging.v2.socket.out.LoginResponseDto;
import com.kingmeter.dto.charging.v2.socket.out.SwingCardUnLockResponseDto;
import com.kingmeter.socket.framework.util.CacheUtil;
import com.kingmeter.utils.HardWareUtils;
import com.kingmeter.utils.KMHttpClient;
import com.kingmeter.utils.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@Service
public class BusinessServiceImpl implements BusinessService {


    @Value("${kingmeter.default.companyCode}")
    private String defaultCompanyCode;

    @Value("${kingmeter.default.timezone}")
    private int defaultTimezone;

    @Autowired
    private KMHttpClient httpClient;


    @Override
    public LoginPermissionDto getLoginPermission(SiteLoginRequestDto requestDto) {

        long siteId = requestDto.getSid();
        String msv = requestDto.getMsv();
        String mhv = requestDto.getMhv();
        String password = requestDto.getPwd();

        Map<String, Object> params = new HashMap<>();
        params.put("siteId", siteId);
        params.put("msv", msv);
        params.put("mhv", mhv);

        ResponseData responseData =
                httpClient.get(
                        params,
                        BusinessApiMappingPrefix.queryLoginPermission + siteId,
                        siteId);
        if (responseData.getCode() == 1000) {
            try {
                LoginPermissionBSDto result =
                        JSONObject.parseObject(responseData.getData().toString(),
                                LoginPermissionBSDto.class);

                int sls = result.getSls();
                String newPassword = result.getPassword();
                String newUrl = result.getSocketDomain();
                int newPort = result.getSocketPort();
                int timezone = result.getTimezone();
                String companyCode = result.getCompanyCode();

                byte[] passwordArray = newPassword.getBytes();
                String passwordMd5 = MD5Util.MD5Encode(passwordArray);

                if (sls == 0) {
                    if (!password.equals(passwordMd5)) {
                        return null;
                    }
                    newUrl = "";
                    newPort = 0;
                }
                LoginResponseDto response = new LoginResponseDto(sls, newPassword, newUrl, newPort,
                        -1, -1, companyCode, HardWareUtils.getInstance()
                        .getUtcTimeStampOnDevice(timezone));
                return new LoginPermissionDto(response, companyCode, timezone);
            } catch (Exception e) {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public BikeInDockResponseDto createBikeInDockResponseDto(BikeInDockRequestDto requestDto) {
        long siteId = requestDto.getSid();

        BikeInDockBSDto rest = new BikeInDockBSDto();
        rest.setSiteId(siteId);
        rest.setDockId(requestDto.getKid());
        rest.setBikeId(requestDto.getBid());
        rest.setCurrentTime(
                HardWareUtils.getInstance().
                        getLocalTimeStampByHardWareUtcTimeStamp(
                                getSiteTimeZone(siteId),
                                requestDto.getTim())
        );

        ResponseData responseData =
                httpClient.post(
                        JSONObject.toJSONString(rest),
                        BusinessApiMappingPrefix.bikeInDock,
                        siteId);
        if (responseData.getCode() == 1000) {
            BikeInDockResponseBSDto result = JSONObject.parseObject(responseData.getData().toString(),
                    BikeInDockResponseBSDto.class);
            return new BikeInDockResponseDto(requestDto.getKid(),
                    result.getRet(), result.getAcm(),
                    result.getExpense(), result.getRtm());
        }else{
            return new BikeInDockResponseDto(requestDto.getKid(), 0, 0, 0, 0);
        }
    }

    @Override
    public void forceUnlockNotify(ForceUnLockRequestDto forceUnLockRequestDto) {

    }

    @Override
    public void heartBeatNotify(SiteHeartRequestDto requestDto) {
        long siteId = requestDto.getSid();

        SiteHeartDataUploadBSDto rest = new SiteHeartDataUploadBSDto();
        rest.setSiteId(siteId);
        rest.setRpow(requestDto.getRpow());

        DockStateInfoFromHeartBeatDataUploadBSVO[] dockArray =
                new DockStateInfoFromHeartBeatDataUploadBSVO[
                        requestDto.getState().length];

        for (int i = 0; i < requestDto.getState().length; i++) {
            DockStateInfoFromHeartBeatVO vo = requestDto.getState()[i];
            dockArray[i] = new DockStateInfoFromHeartBeatDataUploadBSVO(
                    vo.getKid(), vo.getKln(), vo.getBid(), vo.getBsoc()
            );
        }
        rest.setDockArray(dockArray);

        httpClient.post(
                        JSONObject.toJSONString(rest),
                        BusinessApiMappingPrefix.allDockInfoUpload,
                        siteId);
    }

    @Override
    public void malfunctionUploadNotify(DockMalfunctionUploadRequestDto requestDto) {
        long siteId = requestDto.getSid();

        MalfunctionDataUploadBSDto rest =
                new MalfunctionDataUploadBSDto();
        rest.setSiteId(siteId);
        rest.setDockId(requestDto.getKid());
        rest.setDockKln(requestDto.getKln());
        rest.setBikeId(requestDto.getBid());
        rest.setCer(requestDto.getCer());
        rest.setBer(requestDto.getBer());
        rest.setDisp(requestDto.getDisp());
        rest.setPer(requestDto.getPer());
        rest.setPerlk(requestDto.getPerlk());
        rest.setPerws(requestDto.getPerws());

        httpClient.post(
                        JSONObject.toJSONString(rest),
                        BusinessApiMappingPrefix.malfunctionDataUpload,
                        siteId);
    }

    @Override
    public void malfunctionClearNotify(MalfunctionClearRequestDto malfunctionClearRequestDto) {

    }

    @Override
    public void dockBikeInfoNotify(QueryDockBikeInfoRequestDto queryDockBikeInfoRequestDto) {

    }

    @Override
    public void dealWithQueryDockInfo(QueryDockInfoRequestDto queryDockInfoRequestDto) {

    }

    @Override
    public void dealWithScanUnLock(ScanUnLockRequestDto requestDto) {

    }

    @Override
    public SwingCardUnLockResponseDto dealWithSwingCardUnlock(SwingCardUnLockRequestDto requestDto) {
        long siteId = requestDto.getSid();
        Map<String, String> siteMap = CacheUtil.getInstance().getDeviceInfoMap().get(siteId);
        int timezone = Integer.parseInt(siteMap.getOrDefault("timezone", defaultCompanyCode));

        SwingCardUnLockResponseDto responseDto =
                new SwingCardUnLockResponseDto(requestDto.getKid(),
                        0, 0, 10, "",
                        HardWareUtils.getInstance().getUtcTimeStampOnDevice(timezone));


        Map<String, Object> params = new HashMap<>();
        params.put("siteId", siteId);
        params.put("dockId", requestDto.getKid());
        params.put("bikeId", requestDto.getBid());
        params.put("card", requestDto.getCard());
        params.put("currentTime", HardWareUtils.getInstance().
                getLocalTimeStampByHardWareUtcTimeStamp(
                        getSiteTimeZone(siteId),
                        requestDto.getTim()));

        ResponseData responseData =
                httpClient.get(
                        params,
                        BusinessApiMappingPrefix.swipeCardPermission + requestDto.getCard(),
                        siteId);
        if (responseData.getCode() == 1000) {

            log.info("swipe card permission response data is [{}]",
                    JSONObject.toJSONString(responseData.getData()));

            SwipeCardPermissionBSDto result =
                    JSONObject.parseObject(responseData.getData().toString(),
                            SwipeCardPermissionBSDto.class);


            //ast 0 ,7 用户取车超时,11 获取用户信息出错,21 此租赁卡冻结 ,23 没有找到车辆信息
            int ast = result.getAst();//账户状态
            int acm = result.getAcm();//账户余额
            int minbsoc = result.getMinbsoc();//助力车电池最低电量
            String userId = result.getUserId();//租车用户id

            if (ast == 0 && requestDto.getBid() == 0) {
                ast = 23;
            }
            responseDto.setAst(ast);
            responseDto.setAcm(acm);
            responseDto.setMinbsoc(minbsoc);
            responseDto.setUid(userId);
        } else {
            responseDto.setAst(11);
        }
        return responseDto;
    }

    @Override
    public void dealWithSwingCardConfirm(SwingCardUnLockRequestConfirmDto requestDto) {
        long siteId = requestDto.getSid();

        SwipeCardConfirmBSDto rest = new SwipeCardConfirmBSDto();
        rest.setSiteId(siteId);
        rest.setDockId(requestDto.getKid());
        rest.setBikeId(requestDto.getBid());
        rest.setCard(requestDto.getCard());
        rest.setSls(requestDto.getGbs());
        rest.setUserId(requestDto.getUid());
        rest.setCurrentTime(HardWareUtils.getInstance().
                getLocalTimeStampByHardWareUtcTimeStamp(
                        getSiteTimeZone(siteId),
                        requestDto.getTim()));

        httpClient.post(
                        JSONObject.toJSONString(rest),
                        BusinessApiMappingPrefix.swipeCardConfirm,
                        siteId);
    }

    @Override
    public void queryDockLockStatusNotify(QueryDockLockStatusRequestDto queryDockLockStatusRequestDto) {

    }

    @Override
    public void siteSettingNotify(SiteSettingRequestDto siteSettingRequestDto) {

    }

    @Override
    public void offlineNotify(Long siteId) {
        Map<String, Object> params = new HashMap<>();
        params.put("siteId", siteId);

        httpClient.delete(
                        BusinessApiMappingPrefix.offLine + siteId,
                        siteId);
    }

    @Override
    public void dealWithDockMonitorDataUpload(DockDataUploadRequestDto requestDto) {
        long siteId = requestDto.getSid();
        DockDataUploadBSDto restDto = new DockDataUploadBSDto();
        restDto.setSiteId(siteId);

        DockStateInfoFromDockDataUploadBSVO[] dockArray =
                new DockStateInfoFromDockDataUploadBSVO[requestDto.getState().length];

        for (int i = 0; i < requestDto.getState().length; i++) {
            DockStateInfoFromDockDataUploadVO vo =
                    requestDto.getState()[i];
            dockArray[i] = new DockStateInfoFromDockDataUploadBSVO(
                    vo.getKid(), vo.getKln(), vo.getBid(),
                    Float.valueOf(vo.getChgv())/10,
                    Float.valueOf(vo.getChgi())/10,
                    vo.getBsoc(), vo.getBrang(),
                    Float.valueOf(vo.getIpow())/10,
                    changeTemperature(vo.getItemp())
            );
        }
        restDto.setDockArray(dockArray);

        httpClient.post(
                        JSONObject.toJSONString(restDto),
                        BusinessApiMappingPrefix.allDockMonitorData,
                        siteId);
    }

    @Override
    public void otaResponseNotify(OTARequestDto otaRequestDto) {

    }


    @Override
    public void otaTrackerRecordUpload(Long siteId,boolean flag, Map<String, DockInfo4OTADto> dockIdAndBikeIdMap) {
        Map<String,Object> result = new HashMap<>();
        result.put("siteId",siteId);
        result.put("flag",flag);
        result.put("dockIdAndBikeIdMap",dockIdAndBikeIdMap);
        httpClient.post(
                JSONObject.toJSONString(result),
                BusinessApiMappingPrefix.otaTrackerData,
                siteId);
    }

    private int getSiteTimeZone(long siteId) {
        Map<String, String> siteMap = CacheUtil.getInstance().getDeviceInfoMap().get(siteId);
        return Integer.parseInt(siteMap.getOrDefault("timezone", String.valueOf(defaultTimezone)));
    }

    private float changeTemperature(int tmp) {
        if (tmp > 4096) {
            tmp = 4096 - tmp;
        }
        return Float.valueOf(tmp) / 10;
    }
}
