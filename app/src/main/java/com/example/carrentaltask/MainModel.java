package com.example.carrentaltask;
import org.json.JSONArray;

public class MainModel {
    String name;
    JSONArray array;

    public MainModel(String name, JSONArray array) {
        this.name = name;
        this.array = array;
    }

    public String getName() {
        return name;
    }

    public JSONArray getArray() {
        return array;
    }
}