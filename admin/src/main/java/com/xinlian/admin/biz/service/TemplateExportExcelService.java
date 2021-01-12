package com.xinlian.admin.biz.service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * @author lt
 * @date 2020/08/24
 **/
public interface TemplateExportExcelService {
    /**
     * 导出每日资产统计数据
     * @param response HttpServletResponse
     * @param dateTime 导出日期参数
     * @throws IOException
     */
    void exportEveryDayBillDetail(HttpServletResponse response, String dateTime) throws IOException;
}
