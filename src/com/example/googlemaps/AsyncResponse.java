package com.example.googlemaps;
/**
 * Description of AsyncResponse
 *
 * @author chamath sajeewa
 * chamaths.10@cse.mrt.ac.lk
 */

public interface AsyncResponse {
    void processFinish(double[] geoCodes);
    void processFinish(Location locations);
  
}