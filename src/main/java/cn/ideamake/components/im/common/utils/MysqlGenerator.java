//package cn.ideamake.components.im.common.utils;
//
//import com.baomidou.mybatisplus.annotation.FieldFill;
//import com.baomidou.mybatisplus.core.toolkit.StringPool;
//import com.baomidou.mybatisplus.generator.AutoGenerator;
//import com.baomidou.mybatisplus.generator.InjectionConfig;
//import com.baomidou.mybatisplus.generator.config.*;
//import com.baomidou.mybatisplus.generator.config.po.TableFill;
//import com.baomidou.mybatisplus.generator.config.po.TableInfo;
//import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
//import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * <p>
// * mysql 代码生成器演示例子
// * </p>
// *
// * @author jobob
// * @since 2018-09-12
// */
//public class MysqlGenerator {
//
///*    *//**
//     * <p>
//     * 读取控制台内容
//     * </p>
//     *//*
//    public static String scanner(String tip) {
//        Scanner scanner = new Scanner(System.in);
//        StringBuilder help = new StringBuilder();
//        help.append("请输入" + tip + "：");
//        System.out.println(help.toString());
//        if (scanner.hasNext()) {
//            String ipt = scanner.next();
//            if (StringUtils.isNotEmpty(ipt)) {
//                return ipt;
//            }
//        }
//        throw new MybatisPlusException("请输入正确的" + tip + "！");
//    }*/
//
//    /**
//     * 需要生成的表
//     */
//    final static String[] tables ={
//            "im_share"
//
//    };
//    /**
//     * RUN THIS
//     */
//    public static void main(String[] args) {
//
//
//
//        // 自定义需要填充的字段
//        List<TableFill> tableFillList = new ArrayList<TableFill>();
//        //如 每张表都有一个创建时间、修改时间
//        //而且这基本上就是通用的了，新增时，创建时间和修改时间同时修改
//        //修改时，修改时间会修改，
//        //虽然像Mysql数据库有自动更新几只，但像ORACLE的数据库就没有了，
//        //使用公共字段填充功能，就可以实现，自动按场景更新了。
//        //如下是配置
//        TableFill createField = new TableFill("create_at", FieldFill.INSERT);
//        TableFill modifiedField = new TableFill("update_at", FieldFill.INSERT_UPDATE);
//        tableFillList.add(createField);
//        tableFillList.add(modifiedField);
//
//
//
//        // 代码生成器
//        AutoGenerator mpg = new AutoGenerator();
//
//        // 全局配置
//        GlobalConfig gc = new GlobalConfig();
//        String projectPath = System.getProperty("user.dir");
//        gc.setOutputDir(projectPath + "/src/main/java");
//        gc.setAuthor("ideamake");
//        gc.setOpen(false);
////        gc.setFileOverride(true);
//        mpg.setGlobalConfig(gc);
//
//        // 数据源配置
//        DataSourceConfig dsc = new DataSourceConfig();
//        dsc.setUrl("jdbc:mysql://rm-wz9gtu4pe3f6vom022o.mysql.rds.aliyuncs.com:3306/ideamake_bgyjssq?useUnicode=true&useSSL=false&&serverTimezone=UTC");
//        // dsc.setSchemaName("public");
//
//        //mysql1.8驱动
//        dsc.setDriverName("com.mysql.cj.jdbc.Driver");
//        dsc.setUsername("ideamake_dbuser");
//        dsc.setPassword("1a!gHco@d2mrgf^*^4lwGpB^");
//        mpg.setDataSource(dsc);
//
//        // 包配置
//        PackageConfig pc = new PackageConfig();
////        pc.setModuleName(scanner("模块名"));
//        pc.setParent("cn.ideamake.bgyjssq");
//        pc.setController("web.controller");
//        pc.setService("service");
//        pc.setServiceImpl("service.impl");
//        pc.setMapper("dao.mapper");
//        pc.setEntity("pojo.entity");
//        pc.setXml("xml");
//        mpg.setPackageInfo(pc);
//
//
//         //自定义配置
//        InjectionConfig cfg = new InjectionConfig() {
//            @Override
//            public void initMap() {
//                // to do nothing
//            }
//        };
//        List<FileOutConfig> focList = new ArrayList<>();
//        focList.add(new FileOutConfig("/templates/mapper.xml.vm") {
//            @Override
//            public String outputFile(TableInfo tableInfo) {
//                // 自定义输入文件名称
//                return projectPath + "/src/main/resources/mapper/"  + tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
//            }
//        });
//        /*
//        //配置自定义controller模版
//        focList.add(new FileOutConfig("/templates/controller.vm") {
//            @Override
//            public String outputFile(TableInfo tableInfo) {
//                return projectPath+"/src/main/java/cn/ideamake/bgyjssq/web/controller/"+tableInfo.getEntityName() + "Controller" + StringPool.DOT_JAVA;
//            }
//        });
//        */
//
//        cfg.setFileOutConfigList(focList);
//
//
//
//
//
//        TemplateConfig tc = new TemplateConfig();
////        tc.setController("/templates/controller.vm");
////        tc.setEntity(null);
////        tc.setMapper(null);
//        tc.setXml(null);
////        tc.setService(null);
////        tc.setServiceImpl(null);
//        mpg.setTemplate(tc);
//        mpg.setCfg(cfg);
//        // 策略配置
//        StrategyConfig strategy = new StrategyConfig();
//        strategy.setNaming(NamingStrategy.underline_to_camel);
//        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
////        strategy.setSuperEntityClass("com.baomidou.mybatisplus.samples.generator.common.BaseEntity");
//        strategy.setEntityLombokModel(true);
//        strategy.setSuperControllerClass("cn.ideamake.bgyjssq.web.controller.AbstractController");
//        strategy.setInclude(tables);
////        strategy.setSuperEntityColumns("id");
//        strategy.setControllerMappingHyphenStyle(true);
//        strategy.setRestControllerStyle(true);
//        strategy.setTablePrefix("im_");//表前缀
//        strategy.setTableFillList(tableFillList);//设置填充字段，主要针对日期
//        mpg.setStrategy(strategy);
//        // 选择 freemarker 引擎需要指定如下加，注意 pom 依赖必须有！
////        mpg.setTemplateEngine(new FreemarkerTemplateEngine());
//        mpg.setTemplateEngine(new VelocityTemplateEngine());
//        mpg.execute();
//    }
//
//}
