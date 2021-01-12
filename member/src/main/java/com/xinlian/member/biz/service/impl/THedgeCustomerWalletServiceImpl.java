package com.xinlian.member.biz.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.xinlian.biz.dao.TFreezeCycleOptionMapper;
import com.xinlian.biz.dao.THedgeCustomerWalletMapper;
import com.xinlian.biz.dao.TWalletInfoMapper;
import com.xinlian.biz.model.TFreezeCycleOption;
import com.xinlian.biz.model.THedgeCustomerWallet;
import com.xinlian.common.contants.GlobalConstant;
import com.xinlian.common.enums.CurrencyEnum;
import com.xinlian.common.enums.HedgeCustomerWalletEnum;
import com.xinlian.common.request.HedgeCustomerReq;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.result.BizException;
import com.xinlian.common.result.ErrorInfoEnum;
import com.xinlian.common.utils.DateFormatUtil;
import com.xinlian.member.biz.redis.RedisClient;
import com.xinlian.member.biz.redis.RedisConstant;
import com.xinlian.member.biz.service.THedgeCustomerWalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 冻结客户资产记录表 服务实现类
 * </p>
 *
 * @author lt
 * @since 2020-05-29
 */
@Slf4j
@Service
public class THedgeCustomerWalletServiceImpl implements THedgeCustomerWalletService {
    @Autowired
    private THedgeCustomerWalletMapper hedgeCustomerWalletMapper;
    @Autowired
    private TFreezeCycleOptionMapper freezeCycleOptionMapper;
    @Autowired
    private TWalletInfoMapper walletInfoMapper;
    @Autowired
    private RedisClient redisClient;

    @Override
    public ResponseResult addMiningHedgeCustomer(HedgeCustomerReq hedgeCustomerReq) {
        //该接口只对算力地球保证金冻结业务做处理
        try {
            if (!HedgeCustomerWalletEnum.MINING_DEPOSIT.getCode().equals(hedgeCustomerReq.getHedgeCode())) {
                throw new BizException("参数错误！");
            }
            //获取对应冻结周期配置信息
            TFreezeCycleOption freezeCycleOption = findFreezeCycleOption(HedgeCustomerWalletEnum.MINING_DEPOSIT.getTypeCode());
            //创建需要新增的冻结用户相关信息
            THedgeCustomerWallet hedgeCustomerWallet = new THedgeCustomerWallet();
            hedgeCustomerWallet.setUid(hedgeCustomerReq.getUid());
            hedgeCustomerWallet.setHedgeNum(freezeCycleOption.getFreezeAmount());
            hedgeCustomerWallet.setStatus(0);
            //算力地球默认CAT
            hedgeCustomerWallet.setCurrencyId(Long.parseLong(String.valueOf(CurrencyEnum.CAT.getCurrencyId())));
            hedgeCustomerWallet.setCurrencyCode(CurrencyEnum.CAT.getCurrencyCode());
            hedgeCustomerWallet.setOpeType(HedgeCustomerWalletEnum.MINING_DEPOSIT.getTypeCode());
            hedgeCustomerWallet.setHedgeTime(new Date());
            //待解冻时间需要从冻结周期配置中读取天数之后与开始冻结时间相加得到
            hedgeCustomerWallet.setStayUnfreezeTime(DateFormatUtil.addDate(new Date(),freezeCycleOption.getFreezeCycle()));
            Integer insertResult = hedgeCustomerWalletMapper.insert(hedgeCustomerWallet);
            if (0 == insertResult) {
                //手动回滚
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                throw new BizException("冻结用户信息插入失败");
            }
            //用户钱包资产表中的CAT金额需要扣除相应保证金金额
            int updateResult = walletInfoMapper.updateReduceBalanceNum(freezeCycleOption.getFreezeAmount(), hedgeCustomerReq.getUid(),
                    Long.parseLong(String.valueOf(CurrencyEnum.CAT.getCurrencyId())));
            if (0 == updateResult) {
                //手动回滚
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                throw new BizException("用户保证金扣除失败");
            }
            return ResponseResult.builder().code(GlobalConstant.ResponseCode.SUCCESS).build();
        } catch (BizException e) {
            return new ResponseResult(e);
        } catch (Exception e) {
            return new ResponseResult(new BizException(ErrorInfoEnum.FAILED));
        }
    }

    /**
     * 根据对应编码获取冻结周期配置信息
     * @param freezeTypeCode 编码
     * @return 对应冻结周期配置信息
     */
    private TFreezeCycleOption findFreezeCycleOption(String freezeTypeCode) {
        //从redis中读取
        TFreezeCycleOption freezeCycleOption = redisClient.get(RedisConstant.FREEZE_CYCLE_OPTION + freezeTypeCode);
        if (null == freezeCycleOption) {
            //如果redis中没有数据，从数据库中获取，并存入redis
            TFreezeCycleOption tFreezeCycleOption = new TFreezeCycleOption();
            tFreezeCycleOption.setFreezeTypeCode(freezeTypeCode);
            TFreezeCycleOption cycleOption = freezeCycleOptionMapper.selectOne(tFreezeCycleOption);
            if (null == cycleOption) {
                throw new BizException("冻结周期配置获取异常");
            }
            //存入redis
            redisClient.set(RedisConstant.FREEZE_CYCLE_OPTION + freezeTypeCode,cycleOption);
            return cycleOption;
        }else {
            //redis中获取到数据后正常返回
            return freezeCycleOption;
        }
    }

    /**
     * 更新冻结用户信息，解冻已到期用户资产
     * 每天凌晨一点执行一次--->测试阶段，暂时未开启定时任务
     */
//    @Scheduled(cron = "0 0 1 * * ?")
    public void updateFreezeStatus() {
        //获取状态为冻结且待解冻时间已到期的用户id列表，以便将对应金额返还给对应用户
        List<Long> expireFreezeUidList = hedgeCustomerWalletMapper.findExpireFreezeUidList();
        if (null == expireFreezeUidList || 0 == expireFreezeUidList.size()) {
            return;
        }
        //先修改状态为冻结且待解冻时间已到期的用户的状态为解冻
        int updateResult = hedgeCustomerWalletMapper.updateFreezeStatus(expireFreezeUidList);
        if (expireFreezeUidList.size() != updateResult) {
            //手动回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new BizException("用户状态解冻失败");
        }
        //再将相应的冻结金额返还到对应用户的钱包余额中
        List<THedgeCustomerWallet> hedgeCustomerWalletList =
                hedgeCustomerWalletMapper.selectList(new EntityWrapper<THedgeCustomerWallet>().in("uid", expireFreezeUidList));
        for (THedgeCustomerWallet hedgeCustomerWallet : hedgeCustomerWalletList) {
            int addResult = walletInfoMapper.updateAddBalanceNum(hedgeCustomerWallet.getHedgeNum(), hedgeCustomerWallet.getUid(), hedgeCustomerWallet.getCurrencyId());
            if (0 == addResult) {
                //手动回滚
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                throw new BizException("用户冻结金额加到钱包余额失败");
            }
        }
    }
}