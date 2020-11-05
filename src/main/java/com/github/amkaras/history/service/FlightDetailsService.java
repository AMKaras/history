package com.github.amkaras.history.service;

import com.github.amkaras.history.model.FlightDetails;

import java.time.Instant;
import java.util.List;

public interface FlightDetailsService {

    List<FlightDetails> getCheapest(String origin, String destination, Instant from, Instant to);

    List<FlightDetails> getAverage(String origin, String destination, Instant from, Instant to);

    List<FlightDetails> getCurrent(String origin, String destination, Instant from, Instant to);
}
