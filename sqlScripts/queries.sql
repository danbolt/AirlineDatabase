-- SQL Queries for Project

-- Number 1
SELECT a.flightNo, b.flightNo
FROM incoming a, outgoing b
WHERE b.plannedDepartureTime > a.plannedArrivalTime AND
b.plannedDepartureTime < a.plannedArrivalTime + INTERVAL 3 HOUR;

-- Number 2 (Not completed)
SELECT a.PassengerPassport
FROM fliesOn a, arrivals b, departures c
WHERE c.departureStatus = "Departed" AND b.arrivalStatus != "Arrived" AND
a.flight_ID = b.incomingPlane = c.outgoingPlane;


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
