package io.github.r4tylmz.betterpoi;

import io.github.r4tylmz.betterpoi.annotation.BPColumn;
import io.github.r4tylmz.betterpoi.annotation.BPExcelWorkbook;
import io.github.r4tylmz.betterpoi.annotation.BPSheet;
import io.github.r4tylmz.betterpoi.annotation.BPWorkbook;
import io.github.r4tylmz.betterpoi.enums.ExcelType;
import io.github.r4tylmz.betterpoi.i18n.MessageSourceService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertTrue;

public class BPValidatorTest {

    @Test
    public void testValidateWithHiddenSheets() throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet1 = workbook.createSheet("Visible Sheet");
            Row row = sheet1.createRow(0);
            row.createCell(0).setCellValue("Name");

            Sheet sheet2 = workbook.createSheet("Hidden Sheet");
            workbook.setSheetHidden(workbook.getSheetIndex(sheet2), true);

            Sheet sheet3 = workbook.createSheet("Very Hidden Sheet");
            workbook.setSheetHidden(workbook.getSheetIndex(sheet3), true);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            InputStream inputStream = new ByteArrayInputStream(bos.toByteArray());

            BPOptions options = BPOptions.builder()
                    .withExcelType(ExcelType.XLSX)
                    .build();

            TestValidationWorkbook testWorkbook = new TestValidationWorkbook();
            MessageSourceService messageSourceService = new MessageSourceService(options);
            BPValidator validator = new BPValidator(testWorkbook, messageSourceService);
            Set<String> errors = validator.validate(inputStream);

            assertTrue(errors.isEmpty());
        }
    }

    @BPWorkbook
    public static class TestValidationWorkbook implements BPExcelWorkbook {

        @BPSheet(sheetName = "Visible Sheet", type = VisibleData.class, columns = {
                @BPColumn(fieldName = "name", headerTitle = "Name")
        })
        private List<VisibleData> visibleData;

        @BPSheet(sheetName = "Hidden Sheet", type = HiddenData.class, columns = {
                @BPColumn(fieldName = "data", headerTitle = "Data")
        })
        private List<HiddenData> hiddenData;

        @BPSheet(sheetName = "Very Hidden Sheet", type = VeryHiddenData.class, columns = {
                @BPColumn(fieldName = "data", headerTitle = "Data")
        })
        private List<VeryHiddenData> veryHiddenData;

        public List<VisibleData> getVisibleData() {
            return visibleData;
        }

        public void setVisibleData(List<VisibleData> visibleData) {
            this.visibleData = visibleData;
        }

        public List<HiddenData> getHiddenData() {
            return hiddenData;
        }

        public void setHiddenData(List<HiddenData> hiddenData) {
            this.hiddenData = hiddenData;
        }

        public List<VeryHiddenData> getVeryHiddenData() {
            return veryHiddenData;
        }

        public void setVeryHiddenData(List<VeryHiddenData> veryHiddenData) {
            this.veryHiddenData = veryHiddenData;
        }
    }

    public static class VisibleData {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class HiddenData {
        private String data;

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }

    public static class VeryHiddenData {
        private String data;

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }
}