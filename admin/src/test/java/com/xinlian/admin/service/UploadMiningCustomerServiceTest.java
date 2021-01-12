package com.xinlian.admin.service;

import com.xinlian.admin.service.base.BaseServiceTest;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class UploadMiningCustomerServiceTest extends BaseServiceTest {

    @Test
    public void uploadChainOwnerRecordList() throws Exception{
        String fileName = "F:/01.矿池名单列表/第四批处理数据data/第四批算能机测试人员明细表.xlsx";
        Workbook wb = getSheets(fileName);
        Sheet hssfSheet = wb.getSheetAt(0);
        List<Long> successList = new ArrayList<>();
        List<Long> replaceList = new ArrayList<>();
        List<String> errorList = new ArrayList<>();
        for (int i = 3; i <= hssfSheet.getLastRowNum(); i++) {
            Row row = hssfSheet.getRow(i);
            String cellValue = createBeanByXsl(row,i);
            try {
                Long uid = Long.parseLong(cellValue);
                successList.add(uid);
            }catch (Exception e){
                String replaceValue = cellValue.replaceAll("\n","");
                replaceValue = replaceValue.replaceAll(" ","");
                replaceValue = replaceValue.replaceAll("\r","");
                replaceValue = replaceValue.replaceAll("\t","");
                try {
                    Long replaceUid = Long.parseLong(replaceValue);
                    replaceList.add(replaceUid);
                }catch (Exception e1){
                    errorList.add(cellValue);
                }
            }
        }

        for(int i=0;i<replaceList.size();i++){
            Long getValue = replaceList.get(i);
            System.err.print(getValue+",");
        }
    }

    private String createBeanByXsl(Row row,int i) {
        String cellValue = "";
        if(Cell.CELL_TYPE_NUMERIC == row.getCell(1).getCellType()){
            cellValue = NumberToTextConverter.toText(row.getCell(1).getNumericCellValue());
        }else {
            cellValue = row.getCell(1).getStringCellValue();
        }
        return cellValue;
    }
}
