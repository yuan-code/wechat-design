package com.hualala.util;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.ArrayList;
import java.util.List;

/**
 * @author YuanChong
 * @create 2019-07-06 11:25
 * @desc
 */
public class CodeGenerator {


    public static final String PACKAGE_NAME = "com.hualala";

    private static void executeCode(String pack,String[] tables) {
        // 代码生成器
        AutoGenerator mpg = new AutoGenerator();

        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        // 是否覆盖已有文件
        gc.setFileOverride(true);
        // 生成文件的输出目录
        String projectPath = System.getProperty("user.dir");
        gc.setOutputDir(projectPath + "/src/main/test");
        //设置bean命名规范
        gc.setEntityName("%s");
        // 开发人员
        gc.setAuthor("YuanChong");
        // 是否打开输出目录
        gc.setOpen(false);
        // 开启 BaseResultMap
        gc.setBaseResultMap(true);
        // 指定生成的主键的ID类型
        gc.setIdType(IdType.ID_WORKER);
        mpg.setGlobalConfig(gc);

        // 数据源配置
        DataSourceConfig config= new DataSourceConfig();
        // 从试图获取
        config.setUrl("jdbc:mysql://localhost:3306/wechat");
        config.setDriverName("com.mysql.jdbc.Driver");
        config.setUsername("root");
        config.setPassword("root");
        mpg.setDataSource(config);

        // 包配置
        PackageConfig pc = new PackageConfig();
        // 父包名。如果为空，将下面子包名必须写全部， 否则就只需写子包名
        pc.setParent(pack);
        // Entity包名
        pc.setEntity("model");
        mpg.setPackageInfo(pc);

        // 自定义配置
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                // to do nothing
            }
        };
        List<FileOutConfig> focList = new ArrayList<>();
        focList.add(new FileOutConfig("/templates/mapper.xml.ftl") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输入文件名称
                if (StringUtils.isEmpty(pc.getModuleName())) {
                    return projectPath + "/src/main/resources/mapper/" + tableInfo.getXmlName() + StringPool.DOT_XML;
                }else {
                    return projectPath + "/src/main/resources/mapper/" + pc.getModuleName() + "/" + tableInfo.getXmlName() + StringPool.DOT_XML;
                }
            }
        });
        cfg.setFileOutConfigList(focList);
        mpg.setCfg(cfg);
        mpg.setTemplate(new TemplateConfig().setXml(null));

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        // 数据库表映射到实体的命名策略: 下划线转驼峰命名
        strategy.setNaming(NamingStrategy.underline_to_camel);
        // 数据库表字段映射到实体的命名策略: 下划线转驼峰命名
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        // 【实体】是否为lombok模型（默认 false）
        strategy.setEntityLombokModel(true);
        // 需要包含的表名，允许正则表达式（与exclude二选一配置）
        strategy.setInclude(tables);
        // 驼峰转连字符
        strategy.setControllerMappingHyphenStyle(true);
        mpg.setStrategy(strategy);
        mpg.setTemplateEngine(new FreemarkerTemplateEngine());
        mpg.execute();
    }

    public static void main(String[] args) {
        //表名数组
        String[] tables = new String[] {"order"};
        executeCode(PACKAGE_NAME,tables);

    }

}
