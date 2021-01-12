package com.xinlian.admin.service.base;

import com.xinlian.AdminApplication;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.InputStream;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AdminApplication.class)
public class BaseServiceTest {


    public Workbook getSheets(String fileName) throws Exception {
        Workbook wb = null;
        InputStream is = null;
        try {
            is = new FileInputStream(fileName);
            wb = WorkbookFactory.create(is);
        } catch (Exception e){
            e.printStackTrace();
        }
        return wb;
    }
}
