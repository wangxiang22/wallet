//package com.xinlian.admin.server.controller;
//
//import com.xinlian.admin.biz.jwt.util.JwtUtil;
//import com.xinlian.admin.biz.service.AdminMenuLabelService;
//import com.xinlian.admin.biz.service.AdminRoleService;
//import com.xinlian.admin.server.operationLog.OpeAnnotation;
//import com.xinlian.admin.server.vo.AdminPermissionVo;
//import com.xinlian.admin.server.vo.AdminPermissionVoConvertor;
//import com.xinlian.biz.model.AdminPermissionModel;
//import com.xinlian.common.enums.AdminRoleCodeEnum;
//import com.xinlian.common.enums.OperationModuleEnum;
//import com.xinlian.common.enums.OperationTypeEnum;
//import com.xinlian.common.request.AdminRoleRequest;
//import com.xinlian.common.response.ResponseResult;
//import com.xinlian.common.result.BizException;
//import com.xinlian.common.result.ErrorInfoEnum;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import io.swagger.annotations.ApiParam;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpServletRequest;
//import java.util.List;
//
///**
// * com.xinlian.admin.server.controller
// *
// * @author by Song
// * @date 2020/2/24 23:36
// */
//@Api(value = "系统管理-角色与按钮控制器")
//@RestController
//@RequestMapping("/system")
//@Slf4j
//public class AdminRoleLabelController {
//
//    @Autowired
//    private AdminMenuLabelService adminMenuLabelService;
//    @Autowired
//    private AdminRoleService adminRoleService;
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    @ApiOperation(value = "获取所有btn")
//    @GetMapping(value = "/v1/btn/all")
//    public ResponseResult getRoleBtn(HttpServletRequest request){
//        List lists = adminMenuLabelService.queryCheckedLabelsByRoles(null);
//        return new ResponseResult(lists);
//    }
//
//    @ApiOperation(value = "获取角色按钮列表",response = AdminPermissionVo.class)
//    @GetMapping(value = "/v1/btn/roles")
//    public ResponseResult getBtnRoles(HttpServletRequest request) {
//        try{
//            List<AdminPermissionModel> lists = adminMenuLabelService.queryRoleAndLabels();
//            List<AdminPermissionVo> vos = new AdminPermissionVoConvertor().convertList(lists);
//            return new ResponseResult(vos);
//        }catch (Exception e){
//            log.error("获取角色按钮列表出现异常:{}",e.toString(),e);
//            return new ResponseResult(new BizException("请稍候重试!"));
//        }
//    }
//
//    /**
//     * 新增角色 - 并赋予该角色对应得菜单目录权限
//     * @param request
//     * @return
//     */
//    @ApiOperation(value = "运营后台系统-新增角色接口")
//    @PostMapping("/v1/btn/addRoleLabel")
//    @OpeAnnotation(modelName = OperationModuleEnum.ADMIN_ROLE_MANAGE,typeName = OperationTypeEnum.OTHER_OPERATE, opeDesc = "新增角色便签接口")
//    public ResponseResult addRoleLabel(HttpServletRequest request,
//                                  @ApiParam(name = "adminRoleRequest",required = true,value = AdminRoleRequest.Params)
//                                  @RequestBody AdminRoleRequest adminRoleRequest,
//                                  @ApiParam(name = "labelIds",required = true,value = "按钮-id,按钮-id 被选中的按钮id")
//                                  @RequestParam List<Long> labelIds){
//        try{
//            String loginUserName = jwtUtil.getUserName(request);
//            adminMenuLabelService.addRoleAndPutLabel(adminRoleRequest,labelIds,loginUserName);
//            return new ResponseResult(new BizException(ErrorInfoEnum.SUCCESS));
//        }catch(Exception e){
//            log.error("获取运营后台新增角色出现异常:{}",e.toString(),e);
//            return new ResponseResult(new BizException(ErrorInfoEnum.FAILED));
//        }
//    }
//
//    /**
//     * 修改角色 - 并赋予该角色对应得菜单目录权限
//     * @param request
//     * @return
//     */
//    @ApiOperation(value = "运营后台系统-修改角色接口")
//    @PostMapping("/v1/btn/updateRoleLabel")
//    @OpeAnnotation(modelName = OperationModuleEnum.ADMIN_ROLE_MANAGE,typeName = OperationTypeEnum.OTHER_OPERATE, opeDesc = "修改角色标签接口")
//    public ResponseResult updateRoleLabel(HttpServletRequest request,
//                                     @ApiParam(name = "adminRoleId",required = true,value = "角色id")
//                                     @RequestParam Long adminRoleId,
//                                     @ApiParam(name = "labelIds",required = true,value = "按钮-id,按钮-id 被选中的按钮id")
//                                     @RequestParam List<Long> labelIds){
//        try{
//            if(AdminRoleCodeEnum.ADMIN.getRoleCode().equals(adminRoleService.getModelById(adminRoleId).getRoleCode())){
//                return new ResponseResult(new BizException("管理员账号不能修改"));
//            }
//            String loginUserName = jwtUtil.getUserName(request);
//            adminMenuLabelService.updateRoleAndLabel(adminRoleId,labelIds,loginUserName);
//            return new ResponseResult(new BizException(ErrorInfoEnum.SUCCESS));
//        }catch(Exception e){
//            log.error("获取运营后台修改角色出现异常:{}",e.toString(),e);
//            return new ResponseResult(new BizException(ErrorInfoEnum.FAILED));
//        }
//    }
//
//    /**
//     * 删除角色
//     * @param request
//     * @return
//     */
//    @ApiOperation(value = "运营后台系统-删除角色接口")
//    @PostMapping("/v1/btn/deleteRoleLabel")
//    @OpeAnnotation(modelName = OperationModuleEnum.ADMIN_ROLE_MANAGE,typeName = OperationTypeEnum.OTHER_OPERATE, opeDesc = "删除角色标签接口")
//    public ResponseResult deleteRoleLabel(HttpServletRequest request,
//                                          @ApiParam(name = "adminRoleId",required = true,value = "角色id")
//                                          @RequestParam Long adminRoleId){
//        try{
//            if(AdminRoleCodeEnum.ADMIN.getRoleCode().equals(adminRoleService.getModelById(adminRoleId).getRoleCode())){
//                return new ResponseResult(new BizException("管理员账号不能删除"));
//            }
//            adminMenuLabelService.deleteRoleAndLabel(adminRoleId);
//            return new ResponseResult(new BizException(ErrorInfoEnum.SUCCESS));
//        }catch(Exception e){
//            log.error("获取运营后台修改角色出现异常:{}",e.toString(),e);
//            return new ResponseResult(new BizException(ErrorInfoEnum.FAILED));
//        }
//    }
//}
