-- AirlineDB DB + Table Creation SQL Script
-- CSC 370 Project
-- Written by Justin Sketchley and Daniel Savage
-------------------------------------------------------

CREATE DATABASE airlineDB;

--- entity sets start here ---
CREATE TABLE location
(
	location_ID INT PRIMARY KEY,
	name VARCHAR(50)
);

CREATE TABLE flight
(
	flightNo INT PRIMARY KEY,
	locationFrom INT REFERENCES location(location_ID),
	locationTo INT REFERENCES location(location_ID)
);

CREATE TABLE planeModel
(
	plane_ID INT PRIMARY KEY,
	model VARCHAR(20),
	capacity INT
);

CREATE TABLE airline
(
	airline_ID INT PRIMARY KEY,
	name VARCHAR(40),
	website VARCHAR(75)
);

CREATE TABLE incoming
(
	flightNo INT PRIMARY KEY REFERENCES flight(flightNo),
	plannedArrivalTime DATETIME
);

CREATE TABLE outgoing
(
	flightNo INT PRIMARY KEY REFERENCES flight(flightNo),
	plannedDepartureTime DATETIME
);

CREATE TABLE arrivals
(
	arrival_ID INT PRIMARY KEY,
	incomingPlane INT REFERENCES incoming(flightNo),
	gate VARCHAR(10),
	arrivalDate DATETIME,
	arrivalStatus ENUM('Arrived', 'Delayed', 'Cancelled', 'On Time')
);

CREATE TABLE departues
(
	departure_ID INT PRIMARY KEY,
	outgoingPlane INT REFERENCES outgoing(flightNo),
	gate VARCHAR(10),
	departureDate DATETIME,
	departureStatus ENUM('Departed', 'Delayed', 'Cancelled')
);

CREATE TABLE passengers
(
	passPortNumber INT PRIMARY KEY,
	name VARCHAR(40),
	dateOfBirth DATETIME,
	placeOfBirth VARCHAR(50),
	citizenship VARCHAR(30)
)

CREATE TABLE class
(
	class_ID INT PRIMARY KEY,
	classType ENUM('FIRST CLASS', 'ECONOMY', 'HANDICAPPED')
}
--- entity sets end here

--- relationships start here ---

CREATE TABLE departureFliesOn
(
	departure_ID INT REFERENCES departures(departure_ID),
)

--- relationships end here ---
