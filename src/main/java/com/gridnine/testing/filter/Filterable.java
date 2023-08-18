package com.gridnine.testing.filter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public interface Filterable<T> {

    List<T> filterIncorrectDates();

    List<T> filterFor(TimeArrow pointer, LocalDateTime time);

    List<T> filterSummaryTimeMoreThan(LocalTime time);
}