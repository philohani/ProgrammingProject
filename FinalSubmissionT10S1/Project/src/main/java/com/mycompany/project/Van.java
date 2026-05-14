package com.mycompany.project;

public class Van extends Vehicle {
    
     private double CapacityInKg;
    private static final double DAILY_RATE = 80.0;

    public Van(String vehicleId, String brand, double CapacityInKg) {
        super(vehicleId, brand);
        this.CapacityInKg = CapacityInKg;
    }

    @Override
    public double calculateRentalRate(int days) {
        return DAILY_RATE * days;
    }
}
