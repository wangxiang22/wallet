package com.xinlian.member.biz.scedule;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.xinlian.biz.dao.TOrderMapper;
import com.xinlian.biz.dao.TWalletInfoMapper;
import com.xinlian.biz.dao.TWalletTradeOrderMapper;
import com.xinlian.biz.model.TOrder;
import com.xinlian.biz.model.TWalletTradeOrder;
import com.xinlian.biz.model.UserCurrencyStateReq;
import com.xinlian.biz.utils.AdminOptionsUtil;
import com.xinlian.common.enums.AdminOptionsBelongsSystemCodeEnum;
import com.xinlian.common.enums.OrderStateEnum;
import com.xinlian.common.enums.WalletTradeSystemCodeEnum;
import com.xinlian.common.response.OrderOpenRes;
import com.xinlian.common.result.BizException;
import com.xinlian.common.scedule.AbstractSchedule;
import com.xinlian.common.utils.CommonUtil;
import com.xinlian.member.biz.service.TOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;

import static com.xinlian.common.enums.CurrencyEnum.CAG;
import static com.xinlian.common.enums.CurrencyEnum.CAT;
import static com.xinlian.common.enums.WalletTradeOrderStatusEnum.TRADE_SUCCESS;
import static com.xinlian.common.enums.WalletTradeTypeEnum.CAG_BACK;
import static com.xinlian.common.enums.WalletTradeTypeEnum.CAT_BACK;

/**
 * @author: cms
 * @description:
 * @create: 2020/01/28
 * @des: 测试
 **/
@Component
@Slf4j
public class OrderTimeOutSchedule extends AbstractSchedule {
    @Autowired
    private TOrderService tOrderService;
    @Autowired
    private AdminOptionsUtil adminOptionsUtil;
    @Autowired
    private TWalletTradeOrderMapper tWalletTradeOrderMapper;
    @Autowired
    private TWalletInfoMapper tWalletInfoMapper;
    @Autowired
    private TOrderMapper tOrderMapper;
    @Autowired
    private RedisLockRegistry redisLockRegistry;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Value("${isTest}")
    private boolean isTest;
    /**
     * 获取cron表达式
     *
     * @return
     */
    @Override
    protected String getCron() {
        if(isTest) {
            return "0 0/5 * * * ?";
        }else{
            return "0/5 * * * * ?";
        }
    }

    /**
     * 每5秒筛查 等待交易的订单 过期的订单设置过期状态，退回用户挂单的CAT
     */
    //10:00  5 10:04  10:08
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void doSchedule() {

        String redisLockKey = new StringBuffer("ORDER_TIME_OUT").toString();
        Lock lock = redisLockRegistry.obtain(redisLockKey);
        boolean redisLockFlag = true;
        try {
            if (!lock.tryLock()) {
                log.debug(Thread.currentThread().getName() + " : 设置订单过期业务，获取分布式锁失败!lockKey:{}", redisLockKey);
                redisLockFlag = false;
                return;
            }
            OrderOpenRes orderOpenRes = adminOptionsUtil.fieldEntityObject(AdminOptionsBelongsSystemCodeEnum.ORDER_TIME_OUT.getBelongsSystemCode(), OrderOpenRes.class);
            boolean flag = CommonUtil.isTimeRange(orderOpenRes);
            String orderTimeOut = orderOpenRes.getOrderTimeOut();
            long orderTimeOutL = Long.parseLong(orderTimeOut);
            long nowTime = System.currentTimeMillis();
            List<TOrder> state = tOrderService.selectList(new EntityWrapper<TOrder>().eq("state", 0));
            List<TOrder> list = tOrderMapper.queryOutTimeOrders(orderTimeOutL, nowTime);
            transactionTemplate.execute(transactionStatus -> {
                if (!flag) {//如果不在交易时间段直接所有都过期
                    state.forEach(tOrder -> {
                        setOrderOutTime(tOrder);
                    });
                }
                //查出过期订单 修改为过期状态 归还冻结等等
                list.forEach(tOrder -> {
                    setOrderOutTime(tOrder);
                });
                return true;
            });
        } catch (Exception e) {
            e.printStackTrace();
            log.error("订单过期时间处理异常");
        } finally {
            if (redisLockFlag) {
                lock.unlock();
            }
        }
    }

    private void setOrderOutTime(TOrder tOrder) {
        log.info("-------------------------------------------开始解冻");
        String redisLockKey = "lock_" + tOrder.getOrderId();
        Lock lock = redisLockRegistry.obtain(redisLockKey);
        boolean redisLockFlag = true;
        try {
            if (!lock.tryLock()) {
                log.debug(Thread.currentThread().getName() + " : 设置订单锁，获取分布式锁失败!lockKey:{}", redisLockKey);
                redisLockFlag = false;
                return;
            }
            tOrder.setState(OrderStateEnum.TIME_OUT.getCode());
            tOrderService.updateById(tOrder);
            //归还卖家冻结cat
            UserCurrencyStateReq userCurrencyStateReq = new UserCurrencyStateReq();
            userCurrencyStateReq.setUid(tOrder.getSellerUid());
            userCurrencyStateReq.setCurrencyId(Long.valueOf(CAT.getCurrencyId()));
            userCurrencyStateReq.setAmount(tOrder.getAmount());
            tWalletInfoMapper.subFrozen(userCurrencyStateReq);
            //增加卖家cat
            int despoit1 = tWalletInfoMapper.despoit(userCurrencyStateReq);
            //归还卖家冻结cag
            UserCurrencyStateReq cagReq = new UserCurrencyStateReq();
            cagReq.setUid(tOrder.getSellerUid());
            cagReq.setCurrencyId(Long.valueOf(CAG.getCurrencyId()));
            cagReq.setAmount(tOrder.getCagFee());
            tWalletInfoMapper.subFrozen(cagReq);
            //增加卖家cag
            int despoit = tWalletInfoMapper.despoit(cagReq);
            //更新记录
            TWalletTradeOrder sellerOrder = new TWalletTradeOrder();
            sellerOrder.setCreateTime(new Date());
            sellerOrder.setDes(CAT_BACK.getTradeDesc());
            sellerOrder.setTradeType(CAT_BACK.getTradeType());
            sellerOrder.setDes(CAT_BACK.getTradeDesc());
            sellerOrder.setTradeStatus(TRADE_SUCCESS.getCode());//这边流水退还应该记为成功
            sellerOrder.setCurrencyId(Long.valueOf(CAT.getCurrencyId()));
            sellerOrder.setCurrencyCode(CAT.getCurrencyCode());
            sellerOrder.setUid(tOrder.getSellerUid());
            sellerOrder.setTradeSystemCode(WalletTradeSystemCodeEnum.APP_H5_SMART_CONTRACT.getCode());
            sellerOrder.setTradeCurrencyNum(tOrder.getAmount());
            Integer insert = tWalletTradeOrderMapper.insert(sellerOrder);
            if (insert != 1) {
                throw new BizException("处理订单过期时插入用户交易记录失败");
            }

            TWalletTradeOrder order = new TWalletTradeOrder();
            order.setCreateTime(new Date());
            order.setDes(CAG_BACK.getTradeDesc());
            order.setTradeType(CAG_BACK.getTradeType());
            order.setTradeStatus(TRADE_SUCCESS.getCode());//交易失败
            order.setCurrencyId(Long.valueOf(CAG.getCurrencyId()));
            order.setCurrencyCode(CAG.getCurrencyCode());
            order.setUid(tOrder.getSellerUid());
            order.setTradeSystemCode(WalletTradeSystemCodeEnum.APP_H5_SMART_CONTRACT.getCode());
            order.setTradeCurrencyNum(tOrder.getCagFee());
            Integer i = tWalletTradeOrderMapper.insert(order);
            if (i != 1) {
                throw new BizException("处理订单过期时插入用户交易记录失败");
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (redisLockFlag) {
                lock.unlock();
            }
        }
    }

}


