package io.github.r4tylmz.betterpoi.test;

import java.util.List;

import io.github.r4tylmz.betterpoi.annotation.BPColumn;
import io.github.r4tylmz.betterpoi.annotation.BPExcelWorkbook;
import io.github.r4tylmz.betterpoi.annotation.BPSheet;
import io.github.r4tylmz.betterpoi.annotation.BPWorkbook;

@BPWorkbook
public class ConvertWorkbook implements BPExcelWorkbook{
    @BPSheet(sheetName = "Sayfa1",
            type = ConvertRecord.class, columns = {
            @BPColumn(fieldName = "bd", headerTitle = "BD"),
            @BPColumn(fieldName = "d", headerTitle = "Double"),
            @BPColumn(fieldName = "date", headerTitle = "Date"),
            @BPColumn(fieldName = "str", headerTitle = "String")
    })
    List<ConvertRecord> convertRecords;
        
    public List<ConvertRecord> getConvertRecords() {
        return convertRecords;
    }
        
    public void setConvertRecords(List<ConvertRecord> convertRecords) {
        this.convertRecords = convertRecords;
    }
}
