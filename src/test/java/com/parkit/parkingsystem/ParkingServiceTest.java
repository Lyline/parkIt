package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

  private static ParkingService parkingService;
  private static Logger logger;

  @Mock
  private static InputReaderUtil inputReaderUtil;
  @Mock
  private static ParkingSpotDAO parkingSpotDAO;
  @Mock
  private static TicketDAO ticketDAO;
  @Mock
  ParkingSpot parkingSpot;


  @BeforeEach
  private void setUpPerTest() {
    parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
  }

  @Test
  public void getNextCarParkingNumberIfAvailableTest() {
    when(inputReaderUtil.readSelection()).thenReturn(1);
    when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);

    parkingSpot = parkingService.getNextParkingNumberIfAvailable();

    assertEquals(new ParkingSpot(1, ParkingType.CAR, false), parkingSpot);
  }

  @Test
  public void getNextBikeParkingNumberIfAvailableTest() {
    when(inputReaderUtil.readSelection()).thenReturn(2);
    when(parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE)).thenReturn(4);

    parkingSpot = parkingService.getNextParkingNumberIfAvailable();

    assertEquals(new ParkingSpot(4, ParkingType.BIKE, false), parkingSpot);
  }

    /*@Test
    public void getNextParkingNumberIfNotAvailableTest(){
      when(inputReaderUtil.readSelection()).thenReturn(2);
      when(parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE)).thenReturn(-1);

     //parkingService.getNextParkingNumberIfAvailable();
      Exception exception = null;
      assertThrows(Exception.class,()-> parkingService.getNextParkingNumberIfAvailable());
      assertEquals("Error fetching parking number from DB. Parking slots might be full",exception.getMessage());
    }
*/

  @Test
  public void processValidIncomingVehicleTest() throws Exception {
    Ticket ticket = new Ticket();
    when(inputReaderUtil.readSelection()).thenReturn(1);
    when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);
    when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("MyCar");

    parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);


    parkingService.processIncomingVehicle();

    //coder assertEquals()
    verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
    assertEquals(0, ticket.getPrice());
    //assertEquals("MyCar",ticket.getVehicleRegNumber());

  }

  @Test
  public void processValidExitingVehicleTest() {
    Ticket ticket = new Ticket();
    try {
      when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

      ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

      ticket.setInTime(LocalDateTime.now().minusHours(1));
      ticket.setParkingSpot(parkingSpot);
      ticket.setVehicleRegNumber("ABCDEF");
      when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
      when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);

      when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

      parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
    } catch (Exception e) {
      e.printStackTrace();
      throw  new RuntimeException("Failed to set up test mock objects");
    }

    parkingService.processExitingVehicle();

    verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
    assertEquals(1.5, ticket.getPrice());
  }

  @Test
  public void processInvalidExitingVehicleTest() {
    // no vehicle number typed
    ticketDAO.getTicket(null);
    ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
    parkingService.processExitingVehicle();
    assertThrows(Exception.class, () -> logger.error("Unable to process exiting vehicle"));
  }
}
