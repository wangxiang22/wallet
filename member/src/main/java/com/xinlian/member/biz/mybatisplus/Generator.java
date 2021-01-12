package com.xinlian.member.biz.mybatisplus;

import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.DbType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

public class Generator {

        public static void main(String[] args) {
            String packageName = "com.xinlian.biz";
            //auth -> UserService, 设置成true: auth -> IUserServics
            boolean serviceNameStartWithI = false;
            generateByTables(serviceNameStartWithI, packageName,
                    "无名氏", "xinlian_wallet",
                    "admin_role_interface");
            System.out.println("completed...");
        }

        private static void generateByTables(boolean serviceNameStartWithI, String packageName,
                                             String author, String database, String... tableNames) {
            //&serverTimezone=UTC
            String dbUrl = "jdbc:mysql://47.52.102.159:3306/" + database + "?useUnicode=true&characterEncoding=utf8&autoReconnect=true&useSSL=false&serverTimezone=Asia/Shanghai";
            DataSourceConfig dataSourceConfig = new DataSourceConfig();
            dataSourceConfig.setDbType(DbType.MYSQL)
                    .setUrl(dbUrl)
                    .setUsername("nsh")
                    .setPassword("nsh,.123")
                    //.setDriverName("com.mysql.jdbc.Driver");
                    .setDriverName("com.mysql.cj.jdbc.Driver");
            StrategyConfig strategyConfig = new StrategyConfig();
            strategyConfig.setCapitalMode(true)
                    .setEntityLombokModel(true)
                    .setDbColumnUnderline(true)
                    .setRestControllerStyle(true)
                    .setNaming(NamingStrategy.underline_to_camel).entityTableFieldAnnotationEnable(true)
                    //.setSuperMapperClass("cn.saytime.mapper.BaseMapper")
                    //修改替换成你需要的表名，多个表名传数组
                    .setInclude(tableNames);

            GlobalConfig config = new GlobalConfig();
            config.setActiveRecord(false)
                    .setAuthor(author)
                    .setOutputDir("e:\\generator")
                    .setFileOverride(true)
                    .setEnableCache(false)
                    .setBaseResultMap(true)
                    .setBaseColumnList(true);

            if (!serviceNameStartWithI) {
                config.setServiceName("%sService");
            }
            new AutoGenerator().setGlobalConfig(config)
                    .setDataSource(dataSourceConfig)
                    .setStrategy(strategyConfig)
                    .setPackageInfo(new PackageConfig()
                                    .setParent(packageName)
                                    .setController("web")
                                    .setEntity("entity")
                                    .setMapper("mapper")
                                    .setService("service")
                                    .setServiceImpl("service.impl")
                                    .setXml("dao.mapper")).execute();
        }

}
