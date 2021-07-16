package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.LocalDateTime;

import java.sql.*;

/**
 This class manages all methods for the Ticket Data object.
 */
public class TicketDAO {

    private static final Logger logger = LogManager.getLogger("TicketDAO");

    /**
     A new instance of DataBaseConfig.
     */
    public DataBaseConfig dataBaseConfig = new DataBaseConfig();

    /**
     Saves a ticket of this vehicle on the database.

     @param ticket The ticket of this vehicle.
     @return The ticket of this vehicle.
     @see Ticket
     */
    public boolean saveTicket(Ticket ticket) {
        Connection con = null;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.SAVE_TICKET);
            //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
            //ps.setInt(1,ticket.getId());
            ps.setInt(1, ticket.getParkingSpot().getId());
            ps.setString(2, ticket.getVehicleRegNumber());
            ps.setDouble(3, ticket.getPrice());
            ps.setTimestamp(4, new Timestamp((ticket.getInTime().toDateTime().getMillis())));
            ps.setTimestamp(5, (ticket.getOutTime() == null) ? null : (new Timestamp(ticket.getOutTime().toDateTime().getMillis())));
            ps.execute();
            return true;
        } catch (Exception ex) {
            logger.error("Error fetching next available slot", ex);
            return false;
        } finally {
            dataBaseConfig.closeConnection(con);
        }
    }

    /**
     Gets ticket of this vehicle.

     @param vehicleRegNumber The vehicle registration number.

     @return The ticket of this vehicle.

     @see Ticket
     */
    public Ticket getTicket(String vehicleRegNumber) {
        Connection con = null;
        Ticket ticket = null;

        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.GET_TICKET);
            //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
            ps.setString(1, vehicleRegNumber);
            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                ticket = new Ticket();
                ParkingSpot parkingSpot = new ParkingSpot(rs.getInt(1), ParkingType.valueOf(rs.getString(6)), false);
                ticket.setParkingSpot(parkingSpot);
                ticket.setId(rs.getInt(2));
                ticket.setVehicleRegNumber(vehicleRegNumber);
                ticket.setPrice(rs.getDouble(3));
                ticket.setInTime(LocalDateTime.fromDateFields(rs.getTimestamp(4)));
                ticket.setOutTime(LocalDateTime.now());
            }
            dataBaseConfig.closeResultSet(rs);
            dataBaseConfig.closePreparedStatement(ps);
        } catch (Exception ex) {
            logger.error("Error fetching next available slot", ex);
        } finally {
            dataBaseConfig.closeConnection(con);
            return ticket;
        }
    }

    /**
     Updates ticket of this vehicle.

     @param ticket the ticket.

     @return True if update is validated.

     @see Ticket
     */
    public boolean updateTicket(Ticket ticket) {
        Connection con = null;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.UPDATE_TICKET);
            ps.setDouble(1, ticket.getPrice());
            ps.setTimestamp(2, new Timestamp((ticket.getOutTime().toDateTime().getMillis())));
            ps.setInt(3, ticket.getId());
            ps.execute();
            return true;
        } catch (Exception ex) {
            logger.error("Error saving ticket info", ex);
        } finally {
            dataBaseConfig.closeConnection(con);
        }
        return false;
    }

    /**
     Searches vehicle subscribe.

     @param vehicleNumber The vehicle registration number.

     @return True if the vehicle registration number exist on the database.
     */
    public boolean searchVehicleSubscribe(String vehicleNumber) {
        Connection con = null;
        int inComingNumber = 0;

        try {
            con = dataBaseConfig.getConnection();
            Statement statement = con.createStatement();
            ResultSet response = statement.executeQuery("select VEHICLE_REG_NUMBER,OUT_TIME from TICKET");


            while (response.next()) {
                String vehicleResponse = response.getString(1);
                Date outTimeResponse = response.getDate(2);

                if (vehicleResponse.equals(vehicleNumber) && outTimeResponse != null) {
                    inComingNumber++;
                }
            }
            if (inComingNumber >= 1) return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            dataBaseConfig.closeConnection(con);
        }
        return false;
    }
}
