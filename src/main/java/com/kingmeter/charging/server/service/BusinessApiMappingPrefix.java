package com.kingmeter.charging.server.service;

public interface BusinessApiMappingPrefix {
    String queryLoginPermission = "/loginPermission/site/";
    String malfunctionDataUpload = "/malfunction";
    String bikeInDock = "/bikeInDock";
    String offLine = "/offline/site/";
    String swipeCardPermission = "/swipeCard/permission/";
    String swipeCardConfirm = "/swipeCard/confirm";
    String allDockInfoUpload = "/allDockInfo";
    String allDockMonitorData = "/allDockMonitorData";
    String otaTrackerData = "/otaTrackerData";
}
