package com.github.betterpoi.constraint;

import com.github.betterpoi.annotation.BPSheet;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.Map;

public interface ColConstraint {
    Map<Integer, String> validate(Sheet sheet, BPSheet bpSheet);
}
