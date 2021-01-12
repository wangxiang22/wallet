package com.xinlian.member.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.xinlian.common.enums.ErrorCode;
import com.xinlian.common.request.GeetestVo;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.member.biz.jiyan.GeetestConfig;
import com.xinlian.member.biz.jiyan.GeetestLib;
import com.xinlian.member.biz.redis.RedisClient;
import com.xinlian.member.biz.service.JiYanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.util.HashMap;

import static com.xinlian.common.contants.GeeTestConstant.GEETEST_KEY;

@Service
public class JiYanServiceImpl implements JiYanService {
    @Autowired
    private RedisClient redisClient;

    @Override
    public ResponseResult register(GeetestVo geetestVo) {


        GeetestLib gtSdk = new GeetestLib(GeetestConfig.getGeetest_id(), GeetestConfig.getGeetest_key(),
                GeetestConfig.isnewfailback());

        String resStr = "{}";

//        String userid = "test";

        //自定义参数,可选择添加
        HashMap<String, String> param = new HashMap<String, String>();
//        param.put("user_id", userid); //网站用户id
//        param.put("client_type", "web"); //web:电脑上的浏览器；h5:手机上的浏览器，包括移动应用内完全内置的web_view；native：通过原生SDK植入APP应用的方式
//        param.put("ip_address", "127.0.0.1"); //传输用户请求验证时所携带的IP
        //进行验证预处理
        int gtServerStatus = gtSdk.preProcess(param);
        redisClient.setDay(GEETEST_KEY+geetestVo.getCheckParam(),gtServerStatus,10);
        resStr = gtSdk.getResponseStr();
        JSONObject jsonObject = JSON.parseObject(resStr);
        return ResponseResult.builder().msg(ErrorCode.REQ_SUCCESS.getDes()).result(jsonObject).code(ErrorCode.REQ_SUCCESS.getCode()).build();
    }

    @Override
    public ResponseResult validate(GeetestVo geetestVo) {

        GeetestLib gtSdk = new GeetestLib(GeetestConfig.getGeetest_id(), GeetestConfig.getGeetest_key(),
                GeetestConfig.isnewfailback());

        String challenge = geetestVo.getGeetest_challenge();
        String validate = geetestVo.getGeetest_validate();
        String seccode = geetestVo.getGeetest_seccode();

        int gt_server_status_code = 0;
        if (null!=redisClient.get(GEETEST_KEY + geetestVo.getCheckParam())) {
            gt_server_status_code =redisClient.get(GEETEST_KEY + geetestVo.getCheckParam());
        }

        //自定义参数,可选择添加
        HashMap<String, String> param = new HashMap<String, String>();

        int gtResult = 0;

        if (gt_server_status_code == 1) {
            //gt-server正常，向gt-server进行二次验证
            try {
                gtResult = gtSdk.enhencedValidateRequest(challenge, validate, seccode, param);
            }catch (Exception e){
                e.printStackTrace();
            }
        } else {
            // gt-server非正常情况下，进行failback模式验证

        //    System.out.println("failback:use your own server captcha validate");
            gtResult = gtSdk.failbackValidateRequest(challenge, validate, seccode);
           // System.out.println(gtResult);
        }


        if (gtResult == 1) {
            // 验证成功
            JSONObject data = new JSONObject();
            try {
                data.put("status", "success");
                data.put("version", gtSdk.getVersionInfo());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return ResponseResult.builder().code(ErrorCode.REQ_SUCCESS.getCode()).result(data).msg(ErrorCode.REQ_SUCCESS.getDes()).build();
        }
        else {
            // 验证失败
            JSONObject data = new JSONObject();
            try {
                data.put("status", "fail");
                data.put("version", gtSdk.getVersionInfo());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return ResponseResult.builder().code(ErrorCode.CHECK_ERROR.getCode()).result(data).msg(ErrorCode.CHECK_ERROR.getDes()).build();
        }
    }
}
