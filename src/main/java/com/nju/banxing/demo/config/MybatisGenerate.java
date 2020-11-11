package com.nju.banxing.demo.config;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

/**
 * @Author: jaggerw
 * @Description: mybatis plus
 * @Date: 2020/11/11
 */
public class MybatisGenerate {

    public static void main(String[] args) {
        AutoGenerator mpg = new AutoGenerator();

        GlobalConfig gc = new GlobalConfig();
        String projectPath = "D:\\Document\\code\\java\\banxing-mini-app";
        gc.setOutputDir(projectPath+"/src/main/java");
        gc.setAuthor("JaggerW");
        gc.setOpen(false);
        gc.setFileOverride(false);
        gc.setIdType(IdType.ASSIGN_ID);
        gc.setDateType(DateType.ONLY_DATE);
        gc.setEntityName("%sEntity");
        gc.setMapperName("%sMapper");
        gc.setXmlName("%sMapper");
        gc.setServiceName("%sService");
        gc.setServiceImplName("%sServiceImpl");
        gc.setControllerName("%sController");
        mpg.setGlobalConfig(gc);

        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl("jdbc:mysql://47.94.147.13:13306/banxing?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&useSSL=false");
        dsc.setDriverName("com.mysql.cj.jdbc.Driver");
        dsc.setUsername("root");
        dsc.setPassword("JaggerW123@mysql.docker");
        mpg.setDataSource(dsc);

        PackageConfig pc = new PackageConfig();
        pc.setParent("com.nju.banxing.demo");
        pc.setModuleName("banxing-mini-app");
        pc.setEntity("domain");
        pc.setMapper("mapper");
        pc.setService("service");
        pc.setController("controller");
        pc.setXml("mapper.xml");
        mpg.setPackageInfo(pc);

        StrategyConfig sc = new StrategyConfig();
        sc.setInclude("tutor");
        sc.setInclude("user");
        sc.setNaming(NamingStrategy.underline_to_camel);
        sc.setColumnNaming(NamingStrategy.underline_to_camel);
        sc.setEntityLombokModel(true);
        sc.setRestControllerStyle(true);
        sc.setControllerMappingHyphenStyle(true);
        mpg.setStrategy(sc);

        mpg.execute();
    }

}
