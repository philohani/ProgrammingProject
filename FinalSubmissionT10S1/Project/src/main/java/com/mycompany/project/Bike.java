package com.mycompany.project;

public class Bike extends Vehicle {

    private boolean hasHelmetIncluded;
    private static final double DAILY_RATE = 15.0;

    public Bike(String vehicleId, String brand, boolean hasHelmetIncluded) {
        super(vehicleId, brand);
        this.hasHelmetIncluded = hasHelmetIncluded;
    }

    @Override
    public double calculateRentalRate(int days) {
        return DAILY_RATE * days;
    }
}
