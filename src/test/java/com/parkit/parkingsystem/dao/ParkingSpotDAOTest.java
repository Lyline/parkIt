package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
public class ParkingSpotDAOTest {
  private static DataBasePrepareService dataBasePrepareService = new DataBasePrepareService();
  private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();

  private ParkingSpotDAO parkingSpotDAO;
  private ParkingType parkingType;
  private ParkingSpot parkingSpot;
  private Logger logger;

  @BeforeEach
  public void setupPerTest() {
    parkingSpotDAO = new ParkingSpotDAO();
    parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
    dataBasePrepareService.clearDataBaseEntries();
  }

  @Test
  public void getNextAvailableSlotTest() {
    int result = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);

    assertEquals(1, result);
  }

  @Test
  public void getNextNotAvailableSlotTest() {
    parkingSpotDAO.getNextAvailableSlot(parkingType);

    assertThrows(Exception.class, () -> logger.error("Error fetching next available slot"));
  }

  @Test
  public void updateParkingValidateTest() {
    parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

    boolean result = parkingSpotDAO.updateParking(parkingSpot);

    assertTrue(result);
  }

  @Test
  public void updateParkingNotValidateTest() {
    parkingSpotDAO.updateParking(parkingSpot);

    assertThrows(Exception.class, () -> logger.error("Error updating parking info"));
  }
}
