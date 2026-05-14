package com.mycompany.project;

public abstract class Vehicle implements Comparable<Vehicle> {

    private String vehicleId;
    private String brand;
    private boolean isAvailable = true;

    //Constructors
    Vehicle(String vehicleId) {
        this(vehicleId, "unknown");
    }

    Vehicle(String vehicleId, String brand) {
        this.vehicleId = vehicleId;
        this.brand = brand;
        this.isAvailable = isAvailable;

    }
    
    public String getId() { 
        return vehicleId; 
    }

    public String getBrand() { 
        return brand; 
    }
    
    public boolean isAvailable() {
        return isAvailable;
    }
    
    public abstract double calculateRentalRate(int days);
    
    //setters
    public void setAvailable(boolean available){
    this.isAvailable = available;
    }
    
    
    
    @Override
    public int compareTo(Vehicle other){
    return this.brand.compareTo(other.brand);
    }
    
    
    public void displayInfo(){
    System.out.println("ID: " + this.vehicleId + " | Brand: " + this.brand + " |Available: " +isAvailable);
    
    }
    
    
}
