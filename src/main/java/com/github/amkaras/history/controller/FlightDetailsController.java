package com.github.amkaras.history.controller;

import com.github.amkaras.history.model.FlightDetails;
import com.github.amkaras.history.service.FlightDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

@RestController
public class FlightDetailsController {

    private static final String UTC = "UTC";
    private final FlightDetailsService flightDetailsService;

    @Autowired
    public FlightDetailsController(FlightDetailsService flightDetailsService) {
        this.flightDetailsService = flightDetailsService;
    }

    @GetMapping("/flights/cheapest/{origin}/{destination}/{from}/{to}")
    public List<FlightDetails> getCheapest(@PathVariable String origin, @PathVariable String destination,
                                           @PathVariable String from, @PathVariable String to) {
        return flightDetailsService.getCheapest(origin, destination, fromString(from), fromString(to));
    }

    @GetMapping("/flights/average/{origin}/{destination}/{from}/{to}")
    public List<FlightDetails> getAverage(@PathVariable String origin, @PathVariable String destination,
                                          @PathVariable String from, @PathVariable String to) {
        return flightDetailsService.getAverage(origin, destination, fromString(from), fromString(to));
    }

    @GetMapping("/flights/current/{origin}/{destination}/{from}/{to}")
    public List<FlightDetails> getCurrent(@PathVariable String origin, @PathVariable String destination,
                                          @PathVariable String from, @PathVariable String to) {
        return flightDetailsService.getCurrent(origin, destination, fromString(from), fromString(to));
    }

    private Instant fromString(String hopefullyValidInstantString) {
        int[] yearMonthDayArray = Arrays.stream(hopefullyValidInstantString.split("-"))
                .mapToInt(Integer::parseInt)
                .toArray();
        return Instant.from(ZonedDateTime.of(yearMonthDayArray[0], yearMonthDayArray[1], yearMonthDayArray[2],
                0, 0, 0, 0, ZoneId.of(UTC)));
    }
}
