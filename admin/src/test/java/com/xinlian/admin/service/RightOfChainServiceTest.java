package com.xinlian.admin.service;

import com.xinlian.admin.service.base.BaseServiceTest;
import com.xinlian.biz.dao.TReleaseCatRecordMapper;
import com.xinlian.biz.model.TReleaseCatRecord;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class RightOfChainServiceTest extends BaseServiceTest {

    @Autowired
    private TReleaseCatRecordMapper releaseCatRecordMapper;

    @Test
    public void batchInsert()throws Exception{
        List<TReleaseCatRecord> list = this.analysisExcelToList();
        releaseCatRecordMapper.batchInsert(list);
    }

    public List<TReleaseCatRecord> analysisExcelToList() throws Exception{
        String fileName = "F:/Work/CAT钱包重构/2月27日链权人释放CAT名单.xlsx";
        Workbook wb = getSheets(fileName);
        Sheet hssfSheet = wb.getSheetAt(0);
        List<TReleaseCatRecord> list = new ArrayList<>();
        for (int i = 0; i <= hssfSheet.getLastRowNum(); i++) {
            if (hssfSheet.getRow(i) == null) {
                break;
            }
            Row row = hssfSheet.getRow(i);
            TReleaseCatRecord releaseCatRecord = createBeanByXsl(row);
            list.add(releaseCatRecord);
        }
        return list;
    }

    private TReleaseCatRecord createBeanByXsl(Row row) {
        TReleaseCatRecord releaseCatRecord = new TReleaseCatRecord();
        try {
            releaseCatRecord.setStatus(1);

            if(Cell.CELL_TYPE_NUMERIC == row.getCell(3).getCellType()){
                releaseCatRecord.setUid(Long.parseLong(NumberToTextConverter.toText(row.getCell(3).getNumericCellValue())));
            }
            if(Cell.CELL_TYPE_NUMERIC == row.getCell(5).getCellType()){
                releaseCatRecord.setReleaseCatNum(new BigDecimal(NumberToTextConverter.toText(row.getCell(5).getNumericCellValue())));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return releaseCatRecord;
    }




}
