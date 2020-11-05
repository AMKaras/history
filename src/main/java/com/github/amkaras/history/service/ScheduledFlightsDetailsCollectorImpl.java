package com.github.amkaras.history.service;

import com.github.amkaras.history.dao.CitiesDao;
import com.github.amkaras.history.dao.FlightDetailsRepository;
import com.github.amkaras.history.model.Airline;
import com.github.amkaras.history.model.DirectAirlinesFlightDetails;
import com.github.amkaras.history.model.FlightDetails;
import com.github.amkaras.history.model.Route;
import com.google.common.base.Stopwatch;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.time.ZonedDateTime.now;
import static java.util.stream.Collectors.toMap;
import static org.slf4j.LoggerFactory.getLogger;

@Service
public class ScheduledFlightsDetailsCollectorImpl implements ScheduledFlightsDetailsCollector {

    private static final String ENDPOINT_HTTP_TEMPLATE = "http://%s/flights/%s/%s/%s/%s";
    private static final Logger log = getLogger(ScheduledFlightsDetailsCollectorImpl.class);

    private final FlightDetailsRepository flightDetailsRepository;
    private final CitiesDao citiesDao;
    private final RestTemplate restTemplate = new RestTemplate(httpClientRequestFactory());
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final Map<Airline, String> airlinesEndpoints;

    @Autowired
    public ScheduledFlightsDetailsCollectorImpl(FlightDetailsRepository flightDetailsRepository, CitiesDao citiesDao,
                                                @Value("#{${airlines.urls}}") Map<String, String> airlinesEndpoints) {
        this.flightDetailsRepository = flightDetailsRepository;
        this.citiesDao = citiesDao;
        this.airlinesEndpoints = airlinesEndpoints.entrySet().stream()
                .collect(toMap(entry -> Airline.byName(entry.getKey()), Map.Entry::getValue));
    }

    @Override
    // Every 60 minutes
    @Scheduled(fixedRate = 60 * 60 * 1_000)
    public void collect() {
        Instant dateChecked = Instant.now();
        for (Airline airline : Airline.values()) {
            fetchFlightsForAirline(airline, dateChecked);
        }
    }

    private void fetchFlightsForAirline(Airline airline, Instant dateChecked) {
        int totalNumberOfSavedFlights = 0;
        Stopwatch stopwatch = Stopwatch.createStarted();
        for (Route route : citiesDao.findAllRoutes()) {
            final String requestUrl = format(ENDPOINT_HTTP_TEMPLATE,
                    airlinesEndpoints.get(airline), route.getOrigin(), route.getDestination(),
                    now().format(dateFormatter), now().plusMonths(12).format(dateFormatter));
            try {
                ResponseEntity<DirectAirlinesFlightDetails[]> flightDetailsResponseEntity =
                        restTemplate.getForEntity(requestUrl, DirectAirlinesFlightDetails[].class);
                if (isInvalid(flightDetailsResponseEntity)) {
                    log.error("Direct call to airline offers endpoint failed for airline {}. Status is {}",
                            airline.getName(), flightDetailsResponseEntity.getStatusCode());
                } else {
                    DirectAirlinesFlightDetails[] flightDetailsResponseBody = flightDetailsResponseEntity.getBody();
                    Stream.of(flightDetailsResponseBody)
                            .map(directAirlinesFlightDetails -> FlightDetails
                                    .fromDirectAirlinesDetails(directAirlinesFlightDetails, airline.getName(), dateChecked))
                            .peek(flightDetails ->
                                    log.debug("Saving flight details for {}: \n{}", airline.getName(), flightDetails))
                            .forEach(flightDetailsRepository::save);
                    totalNumberOfSavedFlights += flightDetailsResponseBody.length;
                    log.info("Saved details of {} flights for route from {} to {} using {} endpoint",
                            flightDetailsResponseBody.length, route.getOrigin(), route.getDestination(), requestUrl);
                }
            } catch (Exception e) {
                log.error("Exception occurred when calling flights offers endpoint {} for airline {}",
                        requestUrl, airline.getName(), e);
            }
        }
        log.info("Fetched and saved details for total number of {} flights from {} at {} in {}",
                totalNumberOfSavedFlights, airline, dateChecked, stopwatch.stop());
    }

    private SimpleClientHttpRequestFactory httpClientRequestFactory() {
        SimpleClientHttpRequestFactory clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(10_000);
        clientHttpRequestFactory.setReadTimeout(15_000);
        return clientHttpRequestFactory;
    }

    private boolean isInvalid(ResponseEntity<?> responseEntity) {
        return !responseEntity.getStatusCode().is2xxSuccessful() || !responseEntity.hasBody();
    }
}
