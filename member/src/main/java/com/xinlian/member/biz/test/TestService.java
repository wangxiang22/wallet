package com.xinlian.member.biz.test;

import com.alibaba.fastjson.JSON;
import com.xinlian.biz.dao.TCountryDicMapper;
import com.xinlian.biz.model.TCountryDic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;

@Service
public class TestService {

    @Autowired
    private TCountryDicMapper countryDicMapper;

    public void initCountryDic(){
        String json = redJsonFile();
        List<TCountryDic> list = JSON.parseArray(json, TCountryDic.class);
        countryDicMapper.insertBatch(list);
    }

    private String redJsonFile(){
        String jsonStr = "";
        try {
            File jsonFile = new File("E:\\area.json");
            FileReader fileReader = new FileReader(jsonFile);
            Reader reader = new InputStreamReader(new FileInputStream(jsonFile),"utf-8");
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            fileReader.close();
            reader.close();
            jsonStr = sb.toString();
            return jsonStr;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
