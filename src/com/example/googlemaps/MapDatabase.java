package com.example.googlemaps;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MapDatabase {
  
  public static final String KEY_ID = "_id";
  
  //The name and column index of each column in your database.
  //These should be descriptive.
  public static final String PLACE =   "PLACE";
  public static final String LATITUDE ="LATITUDE";
  public static final String LONGITUDE ="LONGITUDE";
  //TODO: Create public field for each column in your table.
  /***/


  // Database open/upgrade helper
  private MapDBOpenHelper mapDBOpenHelper;

  public MapDatabase(Context context) {
    mapDBOpenHelper = new MapDBOpenHelper(context, MapDBOpenHelper.DATABASE_NAME, null, 
                                              MapDBOpenHelper.DATABASE_VERSION);
  }
  
  // Called when you no longer need access to the database.
  public void closeDatabase() {
    mapDBOpenHelper.close();
  }

  private Cursor getPlaceLongLat(String place1) {
    
    // Specify the result column projection. Return the minimum set
    // of columns required to satisfy your requirements.
    String[] result_columns = new String[] { 
      KEY_ID, PLACE,LATITUDE,LONGITUDE }; 
    
    // Specify the where clause that will limit our results.
    String where = PLACE + "=" +"'"+ place1 + "'";
    
    // Replace these with valid SQL statements as necessary.
    String whereArgs[] = null;
    String groupBy = null;
    String having = null;
    String order = null;
    
    SQLiteDatabase db = mapDBOpenHelper.getWritableDatabase();
    Cursor cursor = db.query(MapDBOpenHelper.DATABASE_TABLE,  result_columns, where,whereArgs, groupBy, having, order);
    //
    return cursor;
  }
  
  public double[] getPlaceInfo(String place) {
    Cursor cursor = getPlaceLongLat(place);// get cursor
    String place1;
    double latitude=0;
    double longitude=0;
    double geoCodes[]=new double[2];
    /**
     *  Extracting values from a Cursor
     */
    

    // Find the index to the column(s) being used.
    int PLACE_INDEX =cursor.getColumnIndexOrThrow(PLACE);
    int LATITUDE_INDEX =cursor.getColumnIndexOrThrow(LATITUDE);
    int LONGITUDE_INDEX =cursor.getColumnIndexOrThrow(LONGITUDE);

    // Iterate over the cursors rows. 
    // The Cursor is initialized at before first, so we can 
    // check only if there is a "next" row available. If the
    // result Cursor is empty this will return false.
    while (cursor.moveToNext()) {
    	 place1=cursor.getString(PLACE_INDEX);
         latitude= cursor.getDouble(LATITUDE_INDEX);
         longitude=cursor.getDouble(LONGITUDE_INDEX);
         geoCodes[0]=latitude;
         geoCodes[1]=longitude;
      
      
    }

    
    
    return geoCodes;
  }
  
  public void addNewPlace(String place, double latitude, double longitude) {
    /**
     * Listing 8-5: Inserting new rows into a database
     */
    // Create a new row of values to insert.
    ContentValues newValues = new ContentValues();
  
    // Assign values for each row.
    newValues.put(PLACE, place);
    newValues.put(LATITUDE, latitude);
    newValues.put(LONGITUDE, longitude);
    // [ ... Repeat for each column / value pair ... ]
  
    // Insert the row into your table
    SQLiteDatabase db = mapDBOpenHelper.getWritableDatabase();
    db.insert(MapDBOpenHelper.DATABASE_TABLE, null, newValues); 
  }
  
  public void updatePlaceInfo(String place, double latitude, double longitude) {
    /**
     * Listing 8-6: Updating a database row
     */
    // Create the updated row Content Values.
    ContentValues updatedValues = new ContentValues();
  
    // Assign values for each row.
    updatedValues.put(LATITUDE, latitude);
    updatedValues.put(LONGITUDE, longitude);
    
    // [ ... Repeat for each column to update ... ]
  
    // Specify a where clause the defines which rows should be
    // updated. Specify where arguments as necessary.
    String where = PLACE + "=" + place;
    String whereArgs[] = null;
  
    // Update the row with the specified index with the new values.
    SQLiteDatabase db = mapDBOpenHelper.getWritableDatabase();
    db.update(MapDBOpenHelper.DATABASE_TABLE, updatedValues, 
              where, whereArgs);
  }
  
  
  /**
   * Listing 8-2: Implementing an SQLite Open Helper
   */
  private static class MapDBOpenHelper extends SQLiteOpenHelper {
    
    private static final String DATABASE_NAME = "MapDatabase.db";
    private static final String DATABASE_TABLE = "PLACES";
    private static final int DATABASE_VERSION = 1;
    
    // SQL Statement to create a new database.
    private static final String DATABASE_CREATE = "create table " +
      DATABASE_TABLE + " (" + KEY_ID +
      " integer primary key autoincrement, " +
     PLACE + " text not null, " +
     LATITUDE + " double, " +
     LONGITUDE + " double);";

    public MapDBOpenHelper(Context context, String name,
                      CursorFactory factory, int version) {
      super(context, name, factory, version);
    }

    // Called when no database exists in disk and the helper class needs
    // to create a new one.
    @Override
    public void onCreate(SQLiteDatabase db) {
      db.execSQL(DATABASE_CREATE);
    }

    // Called when there is a database version mismatch meaning that
    // the version of the database on disk needs to be upgraded to
    // the current version.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, 
                          int newVersion) {
      // Log the version upgrade.
      Log.w("TaskDBAdapter", "Upgrading from version " +
        oldVersion + " to " +
        newVersion + ", which will destroy all old data");

      // Upgrade the existing database to conform to the new 
      // version. Multiple previous versions can be handled by 
      // comparing oldVersion and newVersion values.

      // The simplest case is to drop the old table and create a new one.
      db.execSQL("DROP TABLE IF IT EXISTS " + DATABASE_TABLE);
      // Create a new one.
      onCreate(db);
    }
  }
}