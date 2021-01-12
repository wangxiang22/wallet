package com.xinlian.admin.service;

import com.xinlian.admin.service.base.BaseServiceTest;
import com.xinlian.biz.dao.TUploadChainOwnerRecordMapper;
import com.xinlian.biz.model.TUploadChainOwnerRecord;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class UploadChainOwnerServiceTest extends BaseServiceTest {

    @Autowired
    private TUploadChainOwnerRecordMapper uploadChainOwnerRecordMapper;

    @Test
    public void batchInsert()throws Exception{
        List<TUploadChainOwnerRecord> list = this.uploadChainOwnerRecordList();
        uploadChainOwnerRecordMapper.batchInsert(list);
    }

    public List<TUploadChainOwnerRecord> uploadChainOwnerRecordList() throws Exception{
        String fileName = "E:/work/CAT钱包重构/链权人/4.28高原天节点链权人添加信息.xlsx";
        Workbook wb = getSheets(fileName);
        Sheet hssfSheet = wb.getSheetAt(0);
        List<TUploadChainOwnerRecord> list = new ArrayList<>();
        for (int i = 1; i <= hssfSheet.getLastRowNum(); i++) {
            if (hssfSheet.getRow(i) == null) {
                break;
            }
            Row row = hssfSheet.getRow(i);
            TUploadChainOwnerRecord chainOwnerRecord = createBeanByXsl(row,i);
            list.add(chainOwnerRecord);
        }
        return list;
    }

    private TUploadChainOwnerRecord createBeanByXsl(Row row,int i) {
        TUploadChainOwnerRecord chainOwnerRecord = new TUploadChainOwnerRecord();
        try {
            chainOwnerRecord.setStatus(1);
            if(Cell.CELL_TYPE_NUMERIC == row.getCell(0).getCellType()){
                chainOwnerRecord.setUid(Long.parseLong(NumberToTextConverter.toText(row.getCell(0).getNumericCellValue())));
            }else {
                chainOwnerRecord.setUid(Long.parseLong(row.getCell(0).getStringCellValue()));
            }
            if(Cell.CELL_TYPE_NUMERIC == row.getCell(1).getCellType()){
                System.err.println("username:"+NumberToTextConverter.toText(row.getCell(1).getNumericCellValue()));
                chainOwnerRecord.setAuthName(NumberToTextConverter.toText(row.getCell(1).getNumericCellValue()));
            }else{
                chainOwnerRecord.setAuthName(row.getCell(1).getStringCellValue());
            }
            /*if(Cell.CELL_TYPE_NUMERIC == row.getCell(3).getCellType()){
                chainOwnerRecord.setMobile(NumberToTextConverter.toText(row.getCell(3).getNumericCellValue()));
            }else{
                chainOwnerRecord.setMobile(row.getCell(3).getStringCellValue());
            }*/



            if(Cell.CELL_TYPE_NUMERIC == row.getCell(2).getCellType()){
                chainOwnerRecord.setAuthSn(NumberToTextConverter.toText(row.getCell(2).getNumericCellValue()));
            }else{
                chainOwnerRecord.setAuthSn(row.getCell(2).getStringCellValue());
            }
        }catch (Exception e){
            System.err.println("行数:"+i);
            e.printStackTrace();
        }
        return chainOwnerRecord;
    }
}
