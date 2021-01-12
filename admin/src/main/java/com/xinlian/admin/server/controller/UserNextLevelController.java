//package com.xinlian.admin.server.controller;
//
//import com.xinlian.admin.server.operationLog.OpeAnnotation;
//import com.xinlian.biz.dao.TUserInfoMapper;
//import com.xinlian.biz.dao.TUserMapper;
//import com.xinlian.biz.model.next.NextUserInfoModel;
//import com.xinlian.common.enums.OperationModuleEnum;
//import com.xinlian.common.enums.OperationTypeEnum;
//import com.xinlian.common.request.BalanceBillOperationReq;
//import com.xinlian.common.response.ResponseResult;
//import com.xinlian.common.result.BizException;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.poi.hssf.usermodel.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.OutputStream;
//import java.io.UnsupportedEncodingException;
//import java.util.List;
//
///**
// * @author lt
// * @date 2020/09/17
// **/
//@Slf4j
//@Api(value = "userNextLevel")
//@RestController
//@RequestMapping("/userNextLevel")
//public class UserNextLevelController {
//
//    @Autowired
//    private TUserInfoMapper userInfoMapper;
//
//    @ApiOperation(value = "userNextLevel",httpMethod = "POST")
//    @PostMapping("/export")
//    public void userNextLevel(@RequestBody NextUserInfoModel nextUserInfoModel,HttpServletResponse response) {
//        try {
//            //获取数据
//            List<NextUserInfoModel> list = userInfoMapper.getFirstLevelUserInfoByAuthSn(nextUserInfoModel);
//            //excel标题
//            String[] title = {"UID","realName","身份证号","手机号码","激活状态","激活时间"};
//            //excel文件名
//            String fileName = nextUserInfoModel.getAuthSn() + System.currentTimeMillis()+".xls";
//
//            //sheet名
//            String sheetName = "firstNext";
//            String[][] content = new String[][]{};
//            for (int i = 0; i < list.size(); i++) {
//                content[i] = new String[title.length];
//                NextUserInfoModel obj = list.get(i);
//                content[i][0] = obj.getUid().toString();
//                content[i][1] = obj.getRealName();
//                content[i][2] = obj.getAuthSn();
//                content[i][3] = obj.getMobile();
//                content[i][4] = obj.getOremState().toString();
//                content[i][5] = obj.getActiveTime().toString();
//            }
//
//            //创建HSSFWorkbook
//            HSSFWorkbook wb = getHSSFWorkbook(sheetName, title, content, null);
//
//             //响应到客户端
//            this.setResponseHeader(response, fileName);
//            OutputStream os = response.getOutputStream();
//            wb.write(os);
//            os.flush();
//            os.close();
//        }catch (Exception e){
//            log.error("出现异常:{}",e.toString(),e);
//        }
//    }
//
//    //发送响应流方法
//    public void setResponseHeader(HttpServletResponse response, String fileName) {
//        try {
//            try {
//                fileName = new String(fileName.getBytes(),"ISO8859-1");
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//            response.setContentType("application/octet-stream;charset=ISO8859-1");
//            response.setHeader("Content-Disposition", "attachment;filename="+ fileName);
//            response.addHeader("Pargam", "no-cache");
//            response.addHeader("Cache-Control", "no-cache");
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
//
//    /**
//     * 导出Excel
//     * @param sheetName sheet名称
//     * @param title 标题
//     * @param values 内容
//     * @param wb HSSFWorkbook对象
//     * @return
//     */
//    public HSSFWorkbook getHSSFWorkbook(String sheetName,String []title,String [][]values, HSSFWorkbook wb){
//
//        // 第一步，创建一个HSSFWorkbook，对应一个Excel文件
//        if(wb == null){
//            wb = new HSSFWorkbook();
//        }
//
//        // 第二步，在workbook中添加一个sheet,对应Excel文件中的sheet
//        HSSFSheet sheet = wb.createSheet(sheetName);
//
//        // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制
//        HSSFRow row = sheet.createRow(0);
//
//        // 第四步，创建单元格，并设置值表头 设置表头居中
//        HSSFCellStyle style = wb.createCellStyle();
//
//        //声明列对象
//        HSSFCell cell = null;
//
//        //创建标题
//        for(int i=0;i<title.length;i++){
//            cell = row.createCell(i);
//            cell.setCellValue(title[i]);
//            cell.setCellStyle(style);
//        }
//
//        //创建内容
//        for(int i=0;i<values.length;i++){
//            row = sheet.createRow(i + 1);
//            for(int j=0;j<values[i].length;j++){
//                //将内容按顺序赋给对应的列对象
//                row.createCell(j).setCellValue(values[i][j]);
//            }
//        }
//        return wb;
//    }
//
//}
