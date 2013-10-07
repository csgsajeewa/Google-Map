package com.example.googlemaps;
/**
 * Description of AsyncResponse
 * This interface is implemeted by activities to access outputs of asynchronous tasks
 *
 * @author chamath sajeewa
 * chamaths.10@cse.mrt.ac.lk
 */

public interface AsyncResponse {
    void processFinish(double[] geoCodes);
    void processFinish(Location locations);
  
}