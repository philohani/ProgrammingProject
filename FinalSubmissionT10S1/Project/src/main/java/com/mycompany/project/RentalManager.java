
package com.mycompany.project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RentalManager {

    private List<Vehicle> inventory;

    //Constructor
    public RentalManager() {
        inventory = new ArrayList<>();
    }

    public void addVehicle(Vehicle v) {
        inventory.add(v);
    }

    // Generic Sort
    public void sortInventory() {
        Collections.sort(inventory);
    }

    public void rentVehicle(Vehicle v, int days) throws VehicleUnavailableException {
        if (!v.isAvailable()) {
            throw new VehicleUnavailableException("Sorry, " + v.getClass().getSimpleName() + " is already rented out.");
        }
        System.out.println("Vehicle rented Successfully. Cost: $" + v.calculateRentalRate(days));
        v.setAvailable(false);
    }

    public List<Vehicle> getInventory() {
        return inventory;
    }

    public void displayAllVehicles() {
        for (Vehicle v : inventory) {
            v.displayInfo();
        }
    }
}
