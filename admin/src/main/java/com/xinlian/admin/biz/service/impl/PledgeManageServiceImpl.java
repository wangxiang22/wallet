package com.xinlian.admin.biz.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xinlian.admin.biz.service.PledgeManageService;
import com.xinlian.biz.dao.TUserInfoMapper;
import com.xinlian.biz.dao.TWalletInfoMapper;
import com.xinlian.biz.dao.TWalletTradeOrderMapper;
import com.xinlian.biz.model.TUserInfo;
import com.xinlian.biz.model.TWalletInfo;
import com.xinlian.biz.model.TWalletTradeOrder;
import com.xinlian.biz.utils.AdminOptionsUtil;
import com.xinlian.common.dto.PledgeManagePageDto;
import com.xinlian.common.enums.*;
import com.xinlian.common.request.PledgeAuditReq;
import com.xinlian.common.request.PledgeManagePageReq;
import com.xinlian.common.response.PageResult;
import com.xinlian.common.response.PledgeManagePageRes;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.result.BizException;
import com.xinlian.common.result.ErrorInfoEnum;
import com.xinlian.common.utils.UdunBigDecimalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.math.BigDecimal;
import java.util.*;

/**
 * 矿池质押审核 服务实现类
 * @author lt
 * @since 2020-06-11
 */
@Slf4j
@Service
public class PledgeManageServiceImpl implements PledgeManageService {
    @Autowired
    private TWalletTradeOrderMapper walletTradeOrderMapper;
    @Autowired
    private AdminOptionsUtil adminOptionsUtil;
    @Autowired
    private TWalletInfoMapper walletInfoMapper;
    @Autowired
    private TUserInfoMapper userInfoMapper;


    @Override
    public PageResult<List<PledgeManagePageRes>> findPledgePage(PledgeManagePageReq pledgeManagePageReq) {
        pledgeManagePageReq.setDes(PledgeEnum.PLEDGE_MINING.getParameterValue());
        PageResult<List<PledgeManagePageRes>> result = new PageResult<>();
        result.setCode(ErrorInfoEnum.SUCCESS.getCode());
        PageHelper.startPage(pledgeManagePageReq.getPageNum(),pledgeManagePageReq.getPageSize());
        List<PledgeManagePageDto> dtoList = walletTradeOrderMapper.findPledgePage(pledgeManagePageReq);
        List<PledgeManagePageRes> resList = new ArrayList<>();
        if (null != dtoList && dtoList.size() > 0) {
            dtoList.forEach(PledgeManagePageDto -> resList.add(PledgeManagePageDto.pledgeManagePageRes()));
            PageInfo<PledgeManagePageDto> pageInfoDto = new PageInfo<>(dtoList);
            result.setCurPage(pageInfoDto.getPageNum());
            result.setPageSize(pageInfoDto.getPageSize());
            result.setTotal(pageInfoDto.getTotal());
            result.setResult(resList);
        }
        return result;
    }

    @Override
    @Transactional
    public ResponseResult auditPledge(PledgeAuditReq req) {
        //通过
        if (PledgeApplyEnum.PLEDGE_PASS.getPledgeCode().equals(req.getPledgeStatus())) {
            approvedPledge(req.getUid());
        }
        //拒绝
        if (PledgeApplyEnum.PLEDGE_REJECTED.getPledgeCode().equals(req.getPledgeStatus())) {
            //用户余额表中冻结字段对应数量清除，加回到余额字段
            TWalletInfo tWalletInfo = new TWalletInfo();
            tWalletInfo.setUid(req.getUid());
            tWalletInfo.setCurrencyId(Long.parseLong(String.valueOf(CurrencyEnum.CAT.getCurrencyId())));
            tWalletInfo.setFrozenNum(getPledgeAmount());
            int unFreeze = walletInfoMapper.disposalBalanceAndUnFreeze(tWalletInfo);
            if (0 == unFreeze) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                log.error("金额解冻失败：" + req.getUid());
                throw new BizException("金额解冻失败");
            }
            //资产流水表，trade_status改为失败状态，备注字段remark添加为算能质押审核失败，fail_reason字段添加拒绝的理由
            TWalletTradeOrder walletTradeOrder = findPledgeTradeOrder(req.getUid());
            if (null == walletTradeOrder) {
                log.error("获取用户资产流水信息失败：" + req.getUid());
                throw new BizException("获取用户资产流水信息失败");
            }
            TWalletTradeOrder tradeOrder = new TWalletTradeOrder();
            tradeOrder.setId(walletTradeOrder.getId());
            tradeOrder.setTradeStatus(WalletTradeOrderStatusEnum.AUDIT_REJECT.getCode());
            tradeOrder.setRemark("质押审核已拒绝");
            tradeOrder.setFailReason(req.getFailReason());
            tradeOrder.setUpdateTime(new Date());
            int updateResult = walletTradeOrderMapper.updatePledgeTradeOrder(tradeOrder);
            if (0 == updateResult) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                log.error("质押审核操作失败：" + req.getUid());
                throw new BizException("质押审核操作失败");
            }
            //用户表质押状态改为1，未质押
            TUserInfo tUserInfo = new TUserInfo();
            tUserInfo.setUid(req.getUid());
            tUserInfo.setPledgeState(PledgeUserApplyEnum.NOT_PLEDGE.getPledgeCode());
            Integer update = userInfoMapper.updateById(tUserInfo);
            if (0 == update) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                log.error("修改用户质押状态失败：" + req.getUid());
                throw new BizException("修改用户质押状态失败");
            }
        }
        return ResponseResult.builder().code(ErrorInfoEnum.SUCCESS.getCode()).msg("质押审核操作成功").build();
    }

    /**
     * 通过单个用户的算能质押申请
     * @param uid 用户id
     * @return
     */
    public void approvedPledge(Long uid) {
        //用户余额表中冻结字段对应数量清除
        TWalletInfo tWalletInfo = new TWalletInfo();
        tWalletInfo.setUid(uid);
        tWalletInfo.setCurrencyId(Long.parseLong(String.valueOf(CurrencyEnum.CAT.getCurrencyId())));
        tWalletInfo.setFrozenNum(getPledgeAmount());
        int updateFrozenNum = walletInfoMapper.updateFrozenNum(tWalletInfo);
        if (0 == updateFrozenNum) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error("冻结金额扣除失败：" + uid);
            throw new BizException("冻结金额扣除失败");
        }
        //资产流水表，trade_status改为7，备注字段remark添加为质押挖矿审核通过
        TWalletTradeOrder walletTradeOrder = findPledgeTradeOrder(uid);
        if (null == walletTradeOrder) {
            log.error("获取用户资产流水信息失败：" + uid);
            throw new BizException("获取用户资产流水信息失败");
        }
        TWalletTradeOrder tradeOrder = new TWalletTradeOrder();
        tradeOrder.setId(walletTradeOrder.getId());
        tradeOrder.setTradeStatus(WalletTradeOrderStatusEnum.TRADE_SUCCESS.getCode());
        tradeOrder.setRemark("质押审核已通过");
        tradeOrder.setUpdateTime(new Date());
        int updateResult = walletTradeOrderMapper.updatePledgeTradeOrder(tradeOrder);
        if (0 == updateResult) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error("质押审核操作失败：" + uid);
            throw new BizException("质押审核操作失败");
        }
        //用户表质押状态改为3，时间字段赋值
        TUserInfo tUserInfo = new TUserInfo();
        tUserInfo.setUid(uid);
        tUserInfo.setPledgeState(PledgeUserApplyEnum.HAVE_PLEDGE.getPledgeCode());
        tUserInfo.setPledgeTime(new Date());
        int update = userInfoMapper.updateUserInfo(tUserInfo);
        if (0 == update) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error("修改用户质押状态失败：" + uid);
            throw new BizException("修改用户质押状态失败");
        }
    }

    /**
     * 获取质押的金额
     * @return 质押的金额
     */
    private BigDecimal getPledgeAmount() {
        try {
            String pledgeAmount = adminOptionsUtil.findAdminOptionOne(AdminOptionsBelongsSystemCodeEnum.APP_PLEDGE.getBelongsSystemCode());
            if(null != pledgeAmount && !"".equals(pledgeAmount)) {
                return new BigDecimal(pledgeAmount).setScale(4,BigDecimal.ROUND_DOWN);
            }else {
                //质押金额定为2.0000
               return new BigDecimal("2.0000").setScale(4,BigDecimal.ROUND_DOWN);
            }
        }catch (Exception e){
            log.error("查询矿池质押金额出现异常：{}",e.toString(),e);
            //质押金额定为2.0000
            return new BigDecimal("2.0000").setScale(4,BigDecimal.ROUND_DOWN);
        }
    }

    /**
     * 查询用户的质押流水记录
     * @param uid 用户id
     * @return
     */
    private TWalletTradeOrder findPledgeTradeOrder(Long uid) {
        TWalletTradeOrder tWalletTradeOrder = new TWalletTradeOrder();
        tWalletTradeOrder.setUid(uid);
        tWalletTradeOrder.setCurrencyId(Long.parseLong(String.valueOf(CurrencyEnum.CAT.getCurrencyId())));
        tWalletTradeOrder.setTradeStatus(WalletTradeOrderStatusEnum.APPLY.getCode());
        tWalletTradeOrder.setDes(WalletTradeTypeEnum.PLEDGE_MINING.getTradeDesc());
        return walletTradeOrderMapper.selectOne(tWalletTradeOrder);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchAuditPledgeToPass(List<Long> uids){
        //组装查询条件
        TWalletTradeOrder walletTradeOrder = new TWalletTradeOrder();
        walletTradeOrder.setTradeStatus(WalletTradeOrderStatusEnum.APPLY.getCode());
        walletTradeOrder.setTradeType(WalletTradeTypeEnum.PLEDGE_MINING.getTradeType());
        walletTradeOrder.setDes(WalletTradeTypeEnum.PLEDGE_MINING.getTradeDesc());
        walletTradeOrder.setCurrencyCode(CurrencyEnum.CAT.getCurrencyCode());
        List<TWalletTradeOrder> getWalletTradeOrders = null;
        if(null==uids || uids.size()==0){
            //1.从库里获取
            getWalletTradeOrders = walletTradeOrderMapper.getUidsByIdxQyTradeLog(walletTradeOrder);
        }else{//根据uids获取交易单集合
            Map<String,Object> paramMap = new HashMap<String,Object>();
            paramMap.put("uids",uids);
            paramMap.put("walletOrder",walletTradeOrder);
            getWalletTradeOrders = walletTradeOrderMapper.getUidsByIdxQyTradeLogAndUids(paramMap);
        }
        if(null==getWalletTradeOrders || getWalletTradeOrders.size()==0){return;}
        //1.拿到uid
        for(TWalletTradeOrder tradeOrder : getWalletTradeOrders){
            //1.更新wallet_info 资产
            int updateTradeOrder = this.updateTradeOrderToSuccess(tradeOrder);
            //2.冻结金额减去
            int updateWalletInfo = this.updateWalletInfo(tradeOrder);
            //3.userInfo状态变更
            int updateUserInfo = this.updateUserInfo(tradeOrder);
            if(0==updateTradeOrder||0==updateWalletInfo||0==updateUserInfo){
                throw new BizException("质押批量审核失败:uid["+tradeOrder.getUid()+"]");
            }
        }
    }

    private int updateUserInfo(TWalletTradeOrder tradeOrder) {
        TUserInfo userInfo = new TUserInfo();
        userInfo.setUid(tradeOrder.getUid());
        userInfo.setPledgeState(PledgeUserApplyEnum.HAVE_PLEDGE.getPledgeCode());
        userInfo.setPledgeTime(new Date());
        return userInfoMapper.updateUserInfo(userInfo);
    }

    private int updateTradeOrderToSuccess(TWalletTradeOrder tradeOrder){
        TWalletTradeOrder walletTradeOrder = new TWalletTradeOrder();
        walletTradeOrder.setOldTradeStatus(WalletTradeOrderStatusEnum.APPLY.getCode());
        walletTradeOrder.setTradeStatus(WalletTradeOrderStatusEnum.TRADE_SUCCESS.getCode());
        walletTradeOrder.setId(tradeOrder.getId());
        walletTradeOrder.setRemark("批量质押审核通过");
        return walletTradeOrderMapper.updateWalletTradeOrder(walletTradeOrder);
    }

    //减去冻结金额
    private int updateWalletInfo(TWalletTradeOrder tradeOrder){
        TWalletInfo walletInfo = new TWalletInfo();
        walletInfo.setCurrencyId(Long.parseLong(CurrencyEnum.CAT.getCurrencyId()+""));
        walletInfo.setUid(tradeOrder.getUid());
        walletInfo.setFrozenNum(UdunBigDecimalUtil.convertPlus(tradeOrder.getTradeCurrencyNum()));
        return walletInfoMapper.updateFrozenNum(walletInfo);
    }

}
