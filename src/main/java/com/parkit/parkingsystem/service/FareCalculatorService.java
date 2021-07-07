package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket){
       if( (ticket.getOutTime() == null) || (ticket.getOutTime().isBefore(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        int inDayTime = ticket.getInTime().getDayOfMonth();
        int outDayTime = ticket.getOutTime().getDayOfMonth();
        int totalDays=0;

        if(inDayTime<outDayTime){
            totalDays= outDayTime-inDayTime;
        }

        long inTime = ticket.getInTime().getMillisOfDay();
        long outTime = ticket.getOutTime().getMillisOfDay();
        double duration;

       if(outTime-inTime>1800000||totalDays!=0){
          duration = (((outTime - inTime)/60000)/60.)+totalDays*24;
       }else duration=0;

        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
              if (ticket.isVehicleSubscribe()) {
                ticket.setPrice(calculateDiscountedFare(duration, ParkingType.CAR));
              } else ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
              break;
            }
          case BIKE: {
            if (ticket.isVehicleSubscribe()) {
              ticket.setPrice(calculateDiscountedFare(duration, ParkingType.BIKE));
            } else ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
            break;
          }
          default:
            throw new IllegalArgumentException("Unknown Parking Type");
        }
    }

  public double calculateDiscountedFare(double duration, ParkingType vehicleType) {
    if (vehicleType == ParkingType.CAR) {
      return (duration * Fare.CAR_RATE_PER_HOUR) - (((duration * Fare.CAR_RATE_PER_HOUR) * Fare.REDUCTION_RATE) / 100);
    } else
      return (duration * Fare.BIKE_RATE_PER_HOUR) - (((duration * Fare.BIKE_RATE_PER_HOUR) * Fare.REDUCTION_RATE) / 100);
  }
}