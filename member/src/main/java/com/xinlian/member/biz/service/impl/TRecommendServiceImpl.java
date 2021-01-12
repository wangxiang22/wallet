package com.xinlian.member.biz.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.xinlian.biz.dao.TRecommendMapper;
import com.xinlian.biz.dao.TServerNodeMapper;
import com.xinlian.biz.model.TRecommend;
import com.xinlian.biz.model.TServerNode;
import com.xinlian.common.contants.GlobalConstant;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.member.biz.service.TRecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wx
 * @since 2020-03-25
 */
@Service
public class TRecommendServiceImpl extends ServiceImpl<TRecommendMapper, TRecommend> implements TRecommendService {

    @Autowired
    private TRecommendMapper tRecommendMapper;
    @Autowired
    private TServerNodeMapper serverNodeMapper;

    @Override
    public ResponseResult<List<TRecommend>> queryRecommend(Long nodeId) {
        ResponseResult<List<TRecommend>> result = new ResponseResult<>();
        List<TRecommend> tRecommends = tRecommendMapper.selectList(new EntityWrapper<TRecommend>().orderDesc(Collections.singletonList("sort")));
        if (null == tRecommends || tRecommends.size() == 0){
            result.setMsg("请求成功，暂无数据");
            result.setCode(GlobalConstant.ResponseCode.SUCCESS);
            return result;
        }
        TServerNode node = serverNodeMapper.selectById(nodeId);
        //0：不可以智能合约，1：可以智能合约
        if (null != node && 0 == node.getSmartContractsStatus()) {
            tRecommends.removeIf(recommend -> "智能合约".equals(recommend.getTitle()));
        }
        result.setMsg("请求成功");
        result.setCode(GlobalConstant.ResponseCode.SUCCESS);
        result.setResult(tRecommends);
        return result;
    }

}
