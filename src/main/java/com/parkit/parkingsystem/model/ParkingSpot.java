package com.parkit.parkingsystem.model;

import com.parkit.parkingsystem.constants.ParkingType;

/**
 Parking spot model permit to create and update the informations of this parking spot (place number, parking type (car or
 bike), availability).
 */
public class ParkingSpot {
  private int number;
  private ParkingType parkingType;
  private boolean isAvailable;

  /**
   Constructor of ParkingSpot object.

   @param number      The place number.
   @param parkingType The parking type (car or bike).
   @param isAvailable The availability of this place.
   */
  public ParkingSpot(int number, ParkingType parkingType, boolean isAvailable) {
    this.number = number;
    this.parkingType = parkingType;
    this.isAvailable = isAvailable;
  }

  /**
   Get the identifiant of the place number.

   @return The identifiant of the place number.
   */
  public int getId() {
    return number;
  }

  /**
   Set the identifiant of the place number. (dead code)

   @param number The identifiant of the place number.
   */
  public void setId(int number) {
    this.number = number;
  }

  /**
   Get the parking type of this place.

   @return ParkingType object

   @see ParkingType
   */
  public ParkingType getParkingType() {
    return parkingType;
  }

  /**
   Set the parking type of this place. (dead code)

   @param parkingType The parking type oh this place.

   @see ParkingType
   */
  public void setParkingType(ParkingType parkingType) {
    this.parkingType = parkingType;
  }

  /**
   The statement of the availability of this place.

   @return True if is available.
   */
  public boolean isAvailable() {
    return isAvailable;
  }

  /**
   Set the statement of the availability of this place.

   @param available True if is available.
   */
  public void setAvailable(boolean available) {
    isAvailable = available;
  }

  /**
   Compare two ParkingType objects.
   <p>
   {@inheritDoc}

   @return True if these two objects are equals.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ParkingSpot that = (ParkingSpot) o;
    return number == that.number;
  }

  /**
   Hash the number of the number parking place.
   <p>
   {@inheritDoc}

   @return The number of the parking place.
   */
  @Override
  public int hashCode() {
    return number;
  }
}
