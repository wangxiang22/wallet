package com.xinlian.admin.service;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;

/**
 * com.xinlian.admin.service
 *
 * @author by Song
 * @date 2020/2/17 16:53
 */
public class HomePageControllerTest {

    public static void main(String[] args) {
        String token = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiOlsiMSIsIk1vemlsbGEvNS4wIChNYWNpbnRvc2g7IEludGVsIE1hYyBPUyBYIDEwXzE1XzMpIEFwcGxlV2ViS2l0LzUzNy4zNiAoS0hUTUwsIGxpa2UgR2Vja28pIENocm9tZS84MC4wLjM5ODcuMTYzIFNhZmFyaS81MzcuMzYiXSwiZXhwIjoxNTg2OTA1NzgyfQ._ugs0vWznm10bSWAwugxgqtpl3G5CLZISCfAuMqzKx0";
        String md5Token = DigestUtils.md5Hex(token.substring("Bearer ".length()));
        System.err.println(md5Token);
    }

    @Test
    public void testMain(){

        String clearDayStr = "20200115,20200116,20200117,20200118,20200119,20200120,20200121,20200122,20200123,20200124,20200125,20200126,20200127,20200128,20200129,20200130,20200131,20200201,20200202,20200203,20200204,20200205,20200206,20200207,20200208,20200209,20200210,20200211,20200212,20200213,20200214,20200215,20200216,20200217,20200218,20200219,20200220,20200221,20200222,20200223,20200224,20200225,20200226,20200227,20200228,20200229,20200301,20200302,20200303,20200304,20200305,20200306,20200307,20200308,20200309,20200310,20200311,20200312,20200313,20200314,20200315,20200316,20200317,20200318,20200319,20200320,20200321,20200322,20200323,20200324,20200325,20200326,20200327,20200328,20200329,20200330,20200331,20200401,20200402,20200403,20200404,20200405,20200406,20200407,20200408,20200409,20200410,20200411,20200412,20200413,20200414,20200415,20200416,20200417,20200418,20200419,20200420,20200421,20200422,20200423";
        String [] clearArray = clearDayStr.split(",");

        StringBuffer sql = new StringBuffer("insert into t_account_check (reconcile_date,current_total_currency_num,currency_id,currency_code,server_node_id,server_node_name,\n" +
                "        clearing_datetime,create_time)\n" +
                "select #{clearDay},current_total_currency_num,currency_id,currency_code,server_node_id,server_node_name,\n" +
                "        #{clearDay},CURRENT_TIMESTAMP from t_account_check where reconcile_date='20200114';");

        for (int i = 0; i < clearArray.length; i++) {
            String printSql = sql.toString().replace("#{clearDay}",clearArray[i]);
            System.err.println(printSql);
        }
    }
}
