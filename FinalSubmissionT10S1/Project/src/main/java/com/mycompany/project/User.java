package com.mycompany.project;

public class User {
    private String name;
    private String licenseNumber;
    
    public User(String name , String licenseNumber){
    this.name = name;
    this.licenseNumber = licenseNumber;
    }
    
    public String getName(){
    return name;
    }
}
