package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.apache.logging.log4j.Logger;
import org.joda.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

  @Mock
  private DataBaseTestConfig dataBaseTestConfig;
  @Mock
  private static InputReaderUtil inputReaderUtil;
  @Mock
  private static ParkingSpotDAO parkingSpotDAO;
  @Mock
  private static TicketDAO ticketDAO;
  @Mock
  private ParkingSpot parkingSpot;
  @Mock
  private Ticket ticket;

  private ParkingService parkingService;
  private Logger logger;


  @BeforeEach
  private void setUpPerTest() throws SQLException, ClassNotFoundException {
    MockitoAnnotations.initMocks(this);
    Connection con = dataBaseTestConfig.getConnection();
    parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
  }

  @Test
  public void getNextCarParkingNumberIfAvailableTest() {
    when(inputReaderUtil.readSelection()).thenReturn(1);
    when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(2);

    parkingSpot = parkingService.getNextParkingNumberIfAvailable();

    verify(inputReaderUtil, times(1)).readSelection();
    verify(parkingSpotDAO, times(1)).getNextAvailableSlot(ParkingType.CAR);
    assertEquals(2, parkingSpot.getId());
  }

  @Test
  public void getNextBikeParkingNumberIfAvailableTest() {
    when(inputReaderUtil.readSelection()).thenReturn(2);
    when(parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE)).thenReturn(4);

    parkingSpot = parkingService.getNextParkingNumberIfAvailable();

    verify(inputReaderUtil, times(1)).readSelection();
    verify(parkingSpotDAO, times(1)).getNextAvailableSlot(ParkingType.BIKE);
    assertEquals(new ParkingSpot(4, ParkingType.BIKE, false), parkingSpot);
  }

  @Test
  public void getNextParkingNumberIfNotAvailableTest() {
    when(inputReaderUtil.readSelection()).thenReturn(1);
    when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(-1);

    parkingService.getNextParkingNumberIfAvailable();

    assertThrows(Exception.class, () -> logger.getName());
  }


  @Test
  public void processValidIncomingVehicleTest() throws Exception {
    when(inputReaderUtil.readSelection()).thenReturn(1);
    when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("MyCar");
    when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);

    parkingService.processIncomingVehicle();

    verify(inputReaderUtil, times(1)).readSelection();
    verify(inputReaderUtil, times(1)).readVehicleRegistrationNumber();
    verify(parkingSpotDAO, times(1)).updateParking(any(ParkingSpot.class));
  }

  @Test
  public void processInvalidIncomingVehicleTest() {
    when(inputReaderUtil.readSelection()).thenReturn(1);
    when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);
    when(ticketDAO.saveTicket(ticket)).thenReturn(false);

    parkingService.processIncomingVehicle();

    assertThrows(Exception.class, () -> logger.error("Unable to process incoming vehicle"));
  }

  @Test
  public void processValidExitingVehicleTest() throws Exception {
    ticket = new Ticket();
    ticket.setVehicleRegNumber("ABCDEF");
    ticket.setParkingSpot(new ParkingSpot(1, ParkingType.CAR, false));
    ticket.setInTime(LocalDateTime.now().minusHours(1));
    ticket.setOutTime(LocalDateTime.now());

    lenient().when(inputReaderUtil.readSelection()).thenReturn(2);
    lenient().when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
    lenient().when(ticketDAO.updateTicket(ticket)).thenReturn(true);
    lenient().when(ticketDAO.getTicket(anyString())).thenReturn(ticket);

    parkingService.processExitingVehicle();

    verify(parkingSpotDAO, times(1)).updateParking(any(ParkingSpot.class));

    assertEquals(1.5, ticket.getPrice());
  }

  @Test
  public void processInvalidExitingVehicleTest() throws Exception {
    when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("test");

    parkingService.processExitingVehicle();

    assertThrows(Exception.class, () -> logger.error("Unable to process exiting vehicle"));
  }
}
