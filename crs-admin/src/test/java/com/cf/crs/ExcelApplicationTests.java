package com.cf.crs;

import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.cf.AdminApplication;
import com.cf.crs.model.ExportTestModel;
import com.cf.excel.exception.ExcelException;
import com.cf.excel.utils.ExcelUtil;
import com.cf.excel.utils.ExcelUtil2;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = AdminApplication.class)
/**
 * @title: ExcelApplicationTests
 * @description: Excel测试
 * @author spark
 * @date 2019/8/15 9:34
 */
public class ExcelApplicationTests {

   @Test
    public void importExcel(){
       MultipartFile multipartFile = fileTranMultipartFile("F:\\migndan2.xls");
        try {
            List<ExportTestModel> list = ExcelUtil.readExcel(multipartFile,ExportTestModel.class);
            System.out.println(JSON.toJSONString(list, SerializerFeature.PrettyFormat));
        } catch (ExcelException e) {
            e.getMessage();
        }
    }

    @Test
    public void exportData(){
        List<ExportTestModel> list = new ArrayList<>();
        for(int i = 0 ;i<=1000;i++){
            list.add(new ExportTestModel(i+""));
        }
        try {
            ExcelUtil.writeExcel(null,list,"导出测试","没有设定sheet名称", ExcelTypeEnum.XLSX,ExportTestModel.class);
        } catch (ExcelException e) {
            e.getMessage();
        }
        System.out.println("导出成功");
    }

    @Test
    public void writeWithTemplate(){
        String filePath = "F:\\newmingdan.xls";
        ArrayList<ExportTestModel> data = new ArrayList<>();
        for(int i = 0; i < 4; i++){
            ExportTestModel tableHeaderExcelProperty = new ExportTestModel();
            tableHeaderExcelProperty.setName("zhang" + i);
            tableHeaderExcelProperty.setCustomer("80600"+ i);
            tableHeaderExcelProperty.setMobile("13022" + i);
            data.add(tableHeaderExcelProperty);
        }
        ExcelUtil2.writeWithTemplate(filePath,data);
    }

    /**
     * 读取少于1000行的excle
     */
    @org.junit.Test
    public void readLessThan1000Row(){
        String filePath = "F:\\newmingdan.xls";
        List<Object> objects = ExcelUtil2.readLessThan1000Row(filePath);
        objects.forEach(System.out::println);
    }

    /**
     * 读取少于1000行的excel，可以指定sheet和从几行读起
     */
    @org.junit.Test
    public void readLessThan1000RowBySheet(){
        String filePath = "F:\\newmingdan.xls";
        Sheet sheet = new Sheet(1, 1);
        List<Object> objects = ExcelUtil2.readLessThan1000RowBySheet(filePath,sheet);
        objects.forEach(System.out::println);
    }

    /**el
     * 读取大于1000行的excel
     * 带sheet参数的方法可参照测试方法readLessThan1000RowBySheet()
     */
    @org.junit.Test
    public void readMoreThan1000Row(){
        String filePath = "F:\\newmingdan.xls";
        List<Object> objects = ExcelUtil2.readMoreThan1000Row(filePath);
        objects.forEach(System.out::println);
    }


    /**
     * 生成excle, 带用模型,带多个sheet
     */
    @org.junit.Test
    public void writeWithMultipleSheel(){
        ArrayList<ExcelUtil2.MultipleSheelPropety> list1 = new ArrayList<>();
        for(int j = 1; j < 4; j++){
            ArrayList<ExportTestModel> list = new ArrayList<>();
            for(int i = 0; i < 4; i++){
                ExportTestModel tableHeaderExcelProperty = new ExportTestModel();
                tableHeaderExcelProperty.setName("zhang" + i);
                tableHeaderExcelProperty.setCustomer("80600"+ i);
                tableHeaderExcelProperty.setMobile("13022" + i);
                list.add(tableHeaderExcelProperty);
            }
            Sheet sheet = new Sheet(j, 0);
            sheet.setSheetName("sheet" + j);
            ExcelUtil2.MultipleSheelPropety multipleSheelPropety = new ExcelUtil2.MultipleSheelPropety();
            multipleSheelPropety.setData(list);
            multipleSheelPropety.setSheet(sheet);
            list1.add(multipleSheelPropety);
        }

        ExcelUtil2.writeWithMultipleSheel("F:\\newmingdan.xls",list1);

    }

    public MultipartFile fileTranMultipartFile(String filepath) {
        MultipartFile cMultiFile = null;
        try {
            File file = new File(filepath);
            FileInputStream input = new FileInputStream(file);
             cMultiFile =new MockMultipartFile("file", file.getName(), "text/plain", IOUtils.toByteArray(input));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cMultiFile;
    }








}

