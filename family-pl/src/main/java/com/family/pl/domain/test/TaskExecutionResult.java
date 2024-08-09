package com.family.pl.domain.test;

import java.time.LocalDate;
import java.util.List;

public class TaskExecutionResult {
    private int executionCount;
    private List<LocalDate> executionDates;

    public TaskExecutionResult(int executionCount, List<LocalDate> executionDates) {
        this.executionCount = executionCount;
        this.executionDates = executionDates;
    }

    public int getExecutionCount() {
        return executionCount;
    }

    public List<LocalDate> getExecutionDates() {
        return executionDates;
    }
}