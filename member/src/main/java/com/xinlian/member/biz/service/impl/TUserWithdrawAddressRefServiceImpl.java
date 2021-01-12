package com.xinlian.member.biz.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.xinlian.biz.dao.TCurrencyManageMapper;
import com.xinlian.biz.model.TCurrencyManage;
import com.xinlian.common.contants.GlobalConstant;
import com.xinlian.common.request.AddWithdrawAddressReq;
import com.xinlian.common.request.IdReq;
import com.xinlian.common.request.RechargeCurrencyReq;
import com.xinlian.common.response.*;
import com.xinlian.biz.dao.TCurrencyInfoMapper;
import com.xinlian.biz.dao.TUserWithdrawAddressRefMapper;
import com.xinlian.biz.model.TCurrencyInfo;
import com.xinlian.biz.model.TUserWithdrawAddressRef;
import com.xinlian.member.biz.service.TUserWithdrawAddressRefService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 客户提币地址表 服务实现类
 * </p>
 *
 * @author idea
 * @since 2019-12-24
 */
@Service
public class TUserWithdrawAddressRefServiceImpl extends ServiceImpl<TUserWithdrawAddressRefMapper, TUserWithdrawAddressRef> implements TUserWithdrawAddressRefService {

    @Autowired
    private TUserWithdrawAddressRefMapper userWithdrawAddressRefMapper;

    @Autowired
    private TCurrencyInfoMapper currencyInfoMapper;

    @Autowired
    private TCurrencyManageMapper currencyManageMapper;

    @Override
    public TUserWithdrawAddressRef getByCriteria(TUserWithdrawAddressRef userWithdrawAddressRef){
        return userWithdrawAddressRefMapper.getByCriteria(userWithdrawAddressRef);
    }

    @Override
    public ResponseResult addAddressRef(AddWithdrawAddressReq req){
        ResponseResult result = new ResponseResult();
        result.setResult(new NullParam());
        TCurrencyInfo info = currencyInfoMapper.selectById(req.getCurrencyId());
        if(info == null){
            result.setMsg("币种不存在");
            return result;
        }
        TUserWithdrawAddressRef ref = new TUserWithdrawAddressRef();
        ref.setIsDel(0);
        ref.setToCurrencyAddress(req.getCurrencyAddress());
        ref.setUid(req.getUid());
        ref.setCreateTime(new Date());
        ref.setToCurrencyCode(info.getCurrencyCode());
        ref.setToCurrencyId(info.getId());
        ref.setToCurrencyName(info.getCurrencyName());
        ref.setToAddressName(req.getAddressName());
        userWithdrawAddressRefMapper.insert(ref);
        result.setCode(GlobalConstant.ResponseCode.SUCCESS);
        return result;
    }

    @Override
    public PageResult<List<WithdrawAddressRes>> withdrawAddressList(IdReq req){
        PageResult<List<WithdrawAddressRes>> result = new PageResult<>();
        EntityWrapper<TUserWithdrawAddressRef> wrapper = new EntityWrapper<>();
        wrapper.eq("uid", req.getUid());
        wrapper.eq("is_del", 0);
        result.setTotal(userWithdrawAddressRefMapper.selectCount(wrapper));
        wrapper.last("limit " + req.pickUpOffset() + "," + req.pickUpPageSize());
        List<TUserWithdrawAddressRef> list = userWithdrawAddressRefMapper.selectList(wrapper);
        List<WithdrawAddressRes> resList = new ArrayList<>(list.size());
        //币种
        List<TCurrencyInfo> currencyInfoList = currencyInfoMapper.selectList(new EntityWrapper<>());
        Map<Long, TCurrencyInfo> currencyMap = currencyInfoList.stream().collect(Collectors.toMap(TCurrencyInfo::getId, e->e));
        for(TUserWithdrawAddressRef info : list){
            WithdrawAddressRes res = info.withdrawAddressRes();
            if(currencyMap.get(res.getCoinId()) != null){
                res.setThumb(currencyMap.get(res.getCoinId()).getImgUrl());
            }
            resList.add(res);
        }
        result.setCurPage(req.pickUpCurPage());
        result.setPageSize(req.pickUpPageSize());
        result.setResult(resList);
        result.setCode(GlobalConstant.ResponseCode.SUCCESS);
        return result;
    }

    @Override
    public ResponseResult delWithdrawAddress(IdReq req){
        ResponseResult result = new ResponseResult();
        result.setCode(GlobalConstant.ResponseCode.FAIL);
        result.setResult(new NullParam());
        EntityWrapper<TUserWithdrawAddressRef> wrapper = new EntityWrapper<>();
        wrapper.eq("id", req.getId());
        wrapper.eq("uid", req.getUid());
        TUserWithdrawAddressRef update = new TUserWithdrawAddressRef();
        update.setIsDel(1);
        int count = userWithdrawAddressRefMapper.update(update, wrapper);
        if(count == 0){
            result.setMsg("删除失败");
            return result;
        }
        result.setCode(GlobalConstant.ResponseCode.SUCCESS);
        return result;
    }

    @Override
    public ResponseResult<List<CurrencyInfoRes>> findCurrencyInfoRes(){
        ResponseResult<List<CurrencyInfoRes>> result = new ResponseResult<>();
        List<TCurrencyInfo> list = currencyInfoMapper.selectList(new EntityWrapper<TCurrencyInfo>()
                .eq("status", 1));
        List<CurrencyInfoRes> resList = new ArrayList<>(list.size());
        list.stream().forEach(e -> {
            resList.add(e.currencyInfoRes());
        });
        result.setResult(resList);
        result.setCode(GlobalConstant.ResponseCode.SUCCESS);
        return result;
    }

    @Override
    public ResponseResult<List<CurrencyInfoRes>> findCurrencyInfoResNew(){
        ResponseResult<List<CurrencyInfoRes>> result = new ResponseResult<>();
        List<TCurrencyManage> list = currencyManageMapper.selectList(new EntityWrapper<TCurrencyManage>()
                .eq("status", 1));
        List<CurrencyInfoRes> resList = new ArrayList<>(list.size());
        list.stream().forEach(e -> {
            resList.add(e.currencyInfoRes());
        });
        result.setResult(resList);
        result.setCode(GlobalConstant.ResponseCode.SUCCESS);
        return result;
    }

    @Override
    public ResponseResult<List<CurrencyInfoRes>> rechargeCurrencyRes(RechargeCurrencyReq req){
        ResponseResult<List<CurrencyInfoRes>> result = new ResponseResult<>();
        EntityWrapper<TCurrencyManage> wrapper = new  EntityWrapper<>();
        wrapper.eq("status", 1);
        int type = req.getDelType() == null ? 0 : req.getDelType().intValue();
        if(type == 1){
            wrapper.eq("cash", 1);
        }else if(type == 2){
            wrapper.eq("recharge", 1);
        }
        List<TCurrencyManage> list = currencyManageMapper.selectList(wrapper);
        List<CurrencyInfoRes> resList = new ArrayList<>(list.size());
        list.stream().forEach(e -> {
            resList.add(e.currencyInfoRes());
        });
        result.setResult(resList);
        result.setCode(GlobalConstant.ResponseCode.SUCCESS);
        return result;
    }

}
