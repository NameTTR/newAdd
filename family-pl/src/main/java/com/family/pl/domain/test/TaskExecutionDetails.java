package com.family.pl.domain.test;

import java.time.LocalDate;
import java.util.List;

public class TaskExecutionDetails {
    private int count;
    private List<LocalDate> dates;

    public TaskExecutionDetails(int count, List<LocalDate> dates) {
        this.count = count;
        this.dates = dates;
    }

    public int getCount() {
        return count;
    }

    public List<LocalDate> getDates() {
        return dates;
    }
}
