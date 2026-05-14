
package com.mycompany.project;

public class Booking implements Payable {

    private User user;
    private Vehicle vehicle;
    private int days;

    public Booking(User user, Vehicle vehicle, int days) {
        this.user = user;
        this.vehicle = vehicle;
        this.days = days;
    }

    @Override
    public void processPayment(double amount) {
        System.out.println("Processing payment of " + amount + "for user: " + user.getName());
        System.out.println("Payment Successful!");
    }
}
