package com.example.garbagesort.DataBase;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class LastCity {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String lastCity;

    public LastCity(String lastCity) {
        this.lastCity = lastCity;
    }

    public String getLastCity() {
        return lastCity;
    }

    public void setLastCity(String lastCity) {
        this.lastCity = lastCity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
