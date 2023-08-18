package com.gridnine.testing.filter;

import com.gridnine.testing.model.Flight;
import com.gridnine.testing.model.Segment;
import com.gridnine.testing.exception.NullDataException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This class provides filtering capabilities for collections of Flight objects based on various criteria.
 * It implements the Filterable interface and contains methods to filter flights by departure and arrival times,
 * incorrect segment dates, and total segment durations.
 */
public class FlightFilter implements Filterable<Flight> {

    private final List<Flight> flights;

    public FlightFilter(List<Flight> flights) {
        this.flights = flights;
    }

    /**
     * Filters the list of flights based on the specified time and time arrow direction.
     * @param pointer The time arrow direction (EARLIER_DEP_DATE, LATER_DEP_DATE, EARLIER_ARR_DATE, LATER_ARR_DATE).
     * @param time The reference time for filtering.
     * @return A filtered list of Flight objects based on the specified criteria.
     * @throws NullDataException if the input collection 'flights' is null.
     */
    public List<Flight> filterFor(TimeArrow pointer, LocalDateTime time) {

        if (Objects.isNull(flights)) {
            throw new NullDataException("Input collection is null. Recheck your type.");
        }

        if (flights.isEmpty()) {
            return Collections.emptyList();
        }

        List<Flight> res = flights;

        switch (pointer) {
            case EARLIER_DEP_DATE -> res = filterEarlierDep(time);
            case LATER_DEP_DATE -> res = filterLaterDep(time);
            case EARLIER_ARR_DATE -> res = filterEarlierArr(time);
            case LATER_ARR_DATE -> res = filterLaterArr(time);
        }
        return res;
    }

    /**
     * Filters a collection of flights to remove those with incorrect segment dates.
     * An incorrect segment date is defined as an arrival date that is not after the departure date.
     *
     * @return A filtered list of Flight objects containing only those flights with correct segment dates.
     * @throws NullDataException if the input collection 'flights' is null.
     */
    @Override
    public List<Flight> filterIncorrectDates() {

        if (Objects.isNull(flights)) {
            throw new NullDataException("Input collection is null. Recheck your type.");
        }

        if (flights.isEmpty()) {
            return Collections.emptyList();
        }

        List<Flight> filteredFlight = new ArrayList<>();

        for (Flight flight : flights) {

            List<Segment> filtered = flight.getSegments()
                    .stream()
                    .filter(segment -> segment.getArrivalDate().isAfter(segment.getDepartureDate()))
                    .collect(Collectors.toList());

            if (!filtered.isEmpty()) {
                filteredFlight.add(new Flight(filtered));
            }
        }

        return filteredFlight;
    }

    /**
     * Filters a collection of flights to include only those with total segment durations greater than the specified LocalTime.
     *
     * @param time The reference LocalTime to compare total segment durations against.
     * @return A list of Flight objects containing only those flights with total segment durations greater than the given time.
     * @throws NullDataException if the input collection 'flights' is null.
     */
    @Override
    public List<Flight> filterSummaryTimeMoreThan(LocalTime time) {

        if (Objects.isNull(flights)) {
            throw new NullDataException("Input collection is null. Recheck your type.");
        }

        if (flights.isEmpty()) {
            return Collections.emptyList();
        }

        return flights
                .parallelStream()
                .filter(flight -> this
                        .isMoreThan(flight, time))
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Filters a list of flights to include only those with segments departing after the specified time.
     *
     * @param time The reference time used for filtering.
     * @return A list of Flight objects containing only those flights with segments departing after the given time.
     */
    private List<Flight> filterEarlierDep(LocalDateTime time) {

        List<Flight> filteredFlight = new ArrayList<>();

        for (Flight flight : flights) {

            List<Segment> filtered = flight.getSegments()
                    .stream()
                    .filter(segment -> segment.getDepartureDate().isAfter(time))
                    .collect(Collectors.toList());

            if (!filtered.isEmpty()) {
                filteredFlight.add(new Flight(filtered));
            }
        }

        return filteredFlight;
    }

    /**
     * Filters a list of flights to include only those with segments departing after the specified time.
     *
     * @param time The reference time used for filtering.
     * @return A list of Flight objects containing only those flights with segments departing after the given time.
     */
    private List<Flight> filterEarlierArr(LocalDateTime time) {

        List<Flight> filteredFlight = new ArrayList<>();

        for (Flight flight : flights) {

            List<Segment> filtered = flight.getSegments()
                    .stream()
                    .filter(segment -> segment.getArrivalDate().isAfter(time))
                    .collect(Collectors.toList());

            if (!filtered.isEmpty()) {
                filteredFlight.add(new Flight(filtered));
            }
        }

        return filteredFlight;
    }

    /**
     * Filters a list of flights to include only those with segments departing before the specified time.
     *
     * @param time The reference time used for filtering.
     * @return A list of Flight objects containing only those flights with segments departing before the given time.
     */
    private List<Flight> filterLaterDep(LocalDateTime time) {

        List<Flight> filteredFlight = new ArrayList<>();

        for (Flight flight : flights) {

            List<Segment> filtered = flight.getSegments()
                    .stream()
                    .filter(segment -> segment.getDepartureDate().isBefore(time))
                    .collect(Collectors.toList());

            if (!filtered.isEmpty()) {
                filteredFlight.add(new Flight(filtered));
            }
        }

        return filteredFlight;
    }

    /**
     * Filters a list of flights to include only those with segments arriving before the specified time.
     *
     * @param time The reference time used for filtering.
     * @return A list of Flight objects containing only those flights with segments arriving before the given time.
     */
    private List<Flight> filterLaterArr(LocalDateTime time) {

        List<Flight> filteredFlight = new ArrayList<>();

        for (Flight flight : flights) {

            List<Segment> filtered = flight.getSegments()
                    .stream()
                    .filter(segment -> segment.getArrivalDate().isBefore(time))
                    .collect(Collectors.toList());

            if (!filtered.isEmpty()) {
                filteredFlight.add(new Flight(filtered));
            }
        }

        return filteredFlight;
    }

    /**
     * This class contains a utility method for determining whether the total duration of segments in a given flight
     * is greater than a specified LocalTime.
     */
    boolean isMoreThan(Flight flight, LocalTime localTime) {

        List<Segment> segments = flight.getSegments();
        List<Duration> durations = new ArrayList<>();

        for (int i = 0; i < segments.size() - 1; i++) {
            Segment current = segments.get(i);
            Segment next = segments.get(i + 1);
            durations.add(Duration.between(current.getArrivalDate(), next.getDepartureDate()).abs());
        }

        long summaryTime = durations.stream()
                .map(Duration::toSeconds)
                .reduce(Long::sum)
                .orElse(0L);

        return summaryTime < localTime.toSecondOfDay();
    }
}