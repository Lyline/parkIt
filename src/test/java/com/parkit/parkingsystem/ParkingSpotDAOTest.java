package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class ParkingSpotDAOTest {
  private static DataBasePrepareService dataBasePrepareService = new DataBasePrepareService();
  private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();

  private ParkingSpotDAO parkingSpotDAO;
  private ParkingType parkingType;
  private ParkingSpot parkingSpot;

  @BeforeEach
  public void setupPerTest() {
    parkingSpotDAO = new ParkingSpotDAO();
    parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
    dataBasePrepareService.clearDataBaseEntries();
  }

  @Test
  public void getNextAvailableSlotTest() {
    parkingType = ParkingType.CAR;

    int result = parkingSpotDAO.getNextAvailableSlot(parkingType);

    assertEquals(1, result);
  }

  @Test
  public void updateParkingValidateTest() {
    parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

    boolean result = parkingSpotDAO.updateParking(parkingSpot);

    assertTrue(result);
  }
}
