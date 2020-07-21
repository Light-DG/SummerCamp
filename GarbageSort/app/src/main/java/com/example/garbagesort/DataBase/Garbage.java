package com.example.garbagesort.DataBase;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Garbage {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private String kind;
    private String ps;
    private String confidence;
    private String model;

    private String time;

    private String city;
    private String path;

    public Garbage(String name, String kind, String ps, String confidence, String model, String time, String city, String path) {
        this.name = name;
        this.kind = kind;
        this.ps = ps;
        this.confidence = confidence;
        this.model = model;
        this.time = time;
        this.city = city;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    public String getConfidence() {
        return confidence;
    }

    public void setConfidence(String confidence) {
        this.confidence = confidence;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
