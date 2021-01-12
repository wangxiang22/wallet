package com.xinlian.admin.service;

import com.xinlian.admin.service.base.BaseServiceTest;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class UploadAdressServiceTest extends BaseServiceTest {

    @Test
    public void uploadChainOwnerRecordList() throws Exception{
        String fileName = "F:/地址数据0620001.xlsx";
        Workbook wb = getSheets(fileName);
        Sheet hssfSheet = wb.getSheetAt(0);
        List<String> list = new ArrayList<>();
        for (int i = 2; i <= hssfSheet.getLastRowNum(); i++) {
            if (hssfSheet.getRow(i) == null ) {
                break;
            }
            Row row = hssfSheet.getRow(i);
            String getAddress = createBeanByXsl(row);
            list.add(getAddress);
        }
        System.err.println("insert into t_address_pool (address,status,createtime) values ");
        int num = 60000;
        for(int i=45000;i<num;i++){
            String getAddress = list.get(i);
            System.err.println("('" + getAddress + "',0,CURRENT_TIMESTAMP),");
        }
        System.err.println("end");
    }

    private String createBeanByXsl(Row row) {
        String address = row.getCell(0).getStringCellValue();
        return address;
    }
}
