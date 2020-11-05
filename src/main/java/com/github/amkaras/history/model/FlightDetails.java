package com.github.amkaras.history.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Document
public class FlightDetails {

    @Id
    @JsonIgnore
    private String id;

    private final String airline;
    private final String origin;
    private final String destination;
    private final Instant departure;
    private final Instant arrival;
    private final BigDecimal price;
    private final String currency;
    private final Instant dateChecked;

    @JsonCreator
    public FlightDetails(
            @JsonProperty("airline") String airline,
            @JsonProperty("origin") String origin,
            @JsonProperty("destination") String destination,
            @JsonProperty("departure") Instant departure,
            @JsonProperty("arrival") Instant arrival,
            @JsonProperty("price") BigDecimal price,
            @JsonProperty("currency") String currency,
            @JsonProperty("dateChecked") Instant dateChecked) {
        this.airline = airline;
        this.origin = origin;
        this.destination = destination;
        this.departure = departure;
        this.arrival = arrival;
        this.price = price;
        this.currency = currency;
        this.dateChecked = dateChecked;
    }

    public String getAirline() {
        return airline;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public Instant getDeparture() {
        return departure;
    }

    public Instant getArrival() {
        return arrival;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getCurrency() {
        return currency;
    }

    public Instant getDateChecked() {
        return dateChecked;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static FlightDetails fromDirectAirlinesDetails(DirectAirlinesFlightDetails directAirlinesDetails,
                                                          String airline, Instant dateChecked) {
        return new FlightDetails(airline, directAirlinesDetails.getOrigin(), directAirlinesDetails.getDestination(),
                directAirlinesDetails.getDeparture().toInstant(), directAirlinesDetails.getArrival().toInstant(),
                directAirlinesDetails.getPrice(), directAirlinesDetails.getCurrency(), dateChecked);
    }

    @Override
    public String toString() {
        return "FlightDetails{" +
                "id='" + id + '\'' +
                ", airline='" + airline + '\'' +
                ", origin='" + origin + '\'' +
                ", destination='" + destination + '\'' +
                ", departure=" + departure +
                ", arrival=" + arrival +
                ", price=" + price +
                ", currency='" + currency + '\'' +
                ", dateChecked=" + dateChecked +
                '}';
    }
}
