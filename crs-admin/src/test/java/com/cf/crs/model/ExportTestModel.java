package com.cf.crs.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;

@Data
public class ExportTestModel extends BaseRowModel {
    @ExcelProperty(index = 0 ,value = "账号")
    private String customer ;
    @ExcelProperty(index = 0 ,value = "姓名")
    private String name;
    @ExcelProperty(index = 1 ,value = "手机")
    private String mobile;

    public ExportTestModel(String id) {
        this.customer = customer;
        this.name = "标题"+id;
        this.mobile = "描述"+id;
    }

    /**
     * 无参构造函数是导入的必要条件
     */
    public ExportTestModel() {}
}
