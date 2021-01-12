//package com.xinlian.member.server.controller;
//
//
//import com.xinlian.biz.utils.AdminOptionsUtil;
//import com.xinlian.common.dto.LotteryDrawDto;
//import com.xinlian.common.enums.AdminOptionsBelongsSystemCodeEnum;
//import com.xinlian.common.request.LotteryDrawReq;
//import com.xinlian.common.response.ResponseResult;
//import com.xinlian.common.result.BizException;
//import com.xinlian.member.biz.jwt.annotate.PassToken;
//import com.xinlian.member.biz.jwt.util.JwtUtil;
//import com.xinlian.member.biz.service.LotteryDrawPrizerService;
//import com.xinlian.member.biz.service.LotteryDrawService;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpServletRequest;
//
//import static com.xinlian.common.contants.LotteryDrawConstant.TIME_ERROR;
//
///**
// * <p>
// *  前端控制器
// * </p>
// *
// * @author lx
// * @since 2020-06-05
// */
//@RestController
//@RequestMapping("/lotteryDraw")
//@Api("抽奖")
//@Slf4j
//public class LotteryDrawController {
//    @Autowired
//    private JwtUtil jwtUtil;
//    @Autowired
//    private LotteryDrawService lotteryDrawService;
//    @Autowired
//    private LotteryDrawPrizerService lotteryDrawPrizerService;
//    @Autowired
//    private AdminOptionsUtil adminOptionsUtil;
//
//
//    @PostMapping("lotteryDraw")
//    @ApiOperation("抽奖")
//    public ResponseResult lotteryDraw(@RequestBody LotteryDrawReq lotteryDrawReq,HttpServletRequest httpServletRequest){
//        lotteryDrawReq.setUid(jwtUtil.getUserId(httpServletRequest)); //todo 测试先注了
//        log.info("开始抽奖用户名:{},uid:{}",lotteryDrawReq.getUsername(),lotteryDrawReq.getUid());
//        return lotteryDrawService.lotteryDraw(lotteryDrawReq);
//    }
//
//    @GetMapping("findLotteryDrawState")
//    @ApiOperation("查询抽奖状态")
//    public ResponseResult findLotteryDrawState(@RequestParam Long uid){
////        Long uid = jwtUtil.getUserId(httpServletRequest);
//        return lotteryDrawService.findLotteryDrawState(uid);
//    }
//
//    @ApiOperation("抽中奖的人")
//    @GetMapping("runningHorseLight")
//    public ResponseResult runningHorseLight(){
//        return lotteryDrawPrizerService.runningHorseLight();
//    }
//
//    @ApiOperation("塞假数据入库")
//    @GetMapping("insert")
//    @PassToken
//    public ResponseResult insert(){
//        return lotteryDrawPrizerService.insert10();
//    }
//
//    @ApiOperation("查询抽奖是否开启")
//    @GetMapping("queryDrawIsOpen")
//    public ResponseResult queryDrawIsOpen(){
//        LotteryDrawDto bootScreenRes = new LotteryDrawDto();
//        try {
//            bootScreenRes = adminOptionsUtil.fieldEntityObject(AdminOptionsBelongsSystemCodeEnum.LOTTERY_DRAW.getBelongsSystemCode(), LotteryDrawDto.class);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        if (System.currentTimeMillis() < Long.parseLong(bootScreenRes.getLdStartTime())||System.currentTimeMillis()>Long.parseLong(bootScreenRes.getLdEndTime())) {
//            return ResponseResult.ok(false);
//        }
//        return ResponseResult.ok(true);
//    }
//
//}
//
