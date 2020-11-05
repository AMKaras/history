package com.github.amkaras.history.model;

import java.util.Objects;

public class Route {

    private final String origin;
    private final String destination;

    public Route(String origin, String destination) {
        this.origin = origin;
        this.destination = destination;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Route route = (Route) o;
        return getOrigin().equals(route.getOrigin()) &&
                getDestination().equals(route.getDestination());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOrigin(), getDestination());
    }

    @Override
    public String toString() {
        return "Route{" +
                "origin='" + origin + '\'' +
                ", destination='" + destination + '\'' +
                '}';
    }
}
