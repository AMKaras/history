package com.github.amkaras.history.dao;

import com.github.amkaras.history.model.Route;
import com.google.common.collect.Sets;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;

@Repository
public class CitiesDaoImpl implements CitiesDao {

    private final Set<String> supportedCities = new HashSet<>();
    private final List<Route> allRoutes = new ArrayList<>();

    @PostConstruct
    private void addLocations() {
        Stream.of("Warsaw", "Cracow", "Rome", "Paris", "London", "Moscow", "Ankara", "Washington", "Sydney", "Tokio")
                .forEach(supportedCities::add);
        Sets.cartesianProduct(this.supportedCities, this.supportedCities).stream()
                .filter(pair -> !pair.get(0).equals(pair.get(1)))
                .map(pair -> new Route(pair.get(0), pair.get(1)))
                .sorted(comparing(Route::getOrigin).thenComparing(Route::getDestination))
                .forEach(allRoutes::add);
    }

    @Override
    public Set<String> findAll() {
        return supportedCities;
    }

    @Override
    public List<Route> findAllRoutes() {
        return allRoutes;
    }
}
