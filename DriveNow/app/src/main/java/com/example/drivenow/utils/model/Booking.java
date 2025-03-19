package com.example.drivenow.utils.model;

public class Booking {
    private int id;
    private String name;  //tekst = proizvodjac model godiste
    private String pickupDate;
    private String returnDate;
    private double cena;
    private int autoId;

    public Booking(String name, int id, String pickupDate, String returnDate, double cena, int autoId) {
        this.id = id;
        this.name = name;
        this.pickupDate = pickupDate;
        this.returnDate = returnDate;
        this.cena = cena;
        this.autoId = autoId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPickupDate() {
        return pickupDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public double getCena() { return cena; }

    public int getAutoId() { return autoId; }
}
