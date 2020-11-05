package com.github.amkaras.history.service;

import com.github.amkaras.history.dao.FlightDetailsRepository;
import com.github.amkaras.history.model.FlightDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
public class FlightDetailsServiceImpl implements FlightDetailsService {

    private static final String USD = "USD";
    private static final long CHEAPEST_FLIGHTS_RESULTS_NUMBER = 1_000;
    private final FlightDetailsRepository flightDetailsRepository;

    @Autowired
    public FlightDetailsServiceImpl(FlightDetailsRepository flightDetailsRepository) {
        this.flightDetailsRepository = flightDetailsRepository;
    }

    @Override
    public List<FlightDetails> getCheapest(String origin, String destination, Instant from, Instant to) {
        return flightDetailsRepository.findByOriginAndDestination(origin, destination)
                .stream()
                .filter(isBetween(from, to))
                .sorted(comparing(FlightDetails::getPrice))
                .limit(CHEAPEST_FLIGHTS_RESULTS_NUMBER)
                .collect(toList());
    }

    @Override
    public List<FlightDetails> getAverage(String origin, String destination, Instant from, Instant to) {
        Map<String, List<FlightDetails>> detailsByAirlines =
                flightDetailsRepository.findByOriginAndDestination(origin, destination)
                        .stream()
                        .filter(isBetween(from, to))
                        .collect(groupingBy(FlightDetails::getAirline));
        return detailsByAirlines.entrySet()
                .stream()
                .map(entry -> new FlightDetails(entry.getKey(), origin, destination, null, null,
                        averagePriceFrom(entry.getValue()), USD, null))
                .collect(toList());
    }

    @Override
    public List<FlightDetails> getCurrent(String origin, String destination, Instant from, Instant to) {
        Map<Instant, List<FlightDetails>> detailsByDateChecked =
                flightDetailsRepository.findByOriginAndDestination(origin, destination)
                        .stream()
                        .filter(isBetween(from, to))
                        .collect(groupingBy(FlightDetails::getDateChecked));
        Instant latestChecked = detailsByDateChecked.keySet()
                .stream()
                .max(naturalOrder())
                .get();
        return detailsByDateChecked.get(latestChecked);
    }

    private BigDecimal averagePriceFrom(List<FlightDetails> flightDetails) {
        BigDecimal sum = flightDetails.stream()
                .map(FlightDetails::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(BigDecimal.valueOf(flightDetails.size()), RoundingMode.HALF_UP);
    }

    private Predicate<FlightDetails> isBetween(Instant departure, Instant arrival) {
        return flightDetails -> flightDetails.getDeparture().isAfter(departure) &&
                flightDetails.getArrival().isBefore(arrival.plus(1, DAYS));
    }
}
