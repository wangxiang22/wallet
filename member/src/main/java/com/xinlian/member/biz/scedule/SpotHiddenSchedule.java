//package com.xinlian.member.biz.scedule;
//
//import com.baomidou.mybatisplus.mapper.EntityWrapper;
//import com.xinlian.biz.dao.TWalletInfoMapper;
//import com.xinlian.biz.dao.TWalletTradeOrderMapper;
//import com.xinlian.biz.model.TOrder;
//import com.xinlian.biz.model.TRecommend;
//import com.xinlian.biz.model.TWalletTradeOrder;
//import com.xinlian.biz.model.UserCurrencyStateReq;
//import com.xinlian.biz.utils.AdminOptionsUtil;
//import com.xinlian.common.enums.AdminOptionsBelongsSystemCodeEnum;
//import com.xinlian.common.response.OrderOpenRes;
//import com.xinlian.common.result.BizException;
//import com.xinlian.common.scedule.AbstractSchedule;
//import com.xinlian.common.utils.CommonUtil;
//import com.xinlian.member.biz.service.TOrderService;
//import com.xinlian.member.biz.service.TRecommendService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Date;
//import java.util.List;
//
//import static com.xinlian.common.contants.OrderConstant.TIME_OUT_BACK;
//import static com.xinlian.common.enums.CurrencyEnum.CAT;
//import static com.xinlian.common.enums.WalletTradeOrderStatusEnum.TRADE_FAIL;
//
///**
// * @author: cms
// * @description:
// * @create: 2020/01/28
// * @des: 测试
// **/
//@Component
//@Slf4j
//public class SpotHiddenSchedule extends AbstractSchedule {
//
//    @Autowired
//    private AdminOptionsUtil adminOptionsUtil;
//    @Autowired
//    private TRecommendService tRecommendService;
//    /**
//     * 获取cron表达式
//     *
//     * @return
//     */
//    @Override
//    protected String getCron() {
//        return "0 0/1 * * * ?";
//    }
//
//    /**
//     * 每分钟检查挂单交易显隐开关是否开启 根据开放挂单时间段设置显隐开关
//     */
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void doSchedule() {
//        try {
//            OrderOpenRes orderOpenRes = adminOptionsUtil.fieldEntityObject(AdminOptionsBelongsSystemCodeEnum.ORDER_TIME_OUT.getBelongsSystemCode(), OrderOpenRes.class);
//            boolean flag = CommonUtil.isTimeRange(orderOpenRes);
//            //如果当前时间禁止挂单设置隐藏 否则设置开放
//            TRecommend tRecommend = tRecommendService.selectOne(new EntityWrapper<TRecommend>().eq("code", "spot"));
//            if (flag) {
//                if (tRecommend.getIsHidden() == 1) {
//                    tRecommend.setIsHidden(0);
//                    tRecommendService.updateById(tRecommend);
//                }
//            } else {
//                if (tRecommend.getIsHidden() == 0) {
//                    tRecommend.setIsHidden(1);
//                    tRecommendService.updateById(tRecommend);
//                }
//            }
//        }catch (Exception e){
//            log.error("更新spot控制显示隐藏开关失败");
//        }
//    }
//
//}
//
//
