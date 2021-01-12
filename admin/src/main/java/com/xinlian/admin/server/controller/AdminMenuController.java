package com.xinlian.admin.server.controller;

import com.xinlian.admin.biz.service.AdminMenuService;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.result.BizException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * com.xinlian.admin.server.controller
 *
 * @author by Song
 * @date 2020/2/27 16:06
 */
@Slf4j
@Api(value = "系统管理-角色与按钮控制器")
@RestController
@RequestMapping("/system")
public class AdminMenuController {

    @Autowired
    private AdminMenuService adminMenuService;

    /**
     *根据角色获取所有菜单，角色下菜单isChecked=true
     * @param params
     * @return
     */
    @ApiOperation(value = "根据角色id获取对应所有菜单，选中checked:true,未选择checked:false，传-1查询获取所有menu")
    @GetMapping("/v1/menus/all")
    public ResponseResult queryMenu(
            @ApiParam(name = "params",value = "url拼接请求，roleId必传")
            @RequestParam Map<String,Object> params){
        try{
            Object roleId = params.get("roleId");
            if(null==roleId){
                throw new BizException("角色ID不能为空!");
            }
            return new ResponseResult(adminMenuService.queryMenusByRoleId(params));
        }catch (BizException e){
            return new ResponseResult(e);
        }catch (Exception e){
            log.error("根据角色获取所有菜单异常:{}",e.toString(),e);
            return new ResponseResult(new BizException("系统出现异常,请稍候重试!"));
        }
    }
}
