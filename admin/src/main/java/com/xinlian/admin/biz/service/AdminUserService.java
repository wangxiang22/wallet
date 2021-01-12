package com.xinlian.admin.biz.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.xinlian.admin.biz.jwt.util.EncryptionUtil;
import com.xinlian.admin.biz.redis.RedisClient;
import com.xinlian.admin.biz.redis.RedisConstant;
import com.xinlian.admin.biz.service.base.PageBaseService;
import com.xinlian.biz.dao.AdminRoleMapper;
import com.xinlian.biz.dao.AdminUserMapper;
import com.xinlian.biz.dao.AdminUserRoleMapper;
import com.xinlian.biz.model.AdminRoleModel;
import com.xinlian.biz.model.AdminUser;
import com.xinlian.biz.model.AdminUserRoleRef;
import com.xinlian.common.contants.GlobalConstant;
import com.xinlian.common.enums.AdminRoleCodeEnum;
import com.xinlian.common.request.UpdateAdminUserReq;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.result.BizException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * com.xinlian.admin.biz.service
 *
 * @author by Song
 * @date 2020/2/22 21:17
 */
@Service
@Slf4j
public class AdminUserService extends PageBaseService<AdminUser> {

    @Autowired
    private AdminUserMapper adminUserMapper;
    @Autowired
    private AdminUserRoleMapper adminUserRoleMapper;
    @Autowired
    private AdminRoleMapper adminRoleMapper;
    @Autowired
    private RedisClient redisClient;

    /**
     * 删除系统用户
     * @param adminUserId 被删除系统用户id
     * @param loginAdminUserId 登录系统用户id
     * @return
     */
    public int deleteAdminUser(Long adminUserId,Long loginAdminUserId){
        List<AdminRoleModel> adminRoleModels = adminRoleMapper.getRoleByUserId(adminUserId);
        for(AdminRoleModel adminRoleModel : adminRoleModels){
            if(AdminRoleCodeEnum.ADMIN.getRoleCode().equals(adminRoleModel.getRoleCode())){
                throw new BizException("主管理员不能删除!");
            }
        }
        //修改密码就需要删除登录凭证，让登录请求操作不了
        redisClient.deleteByKey(RedisConstant.REDIS_KEY_SESSION_USERID + adminUserId);
        //特定得不要
        AdminUser updateUser = new AdminUser();
        updateUser.setId(adminUserId);
        updateUser.setStatus(2);//2 - 逻辑删除
        updateUser.setUpdater(loginAdminUserId);
        updateUser.setUpdateTime(new Date());
        return adminUserMapper.updateById(updateUser);
    }

    @Override
    public List<AdminUser> query(AdminUser model) throws Exception {
        return adminUserMapper.query(model);
    }

    public ResponseResult updateAdminUser(UpdateAdminUserReq req){
        ResponseResult result = new ResponseResult();
        result.setCode(GlobalConstant.ResponseCode.SUCCESS);
        AdminUser user = adminUserMapper.selectById(req.getUpdateAdminUserId());
        AdminUser updateUser = new AdminUser();
        updateUser.setId(user.getId());
        if(StringUtils.isNotBlank(req.getRealName())){
            updateUser.setRealName(req.getRealName());
        }
        if(StringUtils.isNotBlank(req.getUsername())){
            updateUser.setUsername(req.getUsername());
        }
        if(StringUtils.isNotBlank(req.getPassword())){
            updateUser.setPassword(EncryptionUtil.md5Two(req.getPassword(), user.getSalt()));
            //修改密码就需要删除登录凭证，让登录请求操作不了
            redisClient.deleteByKey(RedisConstant.REDIS_KEY_SESSION_USERID + req.getUpdateAdminUserId());
        }
        updateUser.setUpdater(req.getLoginUserId());
        updateUser.setUpdateTime(new Date());
        updateUser.setAccountDesc(req.getAccountDesc());
        adminUserMapper.updateById(updateUser);
        return result;
    }

    @Transactional
    public ResponseResult createAdminUser(UpdateAdminUserReq req){
        ResponseResult result = new ResponseResult();
        result.setCode(GlobalConstant.ResponseCode.FAIL);
        int count = adminUserMapper.selectCount(new EntityWrapper<AdminUser>().eq("username", req.getUsername()));
        if(count > 0){
            result.setMsg("用户名已存在");
            return result;
        }
        AdminUser user = new AdminUser();
        user.setRealName(req.getRealName());
        user.setAccountDesc(req.getAccountDesc());
        user.setCreater(req.getLoginUserId());
        user.setCreateTime(new Date());
        user.setUsername(req.getUsername());
        String salt = EncryptionUtil.getSalt();
        user.setSalt(salt);
        user.setPassword(EncryptionUtil.md5Two(req.getPassword(), salt));
        user.setStatus(1);
        int resultNum = adminUserMapper.insert(user);
        //系统用户与角色对应关系表
        if(resultNum==1) {
            AdminUserRoleRef adminUserRoleRef = new AdminUserRoleRef();
            adminUserRoleRef.setUserId(user.getId());
            adminUserRoleRef.setRoleId(req.getRoleId());
            int adminRoleRefResultNum = adminUserRoleMapper.insert(adminUserRoleRef);
            if(resultNum != 1 || adminRoleRefResultNum != 1){
                throw new BizException("新增应用用户出现异常，请稍候重试!");
            }
        }
        result.setCode(GlobalConstant.ResponseCode.SUCCESS);
        return result;
    }
}
