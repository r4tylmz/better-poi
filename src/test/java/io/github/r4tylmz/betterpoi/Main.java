package io.github.r4tylmz.betterpoi;

import io.github.r4tylmz.betterpoi.annotation.BPColumn;
import io.github.r4tylmz.betterpoi.annotation.BPExcelWorkbook;
import io.github.r4tylmz.betterpoi.annotation.BPSheet;
import io.github.r4tylmz.betterpoi.annotation.BPWorkbook;
import io.github.r4tylmz.betterpoi.enums.ExcelType;

import java.io.File;
import java.util.Date;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        BPOptions options = BPOptions
                .builder()
                .withExcelType(ExcelType.XLSX)
                .withBundleName("messages")
                .withLocale("tr")
                .build();
        BPImporter<PoWorkbook> bpImporter = new BPImporter<>(PoWorkbook.class, options);
        PoWorkbook poWorkbook = bpImporter.importExcel(new File("/Users/ylmz/Downloads/PO_yükleme_deneme_xlsx/PO yükleme deneme.xlsx"));
        System.out.println(poWorkbook.getPoList());
    }

    public static class Po {
        private String poType;
        private String poNo;
        private Date poDate;
        private String poGroup;

        public Po() {}

        public Date getPoDate() {
            return poDate;
        }

        public void setPoDate(Date poDate) {
            this.poDate = poDate;
        }

        public String getPoGroup() {
            return poGroup;
        }

        public void setPoGroup(String poGroup) {
            this.poGroup = poGroup;
        }

        public String getPoNo() {
            return poNo;
        }

        public void setPoNo(String poNo) {
            this.poNo = poNo;
        }

        public String getPoType() {
            return poType;
        }

        public void setPoType(String poType) {
            this.poType = poType;
        }
    }

    @BPWorkbook
    public static class PoWorkbook implements BPExcelWorkbook {

        public PoWorkbook() {}

        @BPSheet(sheetName = "Sheet1",
                type = Po.class, columns = {
                @BPColumn(fieldName = "poType", headerTitle = "PO Tipi"),
                @BPColumn(fieldName = "poNo", headerTitle = "PO No"),
                @BPColumn(fieldName = "poDate", headerTitle = "PO Tarihi"),
                @BPColumn(fieldName = "poGroup", headerTitle = "İş Paketi"),
        })
        List<Po> poList;

        public List<Po> getPoList() {
            return poList;
        }

        public void setPoList(List<Po> poList) {
            this.poList = poList;
        }
    }
}
