package com.example.iothome;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.containers.Devices;
import com.example.db.DataBaseHandler;
import com.example.utils.Appconstants;

import java.util.ArrayList;
import java.util.List;

public class EditDeviceActivity extends AppCompatActivity implements View.OnClickListener {

    Devices device;

    EditText devName;
    Spinner devType;
    EditText devPower;
    EditText devMAC;
    EditText devIP;

    Button saveButton;
    Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_device);

        device = (Devices) getIntent().getExtras().getSerializable(Appconstants.DEVICE_INFO);
        setUI();

    }

    private void setUI() {
        devName = (EditText) findViewById(R.id.deviceNameText2);
        devType = (Spinner) findViewById(R.id.deviceTypeSpinner2);
        devPower = (EditText) findViewById(R.id.devicePowerText2);
        devMAC = (EditText) findViewById(R.id.macIdText2);
        devIP = (EditText) findViewById(R.id.ipText2);

        saveButton = (Button) findViewById(R.id.saveButton2);
        cancelButton = (Button) findViewById(R.id.cancelButton);

        List<String> categories = new ArrayList<String>();
        categories.add("Light");
        categories.add("Fan");
        categories.add("Door");
        categories.add("Refrigarator");
        categories.add("Washing machine");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        devType.setAdapter(dataAdapter);


        devName.setText(device.getDeviceName());
        int pos = categories.indexOf(device.getDeviceType());
        devType.setSelection(pos);
        devPower.setText(device.getPowerConsumption());
        devMAC.setText(device.getMacId());
        devIP.setText(device.getIp());

        saveButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        devType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                ((TextView)adapterView.getChildAt(0)).setTextColor(getResources().getColor(R.color.White));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onClick(View view) {

        DataBaseHandler handler = new DataBaseHandler(this);

        if (view.getId() == saveButton.getId()) {


            Devices dev = new Devices();
            dev.setDeviceName(devName.getText().toString());
            dev.setDeviceType(devType.getSelectedItem().toString());
            dev.setPowerConsumption(devPower.getText().toString());
            dev.setMacId(devMAC.getText().toString());
            dev.setIp(devIP.getText().toString());
            dev.setId(device.getId());

            boolean b = handler.updateDevices(dev);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            if (b) {
                builder.setMessage("Device information edited successfully...! ");
            } else {
                builder.setMessage("Something wrong.....! Editing failed");
            }

            builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();


        } else if (view.getId() == cancelButton.getId()) {
            this.finish();
        }
    }
}
