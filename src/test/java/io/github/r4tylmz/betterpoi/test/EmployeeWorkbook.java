package io.github.r4tylmz.betterpoi.test;

import io.github.r4tylmz.betterpoi.annotation.BPColumn;
import io.github.r4tylmz.betterpoi.annotation.BPExcelWorkbook;
import io.github.r4tylmz.betterpoi.annotation.BPSheet;
import io.github.r4tylmz.betterpoi.annotation.BPWorkbook;
import io.github.r4tylmz.betterpoi.constraint.DefaultConstraint;
import io.github.r4tylmz.betterpoi.validation.cell.UserDefinedMaxLenValidator;
import io.github.r4tylmz.betterpoi.validation.row.DuplicateRowConstraint;

import java.util.List;

@BPWorkbook
public class EmployeeWorkbook implements BPExcelWorkbook {

    @BPSheet(sheetName = "Employee Records",
            colValidators = DefaultConstraint.class,
            rowValidators = {DuplicateRowConstraint.class},
            type = EmployeeRecord.class, columns = {
            @BPColumn(fieldName = "employeeId", headerTitle = "Employee ID", required = true),
            @BPColumn(fieldName = "employeeName", headerTitle = "Employee Name", cellValidators = {UserDefinedMaxLenValidator.class}),
            @BPColumn(fieldName = "salary", headerTitle = "Salary"),
            @BPColumn(fieldName = "department", headerTitle = "Department"),
            @BPColumn(fieldName = "yearsOfService", headerTitle = "Years of Service"),
            @BPColumn(fieldName = "hireDate", headerTitle = "Hire Date", required = true),
    })
    List<EmployeeRecord> employeeRecordList;

    public List<EmployeeRecord> getEmployeeRecordList() {
        return employeeRecordList;
    }

    public void setEmployeeRecordList(List<EmployeeRecord> employeeRecordList) {
        this.employeeRecordList = employeeRecordList;
    }
}
