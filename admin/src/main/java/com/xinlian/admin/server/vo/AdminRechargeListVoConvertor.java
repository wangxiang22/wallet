package com.xinlian.admin.server.vo;

import com.xinlian.biz.model.TWalletTradeOrder;
import com.xinlian.common.Base.BaseVoConvertor;
import com.xinlian.common.enums.WalletTradeOrderStatusEnum;
import com.xinlian.common.utils.DateFormatUtil;
import org.apache.commons.beanutils.BeanUtils;

/**
 * @author Song
 * @date 2020-05-20 14:30
 * @description Admin充值Vo
 */
public class AdminRechargeListVoConvertor extends BaseVoConvertor<AdminRechargeListVo, TWalletTradeOrder> {


    @Override
    public AdminRechargeListVo convert(TWalletTradeOrder bo) throws Exception {
        AdminRechargeListVo vo = new AdminRechargeListVo();
        try{
            BeanUtils.copyProperties(vo,bo);
            vo.setTradeStatusName(WalletTradeOrderStatusEnum.getEnumDesc(bo.getTradeStatus()));
            vo.setDisposeCheckTime(DateFormatUtil.formatTillSecond(bo.getCreateTime()));
        }catch (Exception e){
            e.printStackTrace();
        }
        return vo;
    }
}
