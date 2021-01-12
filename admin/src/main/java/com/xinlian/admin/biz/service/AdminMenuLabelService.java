package com.xinlian.admin.biz.service;

import com.xinlian.admin.biz.service.base.SequenceService;
import com.xinlian.biz.dao.AdminMenuMapper;
import com.xinlian.biz.dao.AdminRoleMapper;
import com.xinlian.biz.model.AdminMenuLabelModel;
import com.xinlian.biz.model.AdminPermissionModel;
import com.xinlian.biz.model.AdminRoleLabelRef;
import com.xinlian.biz.model.AdminRoleModel;
import com.xinlian.common.request.AdminRoleRequest;
import lombok.extern.slf4j.Slf4j;
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
 * @date 2020/2/25 11:49
 */
@Service
@Slf4j
public class AdminMenuLabelService {

    @Autowired
    private AdminMenuMapper adminMenuMapper;

    @Autowired
    private SequenceService sequenceService;

    @Autowired
    private AdminRoleMapper adminRoleMapper;

    public List<AdminMenuLabelModel> queryCheckedLabelsByRoles(List<AdminRoleModel> roleLists){
        return adminMenuMapper.queryAllLabel(roleLists);
    }

    @Transactional
    public void addRoleAndPutLabel(AdminRoleRequest adminRoleRequest, List<Long> labelIds, String loginUserName) {
        AdminRoleModel adminRoleModel = new AdminRoleModel();
        adminRoleModel.setRoleCode(sequenceService.getBizOrderNo("R",4));
        adminRoleModel.setRoleName(adminRoleRequest.getRoleName());
        adminRoleModel.setRoleStatus("O");
        adminRoleMapper.insert(adminRoleModel);
        //插入关系表
        List<AdminRoleLabelRef> refList = new ArrayList<>(labelIds.size());
        for(Long labelId: labelIds){
            AdminRoleLabelRef ref = new AdminRoleLabelRef();
            ref.setRoleId(adminRoleModel.getId());
            ref.setPermId(labelId);
            ref.setCreator(loginUserName);
            refList.add(ref);
        }
        if(CollectionUtils.isNotEmpty(refList)) {
            adminMenuMapper.batchInsertRoleLabels(refList);
        }
    }

    private List<AdminRoleLabelRef> packageAdminRoleLabels(Long adminRoleId, List<Long> labelIds, String loginUserName){
        List<AdminRoleLabelRef> refList = new ArrayList<>(labelIds.size());
        for(Long labelId: labelIds){
            AdminRoleLabelRef ref = new AdminRoleLabelRef();
            ref.setRoleId(adminRoleId);
            ref.setRoleId(labelId);
            ref.setCreator(loginUserName);
            refList.add(ref);
        }
        return refList;
    }

    @Transactional
    public void updateRoleAndLabel(Long adminRoleId, List<Long> labelIds, String loginUserName) {
        //删除之前得关系，赋予现在得关系
        adminMenuMapper.deleteRoleLabelId(adminRoleId);
        //插入关系表
        List<AdminRoleLabelRef> refList = this.packageAdminRoleLabels(adminRoleId,labelIds,loginUserName);
        if(CollectionUtils.isNotEmpty(refList)) {
            adminMenuMapper.batchInsertRoleLabels(refList);
        }
    }

    @Transactional
    public void deleteRoleAndLabel(Long adminRoleId) {
        adminMenuMapper.deleteRoleLabelId(adminRoleId);
        adminRoleMapper.delete(adminRoleId);
    }

    public List<AdminPermissionModel> queryRoleAndLabels() {
        return adminMenuMapper.queryRoleAndLabels();
    }
}
