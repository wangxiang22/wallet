package com.xinlian.admin.biz.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.xinlian.admin.biz.redis.RedisClient;
import com.xinlian.admin.biz.redis.RedisConstant;
import com.xinlian.admin.biz.service.AdminOptionsService;
import com.xinlian.biz.dao.AdminOptionsMapper;
import com.xinlian.biz.model.AdminOptions;
import com.xinlian.common.contants.GlobalConstant;
import com.xinlian.common.enums.AdminOptionsBelongsSystemCodeEnum;
import com.xinlian.common.request.AdminOptionsReq;
import com.xinlian.common.request.PageReq;
import com.xinlian.common.response.PageResult;
import com.xinlian.common.response.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AdminOptionsServiceImpl implements AdminOptionsService {

    @Autowired
    private AdminOptionsMapper adminOptionsMapper;
    @Autowired
    private RedisClient redisClient;

    @Override
    public PageResult<List<AdminOptions>> findOptionsListPage(PageReq pageReq) {
        PageResult<List<AdminOptions>> result = new PageResult<>();
        result.setTotal(adminOptionsMapper.selectCount(null));
        result.setCurPage(pageReq.pickUpCurPage());
        result.setPageSize(pageReq.pickUpPageSize());
        result.setCode(GlobalConstant.ResponseCode.SUCCESS);
        result.setResult(adminOptionsMapper.selectPage(new Page<AdminOptions>((int) pageReq.pickUpCurPage(),(int) pageReq.pickUpPageSize()),null));
        return result;
    }

    @Override
    public ResponseResult findOptionById(AdminOptionsReq adminOptionsReq) {
        AdminOptions adminOptions = adminOptionsMapper.selectById(adminOptionsReq.getId());
        if (null == adminOptions) {
            return ResponseResult.builder().code(GlobalConstant.ResponseCode.PARAM_ERROR).msg("请求参数不合法").build();
        }
        return ResponseResult.builder().code(GlobalConstant.ResponseCode.SUCCESS).result(adminOptions).build();
    }

    @Override
    public ResponseResult createOption(AdminOptions adminOptions) {
        Integer insertResult = adminOptionsMapper.insert(adminOptions);
        if (0 == insertResult) {
            return ResponseResult.builder().code(GlobalConstant.ResponseCode.PARAM_ERROR).msg("新增配置项失败").build();
        }
        return ResponseResult.builder().code(GlobalConstant.ResponseCode.SUCCESS).msg("新增配置项成功").build();
    }

    @Override
    public ResponseResult updateOption(AdminOptions adminOptions) {
        Integer updateResult = adminOptionsMapper.updateById(adminOptions);
        if (0 == updateResult) {
            return ResponseResult.builder().code(GlobalConstant.ResponseCode.PARAM_ERROR).msg("修改配置项失败").build();
        }
        //删除缓存
        for (String key : redisClient.getKeys(RedisConstant.APP_REDIS_PREFIX + "MOBILE_*")) {
            redisClient.deleteByKey(key);
        }
        redisClient.deleteByKey(RedisConstant.APP_REDIS_PREFIX + "EMAIL_SEND_NUM");
        redisClient.deleteByKey(RedisConstant.APP_REDIS_PREFIX + "EMAIL_IN_THE_TIME");
        return ResponseResult.builder().code(GlobalConstant.ResponseCode.SUCCESS).msg("修改配置项成功").build();
    }

    @Override
    public ResponseResult deleteOption(AdminOptionsReq adminOptionsReq) {
        Integer deleteResult = adminOptionsMapper.deleteById(adminOptionsReq.getId());
        if (0 == deleteResult) {
            return ResponseResult.builder().code(GlobalConstant.ResponseCode.PARAM_ERROR).msg("删除配置项失败").build();
        }
        //删除缓存
        for (String key : redisClient.getKeys(RedisConstant.APP_REDIS_PREFIX + "MOBILE_*")) {
            redisClient.deleteByKey(key);
        }
        redisClient.deleteByKey(RedisConstant.APP_REDIS_PREFIX + "EMAIL_SEND_NUM");
        redisClient.deleteByKey(RedisConstant.APP_REDIS_PREFIX + "EMAIL_IN_THE_TIME");
        return ResponseResult.builder().code(GlobalConstant.ResponseCode.SUCCESS).msg("删除配置项成功").build();
    }

    @Override
    public List<AdminOptions> queryByBelongsSystemCode(AdminOptions adminOptions) {
        return adminOptionsMapper.queryByBelongsSystemCode(adminOptions);
    }

    @Override
    public void batchUpdateModel(List<AdminOptions> adminOptions){
        Long resultNum = adminOptionsMapper.batchUpdateModel(adminOptions);
    }

    /**
     * @see com.xinlian.common.enums.AdminOptionsBelongsSystemCodeEnum#BASIC_CONFIG
     * @param belongsSystemCodeValue
     * @param adminOptionsList
     */
    @Override
    public void removeRedisCache(String belongsSystemCodeValue, List<AdminOptions> adminOptionsList){
        if(AdminOptionsBelongsSystemCodeEnum.BASIC_CONFIG.getBelongsSystemCode().equals(belongsSystemCodeValue)){
            //兼容之前的写法，取缓存拿缓存
            for (AdminOptions model: adminOptionsList) {
                redisClient.deleteByKey(model.getOptionName());
            }
        }else{//另外一种清理缓存方式，因里面的内容是多个
            redisClient.deleteByKey(RedisConstant.REDIS_KEY_SESSION_ADMIN_OPTION+belongsSystemCodeValue);
        }
    }


}
