package com.xinlian.admin.server.controller;

import com.xinlian.admin.server.controller.handler.QueryUserLevelHandler;
import com.xinlian.biz.model.next.NextUserInfoModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletResponse;

/**
 * @author lt
 * @date 2020/09/23
 **/
@Slf4j
@Api(value = "userNextLevel")
@RestController
@RequestMapping("/userNextLevel")
public class QueryUserLevelController {

    @Autowired
    private QueryUserLevelHandler queryUserLevelHandler;


    @ApiOperation(value = "userNextLevel",httpMethod = "POST")
    @PostMapping("/export")
    public void userNextLevel(@RequestBody NextUserInfoModel nextUserInfoModel, HttpServletResponse response) {
        try {
            //获取数据
            queryUserLevelHandler.getFirst(nextUserInfoModel);
        }catch (Exception e){
            log.error("出现异常:{}",e.toString(),e);
        }
    }


}
