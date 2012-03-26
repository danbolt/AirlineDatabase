-- AirlineDB DB + Table Creation SQL Script
-- CSC 370 Project
-- Written by Justin Sketchley and Daniel Savage
-------------------------------------------------------

CREATE DATABASE airlineDB;

--- entity sets start here ---
CREATE TABLE location
(
	location_ID INT NOT NULL AUTO_INCREMENT,
	name VARCHAR(50),
	PRIMARY KEY(location_ID)
);

CREATE TABLE planeModel
(
	plane_ID INT NOT NULL AUTO_INCREMENT,
	model VARCHAR(20),
	capacity INT,
	PRIMARY KEY(plane_ID)	
);

CREATE TABLE flight
(
	flightNo INT NOT NULL AUTO_INCREMENT,
	locationFrom INT,
	locationTo INT,
	planeModel INT,
	PRIMARY KEY(flightNo),
	FOREIGN KEY(locationFrom) REFERENCES location(location_ID),
	FOREIGN KEY(locationTo) REFERENCES location(location_ID),
	FOREIGN KEY(planeModel) REFERENCES planeModel(plane_ID)
);

CREATE TABLE airline
(
	airline_ID INT NOT NULL AUTO_INCREMENT,
	name VARCHAR(40),
	website VARCHAR(75),
	PRIMARY KEY(airline_ID)
);

CREATE TABLE incoming
(
	flightNo INT,
	plannedArrivalTime DATETIME,
	PRIMARY KEY(flightNo),
	FOREIGN KEY(flightNo) REFERENCES flight(flightNo)
);

CREATE TABLE outgoing
(
	flightNo INT,
	plannedDepartureTime DATETIME,
	PRIMARY KEY(flightNo),
	FOREIGN KEY(flightNo) REFERENCES flight(flightNo)
);

CREATE TABLE arrivals
(
	arrival_ID INT NOT NULL AUTO_INCREMENT,
	incomingPlane INT,
	gate VARCHAR(10),
	arrivalDate DATETIME,
	arrivalStatus ENUM('Arrived', 'Delayed', 'Cancelled', 'On Time'),
	PRIMARY KEY(arrival_ID),
	FOREIGN KEY(incomingPlane) REFERENCES incoming(flightNo)
);

CREATE TABLE departures
(
	departure_ID INT NOT NULL AUTO_INCREMENT,
	outgoingPlane INT,
	gate VARCHAR(10),
	departureDate DATETIME,
	departureStatus ENUM('Departed', 'Delayed', 'Cancelled'),
	PRIMARY KEY(departure_ID),
	FOREIGN KEY(outgoingPlane) REFERENCES outgoing(flightNo)
);

CREATE TABLE passengers
(
	passportNumber INT NOT NULL,
	name VARCHAR(40),
	dateOfBirth DATETIME,
	placeOfBirth VARCHAR(50),
	citizenship VARCHAR(30),
	PRIMARY KEY(passportNumber)
);

CREATE TABLE class
(
	class_ID INT NOT NULL AUTO_INCREMENT,
	classType ENUM('FIRST CLASS', 'ECONOMY', 'HANDICAPPED'),
	PRIMARY KEY(class_ID)
);
--- entity sets end here

--- relationships start here ---

CREATE TABLE departureFliesOn
(
	departure_ID INT,
	passengerPassport INT,
	baggage INT,
	class_ID INT,
	PRIMARY KEY(departure_ID,passengerPassport),
	FOREIGN KEY(departure_ID) REFERENCES departures(departure_ID),
	FOREIGN KEY(passengerPassport) REFERENCES passengers(passportNumber),
	FOREIGN KEY(class_ID) REFERENCES class(class_ID)	
);

CREATE TABLE arrivalFliesOn
(
	arrival_ID INT,
	passengerPassport INT,
	baggage INT,
	class_ID INT,
	PRIMARY KEY(arrival_ID,passengerPassport),
	FOREIGN KEY(arrival_ID) REFERENCES arrivals(arrival_ID),
	FOREIGN KEY(passengerPassport) REFERENCES passengers(passportNumber),
	FOREIGN KEY(class_ID) REFERENCES class(class_ID)	
);

CREATE TABLE OperatesFlights
(
    airline_ID INT,
    flightNo INT,
    PRIMARY KEY(airline_ID,flightNo),
    FOREIGN KEY(airline_ID) REFERENCES airline(airline_ID),
    FOREIGN KEY(flightNo) REFERENCES flight(flightNo)
);
--- relationships end here ---
