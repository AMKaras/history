package com.github.amkaras.history.dao;

import com.github.amkaras.history.model.Route;

import java.util.List;
import java.util.Set;

public interface CitiesDao {

    Set<String> findAll();

    List<Route> findAllRoutes();
}
