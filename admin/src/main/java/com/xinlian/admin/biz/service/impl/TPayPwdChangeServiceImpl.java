package com.xinlian.admin.biz.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.xinlian.admin.biz.service.TNewsArticleService;
import com.xinlian.admin.biz.service.TPayPwdChangeService;
import com.xinlian.biz.dao.TPayPwdChangeMapper;
import com.xinlian.biz.dao.TUserInfoMapper;
import com.xinlian.biz.model.TNewsArticle;
import com.xinlian.biz.model.TPayPwdChange;
import com.xinlian.biz.model.TUserAuthAppeal;
import com.xinlian.biz.model.TUserInfo;
import com.xinlian.common.contants.GlobalConstant;
import com.xinlian.common.dto.PayPwdChangeDto;
import com.xinlian.common.request.CheckUserAuthReq;
import com.xinlian.common.response.PageResult;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.utils.CommonUtil;
import org.assertj.core.util.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 * 支付密码变更表（用户手机号已不可用） 服务实现类
 * </p>
 *
 * @author lx
 * @since 2020-06-04
 */
@Service
public class TPayPwdChangeServiceImpl extends ServiceImpl<TPayPwdChangeMapper, TPayPwdChange> implements TPayPwdChangeService {
    @Autowired
    private TPayPwdChangeMapper tPayPwdChangeMapper;
    @Autowired
    private TNewsArticleService tNewsArticleService;
    @Autowired
    private TUserInfoMapper tUserInfoMapper;

    @Override
    public PageResult<List<PayPwdChangeDto>> queryList(CheckUserAuthReq checkUserAuthReq) {
        PageResult<List<PayPwdChangeDto>> result = new PageResult<>();
        result.setCurPage(checkUserAuthReq.pickUpCurPage());
        result.setPageSize(checkUserAuthReq.pickUpPageSize());
        result.setCode(GlobalConstant.ResponseCode.SUCCESS);
        EntityWrapper<TPayPwdChange> wrapper = new EntityWrapper<>();
        if (null != checkUserAuthReq.getUid() && !"".equals(String.valueOf(checkUserAuthReq.getUid()))) {
            wrapper.eq("uid",checkUserAuthReq.getUid());
        }
        if (null != checkUserAuthReq.getState() && !"".equals(String.valueOf(checkUserAuthReq.getState()))) {
            wrapper.eq("state",checkUserAuthReq.getState());
        }
        if (null != checkUserAuthReq.getUserName() && !"".equals(checkUserAuthReq.getUserName())) {
            wrapper.eq("user_name",checkUserAuthReq.getUserName());
        }
        result.setTotal(tPayPwdChangeMapper.selectCount(wrapper));
        List<TPayPwdChange> id = tPayPwdChangeMapper.selectPage(new Page<TPayPwdChange>((int) checkUserAuthReq.pickUpCurPage(),
                (int) checkUserAuthReq.pickUpPageSize()), wrapper.orderBy("id", false));
        List<PayPwdChangeDto> list = Lists.newArrayList();
        id.forEach(tPayPwdChange -> {
            PayPwdChangeDto payPwdChangeDto = new PayPwdChangeDto();
            BeanUtils.copyProperties(tPayPwdChange,payPwdChangeDto);
            list.add(payPwdChangeDto);
        });

        result.setResult(list);
        return result;
    }

    @Override
    public ResponseResult passOrRefuse(TPayPwdChange tPayPwdChange) {
        //     * 审核状态0审核中 1审核通过 2审核驳回
        TNewsArticle tNewsArticle = new TNewsArticle();
        tNewsArticle.setTid(9);
        tNewsArticle.setUidS(String.valueOf(tPayPwdChange.getUid()));
        tNewsArticle.setTypeLanguage("CN");
        tNewsArticle.setStatus(1);
        tNewsArticle.setInputTime(System.currentTimeMillis()/1000);
        tNewsArticle.setLabel(1);
        TUserInfo dbResult = tUserInfoMapper.selectById(tPayPwdChange.getUid());
        TPayPwdChange res = tPayPwdChangeMapper.selectById(tPayPwdChange.getId());
        switch (tPayPwdChange.getState()){
            case 1:
                tNewsArticle.setTitle("申请已通过");
                dbResult.setPayPassWord(res.getPayPassword());
                tUserInfoMapper.updateById(dbResult);
                res.setState(1);
                break;
            case 2:
                tNewsArticle.setTitle(tPayPwdChange.getRemark());
                res.setState(2);
                break;
        }
        tNewsArticleService.insert(tNewsArticle);
        res.setRemark(tPayPwdChange.getRemark());
        updateById(res);
        return ResponseResult.ok();
    }
}
