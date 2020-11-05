package com.github.amkaras.history.model;

public enum Airline {

    POLISH_AIRLINES("Polish Airlines"),
    ITALIAN_AIRLINES("Italian Airlines"),
    RUSSIAN_AIRLINES("Russian Airlines");

    private final String name;

    Airline(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Airline byName(String name) {
        for (Airline airline : Airline.values()) {
            if (airline.name.equals(name)) {
                return airline;
            }
        }
        throw new IllegalArgumentException("Airline does not exist for name " + name);
    }
}
