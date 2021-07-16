package com.parkit.parkingsystem;

import com.parkit.parkingsystem.service.InteractiveShell;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 The main application class.
 */
public class App {
    private static final Logger logger = LogManager.getLogger("App");

    /**
     Launches the application.

     @param args the args
     */
    public static void main(String args[]) {
        logger.info("Initializing Parking System");
        InteractiveShell.loadInterface();
    }
}
