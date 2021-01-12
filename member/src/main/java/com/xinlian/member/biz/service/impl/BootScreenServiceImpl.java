package com.xinlian.member.biz.service.impl;

import com.xinlian.biz.utils.AdminOptionsUtil;
import com.xinlian.common.contants.GlobalConstant;
import com.xinlian.common.enums.AdminOptionsBelongsSystemCodeEnum;
import com.xinlian.common.response.BootScreenRes;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.member.biz.service.BootScreenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BootScreenServiceImpl implements BootScreenService {
    @Autowired
    private AdminOptionsUtil adminOptionsUtil;

    @Override
    public ResponseResult<BootScreenRes> findBootScreen() {
        ResponseResult<BootScreenRes> result = new ResponseResult<>();
        BootScreenRes bootScreenRes = new BootScreenRes();
        try {
            bootScreenRes = adminOptionsUtil.fieldEntityObject(AdminOptionsBelongsSystemCodeEnum.APP_BOOT_SCREEN.getBelongsSystemCode(), BootScreenRes.class);
        }catch (Exception e){
            log.error("查找app启动界面url及开关状态出现异常：{}",e.toString(),e);
            result.setCode(GlobalConstant.ResponseCode.FAIL);
            return result;
        }
        result.setCode(GlobalConstant.ResponseCode.SUCCESS);
        result.setResult(bootScreenRes);
        return result;
    }
}
