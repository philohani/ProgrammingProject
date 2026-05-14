package com.mycompany.project;

public class Car extends Vehicle {
    private int numberOfDoors;
    private static final double DAILY_RATE = 50.0;
    
    public Car(String vehicleId , String brand , int numberofDoors){
    super(vehicleId,brand);
    this.numberOfDoors = numberofDoors;
    }
    
    @Override
    public double calculateRentalRate(int days){
    return DAILY_RATE* days;
    }
}
