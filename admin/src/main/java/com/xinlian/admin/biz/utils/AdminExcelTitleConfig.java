package com.xinlian.admin.biz.utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class AdminExcelTitleConfig {



    public static final Map<String, String> RELEASE_CAT_RECORD_MAP;

    public static final Map<String,String> WAIT_DISPOSE_CHAIN_OWNER_MAP;

    static {
        RELEASE_CAT_RECORD_MAP = getReleaseCatRecordMap();
        WAIT_DISPOSE_CHAIN_OWNER_MAP = getWaitDisposeChainOwnerMap();
    }

    private static Map<String, String> getReleaseCatRecordMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("姓名","userName");
        map.put("链权人账号","userLoginName");
        map.put("UID号","uid");
        map.put("电话","mobile");
        map.put("释放数量CAT","releaseCatNum");
        return map;
    }

    private static Map<String,String> getWaitDisposeChainOwnerMap(){
        Map<String, String> map = new LinkedHashMap<>();
        map.put("UID","uid");
        map.put("姓名","authName");
        map.put("身份证","authSn");
        map.put("联系方式","mobile");
        return map;
    }

}
