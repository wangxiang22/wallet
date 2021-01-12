package com.xinlian.admin.server.controller;

import com.github.pagehelper.PageInfo;
import com.xinlian.admin.biz.jwt.util.JwtUtil;
import com.xinlian.admin.biz.service.AdminRoleService;
import com.xinlian.admin.server.operationLog.OpeAnnotation;
import com.xinlian.admin.server.vo.AdminRoleVoConvertor;
import com.xinlian.biz.model.AdminRoleModel;
import com.xinlian.common.enums.AdminRoleCodeEnum;
import com.xinlian.common.enums.OperationModuleEnum;
import com.xinlian.common.enums.OperationTypeEnum;
import com.xinlian.common.request.AdminRoleRequest;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.response.ResponseResultPage;
import com.xinlian.common.result.BizException;
import com.xinlian.common.result.ErrorInfoEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * com.xinlian.admin.server.controller
 *
 * @author by Song
 * @date 2020/2/22 15:32
 */
@Api(value = "运营后台-系统管理-角色控制器")
@RestController
@RequestMapping("/system")
@Slf4j
public class AdminRoleController {

    @Autowired
    private AdminRoleService adminRoleService;
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 获取运营后台所有角色列表
     * @param request
     * @return
     */
    @ApiOperation(value = "获取运营后台所有角色列表")
    @GetMapping("/v1/roles")
    public ResponseResult queryAllRoles(HttpServletRequest request){
        try{
            List<AdminRoleModel> lists = adminRoleService.queryAllList();
            return new ResponseResult(new AdminRoleVoConvertor().convertList(lists));
        }catch(Exception e){
            log.error("获取运营后台所有角色列表出现异常:{}",e.toString(),e);
            return new ResponseResult(new BizException(ErrorInfoEnum.FAILED));
        }
    }


    /**
     * 获取运营后台所有角色列表 - 分页
     * @param request request
     * @return ResponseResultPage
     */
    @ApiOperation(value = "分页获取运营后台所有角色列表")
    @GetMapping("/v1/pageRoles")
    public ResponseResultPage queryUserMenus(HttpServletRequest request,
                                             @RequestParam Map<String,Object> searchParams){
        try{
            PageInfo pageInfo = adminRoleService.queryPage(searchParams);
            return new ResponseResultPage(pageInfo);
        }catch(Exception e){
            log.error("获取运营后台所有角色列表出现异常:{}",e.toString(),e);
            return new ResponseResultPage(false);
        }
    }

    /**
     * 新增角色 - 并赋予该角色对应得菜单目录权限
     * @param request request
     * @return ResponseResult
     */
    @ApiOperation(value = "运营后台系统-新增角色接口")
    @PostMapping("/v1/role/add")
    @OpeAnnotation(modelName = OperationModuleEnum.ADMIN_ROLE_MANAGE,typeName = OperationTypeEnum.OTHER_OPERATE, opeDesc = "新增角色接口")
    public ResponseResult addRole(HttpServletRequest request,
                                  @ApiParam(name = "adminRoleRequest",required = true,value = AdminRoleRequest.Params)
                                  @RequestBody AdminRoleRequest adminRoleRequest,
                                  @ApiParam(name = "menuIds",required = true,value = "菜单A-id,菜单B-id")
                                  @RequestParam List<Long> menuIds){
        try{
            String loginUserName = jwtUtil.getUserName(request);
            adminRoleService.addRoleAndPutMenu(adminRoleRequest,menuIds,loginUserName);
            return new ResponseResult(new BizException(ErrorInfoEnum.SUCCESS));
        }catch(Exception e){
            log.error("获取运营后台新增角色出现异常:{}",e.toString(),e);
            return new ResponseResult(new BizException(ErrorInfoEnum.FAILED));
        }
    }

    /**
     * 修改角色 - 并赋予该角色对应得菜单目录权限
     * @param request httpServletRequest
     * @param adminRoleId  角色id
     * @param menuIds 菜单目录ids
     * @return ResponseResult
     */
    @ApiOperation(value = "运营后台系统-修改角色接口")
    @PostMapping("/v1/role/update")
    @OpeAnnotation(modelName = OperationModuleEnum.ADMIN_ROLE_MANAGE,typeName = OperationTypeEnum.OTHER_OPERATE, opeDesc = "修改角色接口")
    public ResponseResult updateRole(HttpServletRequest request,
                                  @ApiParam(name = "adminRoleId",required = true,value = "角色id")
                                  @RequestParam Long adminRoleId,
                                  @ApiParam(name = "menuIds",required = true,value = "菜单A-id,菜单B-id")
                                  @RequestParam List<Long> menuIds){
        try{
            if(AdminRoleCodeEnum.ADMIN.getRoleCode().equals(adminRoleService.getModelById(adminRoleId).getRoleCode())){
                return new ResponseResult(new BizException("管理员账号不能修改"));
            }
            String loginUserName = jwtUtil.getUserName(request);
            adminRoleService.updateRoleAndMenu(adminRoleId,menuIds,loginUserName);
            return new ResponseResult(new BizException(ErrorInfoEnum.SUCCESS));
        }catch(Exception e){
            log.error("获取运营后台修改角色出现异常:{}",e.toString(),e);
            return new ResponseResult(new BizException(ErrorInfoEnum.FAILED));
        }
    }

    /**
     * 删除角色 - 并赋予该角色对应得菜单目录权限
     * @param request
     * @return ResponseResult
     */
    @OpeAnnotation(modelName = OperationModuleEnum.ADMIN_ROLE_MANAGE,typeName = OperationTypeEnum.OTHER_OPERATE, opeDesc = "删除角色接口")
    @ApiOperation(value = "运营后台系统-删除角色接口")
    @GetMapping("/v1/role/delete")
    public ResponseResult deleteRole(HttpServletRequest request,
                                     @ApiParam(name = "adminRoleId",required = true,value = "角色id")
                                     @RequestParam Long adminRoleId){
        try{
            if(AdminRoleCodeEnum.ADMIN.getRoleCode().equals(adminRoleService.getModelById(adminRoleId).getRoleCode())){
                return new ResponseResult(new BizException("管理员账号不能删除"));
            }
            adminRoleService.deleteRoleAndMenu(adminRoleId);
            return new ResponseResult(new BizException(ErrorInfoEnum.SUCCESS));
        }catch(Exception e){
            log.error("获取运营后台新增角色出现异常:{}",e.toString(),e);
            return new ResponseResult(new BizException(ErrorInfoEnum.FAILED));
        }
    }
}
