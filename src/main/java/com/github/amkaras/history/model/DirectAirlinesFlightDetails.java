package com.github.amkaras.history.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DirectAirlinesFlightDetails {

    private final String origin;
    private final String destination;
    private final ZonedDateTime departure;
    private final ZonedDateTime arrival;
    private final BigDecimal price;
    private final String currency;

    @JsonCreator
    public DirectAirlinesFlightDetails(@JsonProperty("origin") String origin,
                                       @JsonProperty("destination") String destination,
                                       @JsonProperty("departure") ZonedDateTime departure,
                                       @JsonProperty("arrival") ZonedDateTime arrival,
                                       @JsonProperty("price") BigDecimal price,
                                       @JsonProperty("currency") String currency) {
        this.origin = origin;
        this.destination = destination;
        this.departure = departure;
        this.arrival = arrival;
        this.price = price;
        this.currency = currency;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public ZonedDateTime getDeparture() {
        return departure;
    }

    public ZonedDateTime getArrival() {
        return arrival;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getCurrency() {
        return currency;
    }
}
