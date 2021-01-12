package com.xinlian.admin.server.controller;


import com.xinlian.admin.biz.service.OlddataDrMemberService;
import com.xinlian.common.response.QueryAddress;
import com.xinlian.common.response.ResponseResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *
 * </p>
 *
 * @author WX
 * @since 2020-05-16
 */
@RestController
@RequestMapping("/olddataDrMember")
public class OlddataDrMemberController {

@Autowired
private OlddataDrMemberService olddataDrMemberService;


    @ApiOperation("查询地址")
    @PostMapping("searchAddress")
     public ResponseResult searchAddress(@RequestBody QueryAddress queryAddress){
        return olddataDrMemberService.searchAddress(queryAddress);
    }

}

