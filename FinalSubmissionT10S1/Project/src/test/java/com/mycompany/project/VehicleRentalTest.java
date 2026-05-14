package com.mycompany.project;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class VehicleRentalTest {

    @Test
    public void testCalculateRentalRate() {
        // Arrange: Create a test car
        Car testCar = new Car("C99", "Test Brand", 4);
        int rentDays = 3;

        // Act: Calculate the rate (50.0 * 3 days)
        double actualRate = testCar.calculateRentalRate(rentDays);

        // Assert: Verify the logic is correct
        assertEquals(150.0, actualRate, "The rental rate for a Car over 3 days should be exactly $150.0");
    }

    @Test
    public void testVehicleUnavailableException() {
        // Arrange: Set up the manager and add a car
        RentalManager manager = new RentalManager();
        Car testCar = new Car("C99", "Test Brand", 4);
       // Car testCar2 = new Car("C100", "Test Brand", 4);
        manager.addVehicle(testCar);

        // Act & Assert Part 1: The first rental should succeed without throwing an error
        assertDoesNotThrow(() -> {
            manager.rentVehicle(testCar, 2);
        }, "The first rental attempt should succeed.");

        // Act & Assert Part 2: The second rental attempt on the same car MUST throw our custom exception
        assertThrows(VehicleUnavailableException.class, () -> {
            manager.rentVehicle(testCar, 1);
        }, "Renting an already rented vehicle should throw a VehicleUnavailableException.");
    }
}