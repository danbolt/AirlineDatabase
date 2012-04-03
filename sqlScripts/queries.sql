-- SQL Queries for Project

-- Number 1
SELECT a.flightNo, b.flightNo
FROM incoming a, outgoing b
WHERE b.plannedDepartureTime > a.plannedArrivalTime AND
b.plannedDepartureTime < a.plannedArrivalTime + INTERVAL 3 HOUR;

-- Number 2
SELECT f.passengerpassport
FROM fliesOn f, (SELECT incomingPlane 
                FROM (SELECT incomingPlane, departureDate,arrivalDate
                        FROM arrivals LEFT OUTER JOIN departures ON
                        arrivals.incomingPlane = departures.outgoingPlane) a
                WHERE NOW() < arrivalDate AND NOW() > departureDate) d
WHERE f.flight_ID = d.incomingPlane;


-- Number 3
SELECT PassengerPassport, COUNT(PassengerPassport) AS FlightsTaken
FROM FliesOn
GROUP BY PassengerPassport
ORDER BY FlightsTaken DESC
LIMIT 3;

-- Number 4 (not completed)
SELECT a.airline_ID, b.locationFrom, b.locationTo
FROM OperatesFlights a, flight B, departures C
WHERE b.flightNo = c.outgoingPlane = a.flightNo AND
c.departureStatus = "Delayed";
-- Then do that query for each route
