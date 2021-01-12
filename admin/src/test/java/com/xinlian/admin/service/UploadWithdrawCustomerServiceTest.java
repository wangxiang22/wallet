package com.xinlian.admin.service;

import com.xinlian.admin.service.base.BaseServiceTest;
import com.xinlian.biz.model.WithdrawCustomerModel;
import com.xinlian.common.utils.UdunBigDecimalUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class UploadWithdrawCustomerServiceTest extends BaseServiceTest {

    @Test
    public void uploadChainOwnerRecordList() throws Exception{
        String fileName = "F:/提币6月3日最终.xlsx";
        Workbook wb = getSheets(fileName);
        Sheet hssfSheet = wb.getSheetAt(0);
        List<WithdrawCustomerModel> list = new ArrayList<>();
        for (int i = 2; i <= hssfSheet.getLastRowNum(); i++) {
            if (hssfSheet.getRow(i) == null || i > 33) {
                break;
            }
            Row row = hssfSheet.getRow(i);
            WithdrawCustomerModel withdrawCustomerModel = createBeanByXsl(row,i);
            list.add(withdrawCustomerModel);
        }
        System.err.println("INSERT INTO `t_withdraw_customer` (uid,withdraw_status,allow_withdraw_num,createtime)VALUES ");
        for(int i=0;i<list.size();i++){
            WithdrawCustomerModel model = list.get(i);
            if(model.getAllowWithdrawNum().compareTo(UdunBigDecimalUtil.zeroBigDecimal)==0)continue;
            System.err.println("("+model.getUid()+","+model.getWithdrawStatus()+","+model.getAllowWithdrawNum()+",CURRENT_TIMESTAMP),");
        }
    }

    private WithdrawCustomerModel createBeanByXsl(Row row,int i) {
        WithdrawCustomerModel withdrawCustomerModel = new WithdrawCustomerModel();
        try {
            withdrawCustomerModel.setWithdrawStatus(1);
            if(Cell.CELL_TYPE_NUMERIC == row.getCell(5).getCellType()){
                withdrawCustomerModel.setUid(Long.parseLong(NumberToTextConverter.toText(row.getCell(5).getNumericCellValue())));
            }else {
                withdrawCustomerModel.setUid(Long.parseLong(row.getCell(5).getStringCellValue()));
            }

            if(Cell.CELL_TYPE_NUMERIC == row.getCell(7).getCellType()){
                withdrawCustomerModel.setAllowWithdrawNum(new BigDecimal(row.getCell(7).getNumericCellValue()));
            }else{
                withdrawCustomerModel.setAllowWithdrawNum(new BigDecimal(row.getCell(7).getStringCellValue()));
            }

        }catch (Exception e){
            System.err.println("行数:"+i);
            e.printStackTrace();
        }
        return withdrawCustomerModel;
    }
}
