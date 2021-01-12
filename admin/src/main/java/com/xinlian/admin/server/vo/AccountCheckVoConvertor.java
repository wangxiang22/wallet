package com.xinlian.admin.server.vo;

import com.xinlian.biz.model.AccountCheckModel;
import com.xinlian.common.Base.BaseVoConvertor;
import com.xinlian.common.utils.DateFormatUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;

@Slf4j
public class AccountCheckVoConvertor extends BaseVoConvertor<AccountCheckVo, AccountCheckModel> {


    @Override
    public AccountCheckVo convert(AccountCheckModel bo) throws Exception {
        AccountCheckVo vo = new AccountCheckVo();
        try{
            BeanUtils.copyProperties(vo,bo);
            vo.setCreateTime(DateFormatUtil.formatTillSecond(bo.getCreateTime()));
            vo.setClearingDatetime(DateFormatUtil.get(9,bo.getClearingDatetime())+" 23:59:59");
        }catch (Exception e){
            log.error("币种对账数据转换出现异常");
        }
        return vo;
    }
}
