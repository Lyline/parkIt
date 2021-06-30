package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.joda.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static junit.framework.Assert.*;

@ExtendWith(MockitoExtension.class)
public class TicketDAOTest {
  private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
  private static DataBasePrepareService dataBasePrepareService = new DataBasePrepareService();
  private TicketDAO ticketDAO;
  private ParkingSpot parkingSpot;

  @Mock
  private Ticket ticket;

  @BeforeEach
  private void setUpPerTest() {
    ticketDAO = new TicketDAO();
    ticketDAO.dataBaseConfig = dataBaseTestConfig;
    ticket = new Ticket();
    parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
  }

  @Test
  public void saveIncomingTicketTest() {
    dataBasePrepareService.clearDataBaseEntries();

    LocalDateTime inTime = new LocalDateTime("2020-06-01T00:00:00");

    ticket.setVehicleRegNumber("MyCar");
    ticket.setParkingSpot(parkingSpot);
    ticket.setInTime(inTime);
    ticket.setOutTime(null);

    boolean result = ticketDAO.saveTicket(ticket);
    assertFalse(result);
  }

  @Test
  public void saveOutTicketTest() {
    dataBasePrepareService.clearDataBaseEntries();

    LocalDateTime inTime = new LocalDateTime("2020-06-01T00:00:00");
    LocalDateTime outTime = new LocalDateTime("2020-06-01T01:00:00");

    ticket.setVehicleRegNumber("MyCar");
    ticket.setParkingSpot(parkingSpot);
    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);

    boolean result = ticketDAO.saveTicket(ticket);
    assertFalse(result);
  }

  @Test
  public void getTicketAvailableTest() {
    dataBasePrepareService.clearDataBaseEntries();

    LocalDateTime inTime = new LocalDateTime().minusHours(1);

    ticket.setVehicleRegNumber("MyGreatCar");
    ticket.setParkingSpot(parkingSpot);
    ticket.setInTime(inTime);
    ticket.setPrice(1.5);

    ticketDAO.saveTicket(ticket);

    Ticket result = ticketDAO.getTicket("MyGreatCar");

    assertEquals(1.5, result.getPrice());
    assertEquals("MyGreatCar", result.getVehicleRegNumber());
  }

  @Test
  public void updateTicketAvailableTest() {
    dataBasePrepareService.clearDataBaseEntries();

    LocalDateTime inTime = new LocalDateTime().minusHours(1);
    LocalDateTime outTime = new LocalDateTime();

    ticket.setVehicleRegNumber("MyGreatCar");
    ticket.setParkingSpot(parkingSpot);
    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setPrice(1.5);

    ticketDAO.saveTicket(ticket);

    boolean result = ticketDAO.updateTicket(ticket);

    assertTrue(result);
  }

  @Test
  public void searchVehicleSubscribeTrueTest() {
    dataBasePrepareService.clearDataBaseEntries();

    LocalDateTime inTime = new LocalDateTime().minusHours(2);
    LocalDateTime outTime = new LocalDateTime().minusHours(1);

    ticket.setVehicleRegNumber("MyGreatCar");
    ticket.setParkingSpot(parkingSpot);
    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setPrice(1.5);

    ticketDAO.saveTicket(ticket);

    boolean result = ticketDAO.searchVehicleSubscribe("MyGreatCar");
    assertTrue(result);
  }
}
