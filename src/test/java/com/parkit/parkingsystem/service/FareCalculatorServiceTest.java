package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.joda.time.LocalDateTime;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FareCalculatorServiceTest {
    private static FareCalculatorService fareCalculatorService;
    private Ticket ticket;

    @Mock
    ParkingSpot parkingSpot;

    @BeforeAll
    private static void setUp() {
        fareCalculatorService = new FareCalculatorService();
    }

    @BeforeEach
    private void setUpPerTest() {
        ticket = new Ticket();
    }

    @Test
    public void calculateFareCar(){
        LocalDateTime inTime = new LocalDateTime().minusHours(1);
        LocalDateTime outTime = new LocalDateTime();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        fareCalculatorService.calculateFare(ticket);

        assertEquals(Fare.CAR_RATE_PER_HOUR, ticket.getPrice());
    }

    @Test
    public void calculateFareBike(){
        LocalDateTime inTime = new LocalDateTime().minusHours(1);
        LocalDateTime outTime = new LocalDateTime();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        fareCalculatorService.calculateFare(ticket);

        assertEquals(Fare.BIKE_RATE_PER_HOUR, ticket.getPrice());
    }

    @Test
    public void calculateFareUnknownType(){
        LocalDateTime inTime = new LocalDateTime();
        LocalDateTime outTime = new LocalDateTime();
        ParkingSpot parkingSpot = new ParkingSpot(1, null,false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareBikeWithFutureInTime() {
        LocalDateTime inTime = new LocalDateTime().plusHours(1);
        LocalDateTime outTime = new LocalDateTime();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket), "Out time provided is incorrect:" + ticket.getOutTime().toString());
    }

    @Test
    public void calculateFareBikeWithLessThanOneHourParkingTime(){
        LocalDateTime inTime = new LocalDateTime().minusMinutes(45); //45 minutes parking time should give 3/4th parking fare
        LocalDateTime outTime = new LocalDateTime();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        fareCalculatorService.calculateFare(ticket);

        assertEquals((0.75 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice() );
    }

    @Test
    public void calculateFareCarWithLessThanOneHourParkingTime(){
        LocalDateTime inTime = new LocalDateTime().minusMinutes(45);//45 minutes parking time should give 3/4th parking fare
        LocalDateTime outTime = new LocalDateTime();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        fareCalculatorService.calculateFare(ticket);

        assertEquals( (0.75 * Fare.CAR_RATE_PER_HOUR) , ticket.getPrice());
    }

    @Test
    public void calculateFareCarWithMoreThanADayParkingTime(){
        LocalDateTime inTime = new LocalDateTime().minusHours(24);//24 hours parking time should give 24 * parking fare per hour
        LocalDateTime outTime = new LocalDateTime();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        fareCalculatorService.calculateFare(ticket);

        assertEquals( (24 * Fare.CAR_RATE_PER_HOUR) , ticket.getPrice());
    }

    @Test
    public void calculateFareCarWithParkBetweenTwoDates(){
        LocalDateTime inTime = new LocalDateTime(2020, 6, 5, 5, 30);
        LocalDateTime outTIme = new LocalDateTime(2020, 6, 6, 2, 0);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTIme);
        ticket.setParkingSpot(parkingSpot);

        fareCalculatorService.calculateFare(ticket);

        assertEquals((20.50 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
    }

    @Test
    public void calculateFareCarWithLessThirtyMinutes() {
        LocalDateTime inTime = new LocalDateTime().minusMinutes(29);
        LocalDateTime outTime = new LocalDateTime();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        fareCalculatorService.calculateFare(ticket);

        assertEquals(0, ticket.getPrice());
    }

    @Test
    public void calculateDiscountedFareCarForOneHour() {
        double duration = 1.;
        ParkingType parkingType = ParkingType.CAR;

        assertEquals(1.425, fareCalculatorService.calculateDiscountedFare(duration, parkingType));
    }

    @Test
    public void calculateDiscountedFareBikeForOneHour() {
        double duration = 1.;
        ParkingType parkingType = ParkingType.BIKE;

        assertEquals(0.95, fareCalculatorService.calculateDiscountedFare(duration, parkingType));
    }

    @Test
    public void calculateFareForSubscribeCarForOneHour() {
        LocalDateTime inTime = new LocalDateTime().minusHours(1);
        LocalDateTime outTime = new LocalDateTime();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleSubscribe(true);

        fareCalculatorService.calculateFare(ticket);

        assertEquals(1.425, ticket.getPrice());
    }

    @Test
    public void calculateFareForSubscribeBikeForOneHour() {
        LocalDateTime inTime = new LocalDateTime().minusHours(1);
        LocalDateTime outTime = new LocalDateTime();
        ParkingSpot parkingSpot = new ParkingSpot(4, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleSubscribe(true);

        fareCalculatorService.calculateFare(ticket);
        assertEquals(0.95, ticket.getPrice());
    }

    @Test
    public void impossibleCalculateFareUnKnownParkingType() {
        LocalDateTime inTime = new LocalDateTime().minusHours(1);
        LocalDateTime outTime = new LocalDateTime();
        parkingSpot = new ParkingSpot(1, ParkingType.TEST, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket), "Unknown Parking Type");
    }
}
