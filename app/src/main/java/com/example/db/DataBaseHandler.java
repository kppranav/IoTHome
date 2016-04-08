package com.example.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.containers.Devices;
import com.example.utils.Appconstants;

import java.util.ArrayList;

/**
 * Created by uvionics on 11/2/16.
 */
public class DataBaseHandler extends SQLiteOpenHelper {


    private static final String DEVICE_NAME = "DEVICE_NAME";
    private static final String DEVICE_TYPE = "DEVICE_TYPE";
    private static final String POWER_CONSUMPTION = "POWER_CONSUMPTION";
    private static final String MAC_ID = "MAC_ID";
    private static final String IP = "IP";
    private static final String STATUS = "STATUS";

    public DataBaseHandler(Context context) {
        super(context, Appconstants.DB_NAME, null, Appconstants.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_DEVICE_TABLE = "CREATE TABLE " + Appconstants.DEVICES_TABLE + "(" +
                Appconstants.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + DEVICE_NAME + " TEXT," + DEVICE_TYPE +
                " TEXT," + POWER_CONSUMPTION + " TEXT," + MAC_ID + " TEXT," + IP +
                " TEXT," + STATUS + " TEXT)";
        db.execSQL(CREATE_DEVICE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }

    // Method to add device details
    public boolean addDevice(Devices device) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DEVICE_NAME, device.getDeviceName());
        values.put(DEVICE_TYPE, device.getDeviceType());
        values.put(POWER_CONSUMPTION, device.getPowerConsumption());
        values.put(MAC_ID, device.getMacId());
        values.put(IP, device.getIp());
        values.put(STATUS, device.getStatus());
        long insert = db.insert(Appconstants.DEVICES_TABLE, null, values);
        if (insert > 0) {
            return true;
        }
        return false;
    }

    // To get the list of devices
    public ArrayList<Devices> getAllDevices() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + Appconstants.DEVICES_TABLE;
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<Devices> list = null;
        if (cursor != null) {
            list = new ArrayList<Devices>();
            while (cursor.moveToNext()) {
                Devices device = new Devices();
                device.setId(cursor.getInt(cursor.getColumnIndex(Appconstants.ID)));
                device.setDeviceName(cursor.getString(cursor.getColumnIndex(DEVICE_NAME)));
                device.setDeviceType(cursor.getString(cursor.getColumnIndex(DEVICE_TYPE)));
                device.setPowerConsumption(cursor.getString(cursor.getColumnIndex(POWER_CONSUMPTION)));
                device.setMacId(cursor.getString(cursor.getColumnIndex(MAC_ID)));
                device.setIp(cursor.getString(cursor.getColumnIndex(IP)));
                device.setStatus(cursor.getString(cursor.getColumnIndex(STATUS)));
                list.add(device);
            }
            cursor.close();
            db.close();
        }
        return list;
    }

    public boolean deleteDevice(String id) {
        SQLiteDatabase database = this.getWritableDatabase();
        int delete = database.delete(Appconstants.DEVICES_TABLE, Appconstants.ID + "=?", new String[]{id});
        if (delete > 0) {
            return true;
        }
        return false;
    }

    public boolean updateStatus(String status, String id) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(STATUS, status);
        int update = db.update(Appconstants.DEVICES_TABLE, cv, Appconstants.ID + "=" + id, null);
        if (update > 0) {
            return true;
        }

        return false;
    }

    public String getStatus(String id) {
        String status = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT STATUS FROM " + Appconstants.DEVICES_TABLE + " WHERE " + Appconstants.ID + "= " + id;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                status = cursor.getString(cursor.getColumnIndex(STATUS));
            }
        }
        return status;
    }
    public boolean updateDevices(Devices devices) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DEVICE_NAME, devices.getDeviceName());
        values.put(DEVICE_TYPE, devices.getDeviceType());
        values.put(POWER_CONSUMPTION, devices.getPowerConsumption());
        values.put(MAC_ID, devices.getMacId());
        values.put(IP, devices.getIp());
        int update = db.update(Appconstants.DEVICES_TABLE, values,
                Appconstants.ID + "=?", new String[] { devices.getId() + "" });
        if (update > 0) {

            return true;
        }

        return false;
    }
}
