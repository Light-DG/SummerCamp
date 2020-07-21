package com.example.garbagesort.history;

public class BaseInfo {
    private String name = null;
    private String kind= null;
    private String model = null;
    private String time = null;

    public String getName() {
        return name;
    }

    public String getKind() {
        return kind;
    }

    public String getModel() {
        return model;
    }

    public String getTime() {
        return time;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
