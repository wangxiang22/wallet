package com.xinlian.member.server.controller;

import com.alibaba.fastjson.JSONObject;
import com.xinlian.common.enums.CheckSmsMethodEnum;
import com.xinlian.common.enums.SendRegisterTypeEnum;
import com.xinlian.common.redis.RedisKeys;
import com.xinlian.common.request.CatWalletPayDto;
import com.xinlian.common.request.CatWalletPayReq;
import com.xinlian.common.request.CatWalletPayStatusReq;
import com.xinlian.common.request.RegisterReq;
import com.xinlian.common.response.CatWalletPayCallbackRes;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.member.biz.jwt.annotate.EncryptionAnnotation;
import com.xinlian.member.biz.jwt.annotate.PassToken;
import com.xinlian.member.biz.jwt.util.JwtUtil;
import com.xinlian.member.biz.redis.RedisClient;
import com.xinlian.member.biz.service.BlockmallService;
import com.xinlian.member.biz.service.IRegisterLoginService;
import com.xinlian.member.server.controller.handler.CheckSmsRuleHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 钱包服务对外提供给布鲁克商城的接口
 */
@Api(value = "钱包服务对外提供给布鲁克商城的接口")
@RestController
@RequestMapping("/{versionPath}/blockmall")
@Slf4j
public class BlockmallController {
    @Autowired
    private IRegisterLoginService registerLoginService;
    @Autowired
    private CheckSmsRuleHandler checkSmsRuleHandler;
    @Autowired
    private BlockmallService blockmallService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisClient redisClient;

    @ApiOperation(value = "发送短信验证码", httpMethod = "POST")
    @PostMapping("/sendPhoneCode")
    @EncryptionAnnotation
    @PassToken
    public ResponseResult sendPhoneCode(@RequestBody Map<String, String> paramMap) {
        RegisterReq registerReq = JSONObject.parseObject(paramMap.get("data"), RegisterReq.class);
        if (null == registerReq || StringUtils.isBlank(registerReq.getPhone())) {
            return ResponseResult.error("参数错误");
        }

        checkSmsRuleHandler.doCheckSmsRuleHandler(registerReq.getPhone(),
                CheckSmsMethodEnum.BLOCKMALL_CERT.getMethodCode());
        registerReq.setType(SendRegisterTypeEnum.BLOCKMALL_CERT.getType());
        return registerLoginService.sendRegisterSms(registerReq, false);
    }

    @ApiOperation(value = "校验哥伦布认证信息", httpMethod = "POST")
    @PostMapping("/cert")
    @EncryptionAnnotation
    @PassToken
    public ResponseResult cert(@RequestBody Map<String, String> paramMap) {
        RegisterReq registerReq = JSONObject.parseObject(paramMap.get("data"), RegisterReq.class);
        if (null == registerReq || StringUtils.isBlank(registerReq.getPhone())) {
            return ResponseResult.error("参数错误");
        }
        log.info(":::: 入参：" + registerReq.toString());
        registerReq.check4BblockmallCert();
        registerReq.setType(SendRegisterTypeEnum.BLOCKMALL_CERT.getType());
        return blockmallService.cert(registerReq);
    }

    /**
     * // 区域 private Integer countryCode; <br>
     * // 节点 private Long nodeId; <br>
     * private String phone;
     *
     * @return
     */
    @ApiOperation(value = "发送支付短信验证码", httpMethod = "POST")
    @PostMapping("/sendPayPhoneCode")
    @EncryptionAnnotation
    @PassToken
    public ResponseResult sendPayPhoneCode(@RequestBody Map<String, String> paramMap) {
        RegisterReq registerReq = JSONObject.parseObject(paramMap.get("data"), RegisterReq.class);
        if (null == registerReq || StringUtils.isBlank(registerReq.getPhone())) {
            return ResponseResult.error("参数错误");
        }
        checkSmsRuleHandler.doCheckSmsRuleHandler(registerReq.getPhone(),
                CheckSmsMethodEnum.BLOCKMALL_PAY.getMethodCode());
        registerReq.setType(SendRegisterTypeEnum.BLOCKMALL_PAY.getType());
        return registerLoginService.sendRegisterSms(registerReq, false);
    }

    //    @PassToken
    @ApiOperation(value = "支付", httpMethod = "POST")
    @PostMapping("/pay")
    @EncryptionAnnotation
    public ResponseResult pay(@RequestBody Map<String, String> paramMap) {
        CatWalletPayReq catWalletPayReq = JSONObject.parseObject(paramMap.get("data"), CatWalletPayReq.class);
        if (null == catWalletPayReq || StringUtils.isBlank(catWalletPayReq.getOrderNo())) {
            return ResponseResult.error("参数错误");
        }
        catWalletPayReq.setType(SendRegisterTypeEnum.BLOCKMALL_PAY.getType());
        catWalletPayReq.setUid(jwtUtil.getUserId(request));
        catWalletPayReq.setNodeId(jwtUtil.getNodeId(request));
        log.info(":::: 入参：" + catWalletPayReq.toString());
        blockmallService.pay(catWalletPayReq);
        log.info(":::: 异步执行支付方法，直接返回成功给调用方，真正结果待回调返回或通过订单号主动查询");
        return ResponseResult.ok();
    }

    @ApiOperation(value = "支付状态", httpMethod = "POST")
    @PostMapping("/payStatus")
    @EncryptionAnnotation
    @PassToken
    public ResponseResult<CatWalletPayCallbackRes> payStatus(@RequestBody Map<String, String> paramMap) {
        CatWalletPayStatusReq catWalletPayStatusReq = JSONObject.parseObject(paramMap.get("data"),
                CatWalletPayStatusReq.class);
        if (null == catWalletPayStatusReq || StringUtils.isBlank(catWalletPayStatusReq.getOrderNo())) {
            return ResponseResult.error("参数错误");
        }
        log.info(":::: 入参：" + catWalletPayStatusReq.toString());
        ResponseResult res = blockmallService.payStatus(catWalletPayStatusReq);
        log.info(":::: 支付结果： " + res);
        return res;
    }

    //    @PassToken
    @ApiOperation(value = "解密二维码参数", httpMethod = "POST")
    @PostMapping("/decryptQrCode")
    @EncryptionAnnotation
    public ResponseResult<CatWalletPayDto> decryptQrCode(@RequestBody Map<String, String> paramMap) {
        log.info(":::: 入参解密后：" + JSONObject.toJSONString(paramMap));
        CatWalletPayDto catWalletPayDto = JSONObject.parseObject(paramMap.get("data"), CatWalletPayDto.class);
        if (null == catWalletPayDto
                || catWalletPayDto.getQrCodeTimeOut().compareTo(System.currentTimeMillis() / 1000) < 0) {
            return ResponseResult.error("二维码已过期");
        }
        if (null == catWalletPayDto
                || catWalletPayDto.getOrderTimeOut().compareTo(System.currentTimeMillis() / 1000) < 0
                || StringUtils.isBlank(catWalletPayDto.getOrderNo())) {
            return ResponseResult.error("订单已过期");
        }

        Long timeOut = catWalletPayDto.getOrderTimeOut() - (System.currentTimeMillis() / 1000);

        redisClient.set(RedisKeys.blockmallUnTimeoutOrderKey(catWalletPayDto.getOrderNo()),
                catWalletPayDto.getOrderTimeOut(), timeOut);

        return ResponseResult.ok(catWalletPayDto);
    }

}
