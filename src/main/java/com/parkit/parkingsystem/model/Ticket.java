package com.parkit.parkingsystem.model;

import org.joda.time.LocalDateTime;

/**
 Ticket model permit save and update the informations of this vehicle (Id, Parking spot, vehicle registration, price of
 parking, datetime In and Out, vehicle subscribe or not.
 */
public class Ticket {
    private int id;
    private ParkingSpot parkingSpot;
    private String vehicleRegNumber;
    private double price;
    private LocalDateTime inTime;
    private LocalDateTime outTime;
    private boolean vehicleSubscribe;

    /**
     Get parking ticket identifiant.

     @return ticket number identifiant
     */
    public int getId() {
        return id;
    }

    /**
     Set parking ticket identifiant.

     @param id ticket number identifiant
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     Get the parking spot of this vehicle (number of place, type of place, availability).

     @return parking spot object
     */
    public ParkingSpot getParkingSpot() {
        return parkingSpot;
    }

    /**
     Set the parking spot of this vehicle (number of place, type of place, availability).

     @param parkingSpot parking spot object
     */
    public void setParkingSpot(ParkingSpot parkingSpot) {
        this.parkingSpot = parkingSpot;
    }

    /**
     Get the vehicle registration of this vehicle.

     @return the vehicle registration.
     */
    public String getVehicleRegNumber() {
        return vehicleRegNumber;
    }

    /**
     Set the vehicle registration of this vehicle

     @param vehicleRegNumber the vehicle registration
     */
    public void setVehicleRegNumber(String vehicleRegNumber) {
        this.vehicleRegNumber = vehicleRegNumber;
    }

    /**
     Get the price of this parking ticket.

     @return the price to pay.
     */
    public double getPrice() {
        return price;
    }

    /**
     Set the price of this parking ticket.

     @param price The price to pay.
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     Get the datetime of the vehicle entrance

     @return The datetime of the entrance

     @see LocalDateTime Joda-time object
     */
    public LocalDateTime getInTime() {
        return inTime;
    }

    /**
     Set the datetime of the vehicle entrance.

     @param inTime LocaleDateTime object.

     @see LocalDateTime Joda-time object.
     */
    public void setInTime(LocalDateTime inTime) {
        this.inTime = inTime;
    }

    /**
     Get the datetime of the exit vehicle.

     @return the datetime of the exit vehicle.

     @see LocalDateTime Joda-time object
     */
    public LocalDateTime getOutTime() {
        return outTime;
    }

    /**
     Set the datetime of the exit vehicle.

     @param outTime the datetime of the exit vehicle.

     @see LocalDateTime Joda-time object.
     */
    public void setOutTime(LocalDateTime outTime) {
        this.outTime = outTime;
    }

    /**
     Get the statement of the vehicle subscribe.

     @return True if the vehicle is subscribed.
     */
    public boolean isVehicleSubscribe() {
        return vehicleSubscribe;
    }

    /**
     Set the statement of the vehicle subscribe.

     @param vehicleSubscribe True if the vehicle is subscribed.
     */
    public void setVehicleSubscribe(boolean vehicleSubscribe) {
        this.vehicleSubscribe = vehicleSubscribe;
    }
}
