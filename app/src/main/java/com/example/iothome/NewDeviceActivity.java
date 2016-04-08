package com.example.iothome;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.containers.Devices;
import com.example.db.DataBaseHandler;
import com.example.utils.Appconstants;

import java.util.ArrayList;
import java.util.List;

public class NewDeviceActivity extends AppCompatActivity {

    EditText deviceName;
    Spinner typeSpinner;
    EditText powerUsage;
    EditText macId;
    EditText ip;
    Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_device);
        setUI();
    }

    private void setUI() {
        deviceName = (EditText) findViewById(R.id.deviceNameText);
        typeSpinner = (Spinner) findViewById(R.id.deviceTypeSpinner);
        powerUsage = (EditText) findViewById(R.id.devicePowerText);
        macId = (EditText) findViewById(R.id.macIdText);
        ip = (EditText) findViewById(R.id.ipText);
        saveButton = (Button) findViewById(R.id.saveButton);

        List<String> categories = new ArrayList<String>();
        categories.add("Light");
        categories.add("Fan");
        categories.add("Door");
        categories.add("Refrigarator");
        categories.add("Washing machine");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        typeSpinner.setAdapter(dataAdapter);

        final DataBaseHandler handler = new DataBaseHandler(this);

        // Listner for save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (check()) {

                    Devices device = new Devices();
                    device.setDeviceName(deviceName.getText().toString());
                    device.setDeviceType(typeSpinner.getSelectedItem().toString());
                    device.setPowerConsumption(powerUsage.getText().toString());
                    device.setMacId(macId.getText().toString());
                    device.setIp(ip.getText().toString());
                    device.setStatus(Appconstants.OFF);
                    boolean b = handler.addDevice(device);
                    if (b) {
                        Toast.makeText(NewDeviceActivity.this, "Device added successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(NewDeviceActivity.this, "Adding device failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private boolean check() {
        if (deviceName.getText().toString().equals("")) {
           deviceName.setError("Enter device name!");
            return false;
        } else if (powerUsage.getText().toString().equals("")) {
            powerUsage.setError("Enter power!");
            return false;
        } else if (macId.getText().toString().equals("")) {
            macId.setError("Enter IP!");
            return false;
        } else if (ip.getText().toString().equals("")) {
            ip.setError("Enter ip!");
            return false;
        }
        return true;
    }
}
