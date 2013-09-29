package com.example.googlemaps;
/**
 * Description of LocationInfo
 *
 * @author chamath sajeewa
 * chamaths.10@cse.mrt.ac.lk
 */

public class Location {
	
	private String name;
	private double latitude;
	private double longitude;
	
	public Location() {
		
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public String getName() {
		return name;
	}
	
	public double getLatitude() {
		return latitude;
	}
	
	public double getLongitude() {
		return longitude;
	}

}
