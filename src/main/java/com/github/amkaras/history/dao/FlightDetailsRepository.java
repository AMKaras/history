package com.github.amkaras.history.dao;

import com.github.amkaras.history.model.FlightDetails;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlightDetailsRepository extends MongoRepository<FlightDetails, String> {

    List<FlightDetails> findByOriginAndDestination(String origin, String destination);

    List<FlightDetails> findByAirline(String airline);
}
