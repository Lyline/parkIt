package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

  private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
  private static ParkingSpotDAO parkingSpotDAO;
  private static TicketDAO ticketDAO;
  private static DataBasePrepareService dataBasePrepareService;

  private String vehicleNumberResponse = "";
  private int parkingNumberResponse = 0;
  private double priceResponse = 0;
  private String outTimeResponse;
  private String inTimeResponse;
  private DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-mm-dd H:mm");

  @Mock
  private static InputReaderUtil inputReaderUtil;

  @BeforeAll
  private static void setUp() throws Exception {
    parkingSpotDAO = new ParkingSpotDAO();
    parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
    ticketDAO = new TicketDAO();
    ticketDAO.dataBaseConfig = dataBaseTestConfig;
    dataBasePrepareService = new DataBasePrepareService();
  }

  @AfterAll
  private static void tearDown() {

  }

  @Test
  public void testParkingACarIT() throws Exception {
    dataBasePrepareService.clearDataBaseEntries();
    ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

    when(inputReaderUtil.readSelection()).thenReturn(1);
    when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

    Connection con = dataBaseTestConfig.getConnection();

    parkingService.processIncomingVehicle();

    try {
      PreparedStatement ps = con.prepareStatement
          ("select PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME FROM ticket");
      ResultSet rs = ps.executeQuery();

      while (rs.next()) {
        vehicleNumberResponse = rs.getString("VEHICLE_REG_NUMBER");
        parkingNumberResponse = rs.getInt("PARKING_NUMBER");
        priceResponse = rs.getDouble("PRICE");
        inTimeResponse = rs.getTimestamp("IN_TIME").toLocalDateTime()
            .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-mm-dd H:mm"));
      }
    } catch (SQLException throwables) {
      throwables.printStackTrace();
    } finally {
      dataBaseTestConfig.closeConnection(con);
    }

    assertEquals("ABCDEF", vehicleNumberResponse);
    assertEquals(1, parkingNumberResponse);
    assertEquals(0.0, priceResponse);
    assertEquals(LocalDateTime.now().toString(fmt), inTimeResponse);
  }

  @Test
  public void testParkingLotExitIT() throws Exception {
    dataBasePrepareService.clearDataBaseEntries();
    Connection con = null;

    try {
      con = dataBaseTestConfig.getConnection();
      PreparedStatement psParking = con.prepareStatement
          ("update parking set AVAILABLE = 0 where PARKING_NUMBER=1");
      psParking.executeUpdate();
      Statement psTicket = con.createStatement();
      psTicket.executeUpdate
          ("insert into ticket(id,parking_number,vehicle_reg_number,price,in_time,out_time)" +
              "value (1,1,'ABCDEF',0.0,DATE_SUB(CURRENT_TIME ,INTERVAL 1 HOUR),null )");
    } catch (SQLException throwables) {
      throwables.printStackTrace();
    } finally {
      dataBaseTestConfig.closeConnection(con);
    }

    when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

    ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
    parkingService.processExitingVehicle();

    try {
      con = dataBaseTestConfig.getConnection();
      PreparedStatement ps = con.prepareStatement
          ("select PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, OUT_TIME FROM ticket");
      ResultSet rs = ps.executeQuery();

      while (rs.next()) {
        vehicleNumberResponse = rs.getString("VEHICLE_REG_NUMBER");
        parkingNumberResponse = rs.getInt("PARKING_NUMBER");
        priceResponse = rs.getDouble("PRICE");
        outTimeResponse = rs.getTimestamp("OUT_TIME")
            .toLocalDateTime().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-mm-dd H:mm"));
      }
    } catch (SQLException throwables) {
      throwables.printStackTrace();
    } finally {
      dataBaseTestConfig.closeConnection(con);
    }

    assertEquals("ABCDEF", vehicleNumberResponse);
    assertEquals(1, parkingNumberResponse);
    assertEquals(1.5, priceResponse);
    assertEquals(LocalDateTime.now().toString(fmt), outTimeResponse);
  }

  @Test
  public void testVehicleSubscribeIT() {
    dataBasePrepareService.clearDataBaseEntries();
    Connection con = null;
    String vehicleRequest = "ABCDEF";
    try {
      con = dataBaseTestConfig.getConnection();
      Statement psTicket = con.createStatement();
      psTicket.executeUpdate
          ("insert into ticket(id,parking_number,vehicle_reg_number,price,in_time,out_time)" +
              "value (1,1,'ABCDEF',0.0,DATE_SUB(CURRENT_TIME ,INTERVAL 4 HOUR),DATE_SUB(CURRENT_TIME ,INTERVAL 3 HOUR))");
    } catch (SQLException | ClassNotFoundException throwables) {
      throwables.printStackTrace();
    } finally {
      dataBaseTestConfig.closeConnection(con);
    }

    boolean response = ticketDAO.searchVehicleSubscribe(vehicleRequest);

    assertEquals(true, response);
  }

  @Test
  public void testCarParkingFareDiscountedIT() throws Exception {
    dataBasePrepareService.clearDataBaseEntries();
    Connection con = null;

    try {
      con = dataBaseTestConfig.getConnection();
      Statement psTicket = con.createStatement();
      psTicket.executeUpdate
          ("insert into ticket(id,parking_number,vehicle_reg_number,price,in_time,out_time)" +
              "value (1,1,'ABCDEF',1.5,DATE_SUB(CURRENT_TIME ,INTERVAL 8 HOUR),DATE_SUB(CURRENT_TIME ,INTERVAL 7 HOUR))");
    } catch (SQLException | ClassNotFoundException throwables) {
      throwables.printStackTrace();
    } finally {
      dataBaseTestConfig.closeConnection(con);
    }

    try {
      con = dataBaseTestConfig.getConnection();
      Statement psTicket = con.createStatement();
      psTicket.executeUpdate
          ("insert into ticket(id,parking_number,vehicle_reg_number,price,in_time,out_time)" +
              "value (2,1,'ABCDEF',0,DATE_SUB(CURRENT_TIME ,INTERVAL 2 HOUR),null)");
    } catch (SQLException | ClassNotFoundException throwables) {
      throwables.printStackTrace();
    } finally {
      dataBaseTestConfig.closeConnection(con);
    }

    when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

    ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

    parkingService.processExitingVehicle();

    try {
      con = dataBaseTestConfig.getConnection();
      PreparedStatement ps = con.prepareStatement
          ("select VEHICLE_REG_NUMBER, PRICE FROM ticket");
      ResultSet rs = ps.executeQuery();

      while (rs.next()) {
        vehicleNumberResponse = rs.getString("VEHICLE_REG_NUMBER");
        priceResponse = rs.getDouble("PRICE");

      }
    } catch (SQLException throwables) {
      throwables.printStackTrace();
    } finally {
      dataBaseTestConfig.closeConnection(con);
    }

    assertEquals("ABCDEF", vehicleNumberResponse);
    assertEquals(2.85, priceResponse);
  }

  @Test
  public void testBikeParkingFareDiscountedIT() throws Exception {
    dataBasePrepareService.clearDataBaseEntries();
    ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

    Connection con = null;

    try {
      con = dataBaseTestConfig.getConnection();
      Statement psTicket = con.createStatement();
      psTicket.executeUpdate
          ("insert into ticket(id,parking_number,vehicle_reg_number,price,in_time,out_time)" +
              "value (1,4,'MyBike',1,DATE_SUB(CURRENT_TIME ,INTERVAL 8 HOUR),DATE_SUB(CURRENT_TIME ,INTERVAL 7 HOUR))");
    } catch (SQLException | ClassNotFoundException throwables) {
      throwables.printStackTrace();
    } finally {
      dataBaseTestConfig.closeConnection(con);
    }

    try {
      con = dataBaseTestConfig.getConnection();
      Statement psTicket = con.createStatement();
      psTicket.executeUpdate
          ("insert into ticket(id,parking_number,vehicle_reg_number,price,in_time,out_time)" +
              "value (2,4,'MyBike',0,DATE_SUB(CURRENT_TIME ,INTERVAL 2 HOUR),null)");
    } catch (SQLException | ClassNotFoundException throwables) {
      throwables.printStackTrace();
    } finally {
      dataBaseTestConfig.closeConnection(con);
    }

    when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("MyBike");

    parkingService.processExitingVehicle();

    try {
      con = dataBaseTestConfig.getConnection();
      PreparedStatement ps = con.prepareStatement
          ("select VEHICLE_REG_NUMBER, PRICE FROM ticket");
      ResultSet rs = ps.executeQuery();

      while (rs.next()) {
        vehicleNumberResponse = rs.getString("VEHICLE_REG_NUMBER");
        priceResponse = rs.getDouble("PRICE");
      }
    } catch (SQLException throwables) {
      throwables.printStackTrace();
    } finally {
      dataBaseTestConfig.closeConnection(con);
    }

    assertEquals("MyBike", vehicleNumberResponse);
    assertEquals(1.9, priceResponse);
  }

}
