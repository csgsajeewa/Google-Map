package com.example.googlemaps;

public interface AsyncResponse {
    void processFinish(double[] geoCodes);
    void processFinish(Location locations);
  
}