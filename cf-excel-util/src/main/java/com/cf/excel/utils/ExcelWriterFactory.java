package com.cf.excel.utils;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.BaseRowModel;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * @author spark
 * @title: ExcelWriterFactory
 * @description: Excel写入工厂
 * @date 2019/8/15 9:34
 */
public class ExcelWriterFactory extends ExcelWriter {
    private OutputStream outputStream;
    private int sheetNo = 1;

    ExcelWriterFactory(OutputStream outputStream, ExcelTypeEnum typeEnum) {
        super(outputStream, typeEnum);
        this.outputStream = outputStream;
    }

    public ExcelWriterFactory write(List<? extends BaseRowModel> list, String sheetName,
                                    BaseRowModel object) {
        this.sheetNo++;
        try {
            Sheet sheet = new Sheet(sheetNo, 0, object.getClass());
            sheet.setSheetName(sheetName);
            this.write(list, sheet);
        } catch (Exception ex) {
            try {
                outputStream.flush();
            } catch (IOException e) {
                //do something
            }
        }
        return this;
    }

    @Override
    public void finish() {
        super.finish();
        try {
            outputStream.flush();
        } catch (IOException e) {
            //do something
        }
    }

    public void close() {
        try {
            outputStream.close();
        } catch (IOException e) {
            //do something
        }
    }
}
