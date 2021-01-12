package com.xinlian.admin.biz.service;

import com.xinlian.admin.biz.redis.RedisClient;
import com.xinlian.admin.biz.service.base.PageBaseService;
import com.xinlian.admin.biz.service.base.SequenceService;
import com.xinlian.biz.dao.AdminMenuMapper;
import com.xinlian.biz.dao.AdminRoleMapper;
import com.xinlian.biz.model.AdminRoleMenuRef;
import com.xinlian.biz.model.AdminRoleModel;
import com.xinlian.common.request.AdminRoleRequest;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * com.xinlian.admin.biz.service
 *
 * @author by Song
 * @date 2020/2/22 15:35
 */
@Service
public class AdminRoleService extends PageBaseService<AdminRoleModel> {

    @Autowired
    private AdminRoleMapper adminRoleMapper;
    @Autowired
    private AdminMenuMapper adminMenuMapper;
    @Autowired
    private SequenceService sequenceService;
    @Autowired
    private RedisClient redisClient;

    public AdminRoleModel getModelById(Long adminRoleId){
        String roleKey = "ADMIN_ROLE_KEY_"+adminRoleId;
        AdminRoleModel getRole = redisClient.get(roleKey);
        if(null==getRole){
            getRole = adminRoleMapper.getById(adminRoleId);
            redisClient.set(roleKey,getRole);
        }
        return getRole;
    }

    @Override
    public List<AdminRoleModel> query(AdminRoleModel model) throws Exception {
        return adminRoleMapper.query(model);
    }

    public List<AdminRoleModel> queryAllList(){
        return adminRoleMapper.query(null);
    }

    @Transactional
    public void addRoleAndPutMenu(AdminRoleRequest adminRoleRequest, List<Long> menuIds,String creator) {
        AdminRoleModel adminRoleModel = new AdminRoleModel();
        adminRoleModel.setRoleCode(sequenceService.getBizOrderNo("R",4));
        adminRoleModel.setRoleName(adminRoleRequest.getRoleName());
        adminRoleModel.setRoleStatus("O");
        adminRoleModel.setRoleExplain(adminRoleRequest.getRoleExplain());
        adminRoleMapper.insert(adminRoleModel);
        //插入关系表
        List<AdminRoleMenuRef> refList = new ArrayList<>(menuIds.size());
        for(Long menuId: menuIds){
            AdminRoleMenuRef ref = new AdminRoleMenuRef();
            ref.setRoleId(adminRoleModel.getId());
            ref.setMenuId(menuId);
            ref.setCreator(creator);
            refList.add(ref);
        }
        if(CollectionUtils.isNotEmpty(refList)) {
            adminMenuMapper.batchInsertRoleMenu(refList);
        }
    }

    @Transactional
    public void updateRoleAndMenu(Long adminRoleId, List<Long> menuIds,String creator) {
        //删除之前得关系，赋予现在得关系
        adminRoleMapper.deleteRoleMenuId(adminRoleId);
        //插入关系表
        List<AdminRoleMenuRef> refList = new ArrayList<>(menuIds.size());
        for(Long menuId: menuIds){
            AdminRoleMenuRef ref = new AdminRoleMenuRef();
            ref.setRoleId(adminRoleId);
            ref.setMenuId(menuId);
            ref.setCreator(creator);
            refList.add(ref);
        }
        if(CollectionUtils.isNotEmpty(refList)) {
            adminMenuMapper.batchInsertRoleMenu(refList);
        }
    }

    @Transactional
    public void deleteRoleAndMenu(Long adminRoleId) {
        adminRoleMapper.delete(adminRoleId);
        //删除之前得关系
        adminRoleMapper.deleteRoleMenuId(adminRoleId);
    }
}
