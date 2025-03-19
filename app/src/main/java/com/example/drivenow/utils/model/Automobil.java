package com.example.drivenow.utils.model;

import android.net.Uri;

public class Automobil {
    private int id;
    private String proizvodjac;
    private String model;
    private String tip;
    private int godiste;
    private int brojSedista;
    private int kilometraza;
    private double cena;
    private boolean dostupnost;
    private String slikaUrl;

    public Automobil(int id, String proizvodjac, String model, String tip, int godiste, int brojSedista, int kilometraza, double cena, boolean dostupnost, String slikaUrl) {
        this.id = id;
        this.proizvodjac = proizvodjac;
        this.model = model;
        this.tip = tip;
        this.godiste = godiste;
        this.brojSedista = brojSedista;
        this.kilometraza = kilometraza;
        this.cena = cena;
        this.dostupnost = dostupnost;
        this.slikaUrl = slikaUrl;
    }

    public int getId() {
        return id;
    }

    public String getProizvodjac() {
        return proizvodjac;
    }

    public String getModel() {
        return model;
    }

    public String getTip() {
        return tip;
    }

    public int getGodiste() {
        return godiste;
    }

    public int getBrojSedista() {
        return brojSedista;
    }

    public int getKilometraza() {
        return kilometraza;
    }

    public double getCena() {
        return cena;
    }

    public boolean isDostupnost() {
        return dostupnost;
    }

    public String getSlikaUrl() {
        return slikaUrl;
    }
}
