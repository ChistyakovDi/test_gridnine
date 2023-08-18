package com.gridnine.testing;

import com.gridnine.testing.model.Flight;
import com.gridnine.testing.exception.NullDataException;
import com.gridnine.testing.filter.Filterable;
import com.gridnine.testing.filter.FlightFilter;
import com.gridnine.testing.filter.TimeArrow;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MainTest {

    List<Flight> FLIGHTS = FlightBuilder.createFlights();
    Filterable<Flight> FILTER = new FlightFilter(FLIGHTS);
    //
    Filterable<Flight> WITH_NULL_INPUT = new FlightFilter(null);
    Filterable<Flight> WITH_EMPTY_INPUT = new FlightFilter(Collections.emptyList());

    LocalDateTime NOW = LocalDateTime.now();
    LocalTime FULL_TIME_CONDITION_IS_2_HOURS = LocalTime.of(2, 0);

    @Test
    void listWithDepartureBeforeCurrentTime() {

        List<Flight> result = FILTER.filterFor(TimeArrow.EARLIER_DEP_DATE, NOW);

        assertEquals(5, result.size());
    }

    @Test
    @DisplayName("Segments with correct date-time")
    void segmentsWithArrivalDateEarlierThanTheDepartureDate() {

        final List<Flight> flights = FILTER.filterIncorrectDates();
    }

    @Test
    @DisplayName("Segments with correct date-time")
    void excludeSegmentsWIthTotalGroundTimeMore2Hours() {

        final List<Flight> result = FILTER.filterSummaryTimeMoreThan(FULL_TIME_CONDITION_IS_2_HOURS);

        assertEquals(4, result.size());
    }

    @Nested
    class NullAndEmptyTest {

        @Test
        void throwsExceptionWhenInputIsNull() {

            assertThrows(
                    NullDataException.class,
                    () -> WITH_NULL_INPUT.filterIncorrectDates(),
                    "Input collection is null. Recheck your type."
            );
        }

        @Test
        void returnEmptyListWhenInputListIsEmptyToo() {

            List<Flight> returnedList = WITH_EMPTY_INPUT
                    .filterSummaryTimeMoreThan(FULL_TIME_CONDITION_IS_2_HOURS);

            assertEquals(Collections.emptyList(), returnedList);

        }
    }
}