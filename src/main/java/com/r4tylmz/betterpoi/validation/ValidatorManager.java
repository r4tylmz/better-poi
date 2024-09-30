package com.r4tylmz.betterpoi.validation;

import com.r4tylmz.betterpoi.annotation.BPSheet;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.List;
import java.util.Map;

public interface ValidatorManager {
    String getErrorMessage(Map<Integer, String> violationMap);

    List<String> validate(Sheet sheet, BPSheet bpSheet);
}
