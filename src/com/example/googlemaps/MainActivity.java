package com.example.googlemaps;
/**
 * Description of XML Parser
 * 
 *
 * @author chamath sajeewa
 * chamaths.10@cse.mrt.ac.lk
 */


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity{
        
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main_page);
        
       
    }
    
    public void showMap(View view){
		
		Intent intent = new Intent(this, MapView.class);
		startActivity(intent);
		
	}
    
   
     public void showMapDB(View view){
 		
 		Intent intent = new Intent(this, DBActivity.class);
 		startActivity(intent);
 		
 	}
     
}
