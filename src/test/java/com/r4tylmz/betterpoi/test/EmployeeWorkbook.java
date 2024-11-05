package com.r4tylmz.betterpoi.test;

import com.r4tylmz.betterpoi.annotation.BPColumn;
import com.r4tylmz.betterpoi.annotation.BPExcelWorkbook;
import com.r4tylmz.betterpoi.annotation.BPSheet;
import com.r4tylmz.betterpoi.annotation.BPWorkbook;
import com.r4tylmz.betterpoi.constraint.DefaultConstraint;
import com.r4tylmz.betterpoi.validation.cell.UserDefinedMaxLenValidator;
import com.r4tylmz.betterpoi.validation.row.DuplicateRowConstraint;

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
