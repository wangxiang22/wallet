package com.xinlian.admin.server.controller;

import com.github.pagehelper.PageInfo;
import com.xinlian.admin.biz.jwt.util.JwtUtil;
import com.xinlian.admin.biz.redis.RedisClient;
import com.xinlian.admin.biz.service.AdminUserService;
import com.xinlian.admin.server.operationLog.OpeAnnotation;
import com.xinlian.admin.server.vo.AdminUserVo;
import com.xinlian.admin.server.vo.AdminUserVoConvertor;
import com.xinlian.admin.server.vo.UserLoginSession;
import com.xinlian.biz.model.AdminMenuModel;
import com.xinlian.common.enums.OperationModuleEnum;
import com.xinlian.common.enums.OperationTypeEnum;
import com.xinlian.common.request.UpdateAdminUserReq;
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

@Api(value = "系统管理")
@RestController
@RequestMapping("/system")
@Slf4j
public class AdminUserController {

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 获取当前登录用户对应菜单
     * @param request
     * @return
     */
    @ApiOperation(value = "查询当前登录用户对应菜单")
    @GetMapping("/v1/users/menus")
    public ResponseResult queryUserMenus(HttpServletRequest request){
        try{
            //获取当前请求用户
            UserLoginSession userLoginSession = jwtUtil.getUserLoginSession(request);
            String ticketMenu = userLoginSession.getTicketMenu();
            List<AdminMenuModel> menuJson = redisClient.get(ticketMenu);
            return new ResponseResult(menuJson);
        }catch(Exception e){
            log.error("获取登录对应菜单出现异常:{}",e.toString(),e);
            return new ResponseResult(new BizException(ErrorInfoEnum.FAILED));
        }
    }

    @OpeAnnotation(modelName = OperationModuleEnum.SYSTEM_MANAGE,typeName = OperationTypeEnum.OTHER_OPERATE, opeDesc = "添加系统用户")
    @ApiOperation(value = "添加系统用户", httpMethod = "POST")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult createAdminUser(HttpServletRequest request,
                                          @ApiParam(name = "req",required = true,value = UpdateAdminUserReq.Params)
                                          @RequestBody UpdateAdminUserReq req){
        req.setLoginUserId(jwtUtil.getUserId(request));
        return adminUserService.createAdminUser(req);
    }

    @OpeAnnotation(modelName = OperationModuleEnum.SYSTEM_MANAGE,typeName = OperationTypeEnum.OTHER_OPERATE, opeDesc = "修改系统用户")
    @ApiOperation(value = "修改系统用户", httpMethod = "POST")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult updateAdminUser(HttpServletRequest request,
                                          @ApiParam(name = "req",required = true,value = UpdateAdminUserReq.Params)
                                          @RequestBody UpdateAdminUserReq req){
        try {
            req.setLoginUserId(jwtUtil.getUserId(request));
            return adminUserService.updateAdminUser(req);
        }catch (BizException e){
            return new ResponseResult(e);
        }catch (Exception e){
            return new ResponseResult(new BizException(ErrorInfoEnum.FAILED));
        }
    }

    @OpeAnnotation(modelName = OperationModuleEnum.SYSTEM_MANAGE,typeName = OperationTypeEnum.OTHER_OPERATE, opeDesc = "删除系统用户")
    @ApiOperation(value = "删除系统用户", httpMethod = "POST")
    @RequestMapping(value = "/delete/{adminUserId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult deleteAdminUser(HttpServletRequest request,
                                          @ApiParam(name = "adminUserId",required = true,value = "系统用户列表主键id")
                                          @PathVariable Long adminUserId){
        try {
            int resultNum = adminUserService.deleteAdminUser(adminUserId, jwtUtil.getUserId(request));
            if (resultNum == 1) {
                return new ResponseResult(new BizException(ErrorInfoEnum.SUCCESS));
            }
            return new ResponseResult(new BizException(ErrorInfoEnum.FAILED));
        }catch (BizException e){
            return new ResponseResult(e);
        }catch (Exception e){
            return new ResponseResult(new BizException(ErrorInfoEnum.FAILED));
        }
    }

    @ApiOperation(value = "获取系统用户列表")
    @GetMapping(value = "/getAdminUsers")
    @ResponseBody
    public ResponseResultPage getAdminUser(HttpServletRequest request,
                                           @RequestParam Map<String,Object> searchParams){
        try {
            searchParams.put("status","1");
            PageInfo pageInfo = adminUserService.queryPage(searchParams);
            List<AdminUserVo> lists = new AdminUserVoConvertor().convertList(pageInfo.getList());
            pageInfo.setList(lists);
            return new ResponseResultPage(pageInfo);
        }catch (Exception e){
            log.error("获取系统用户列表出现异常:{}",e.toString(),e);
            return new ResponseResultPage(false);
        }
    }



}
