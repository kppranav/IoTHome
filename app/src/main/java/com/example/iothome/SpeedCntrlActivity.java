package com.example.iothome;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.containers.Devices;
import com.example.db.DataBaseHandler;
import com.example.utils.Appconstants;
import com.example.utils.HttpConnection;
import com.example.utils.RotaryKnobView;

import java.io.Serializable;

public class SpeedCntrlActivity extends AppCompatActivity implements View.OnClickListener {
    FloatingActionButton onFab;
    FloatingActionButton fab1;
    FloatingActionButton fab2;
    FloatingActionButton fab3;
    FloatingActionButton fab4;
    DataBaseHandler handler;
    Devices devices;
    String status;
    RotaryKnobView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed_cntrl);
        setUI();
        devices = (Devices) getIntent().getExtras().getSerializable(Appconstants.DEVICE_INFO);
        status = devices.getStatus();


        handler = new DataBaseHandler(this);

    }

    private void setUI() {
        onFab = (FloatingActionButton) findViewById(R.id.fabOn);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) findViewById(R.id.fab3);
        fab4 = (FloatingActionButton) findViewById(R.id.fab4);

        onFab.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);
        fab3.setOnClickListener(this);
        fab4.setOnClickListener(this);

        view = (RotaryKnobView) findViewById(R.id.knob);
        view.setKnobListener(new RotaryKnobView.RotaryKnobListener() {
            @Override
            public void onKnobChanged(int arg) {

                if (arg > 0) {

                } else {

                }

            }
        });




    }

    @Override
    public void onClick(View view) {

            //String status = handler.getStatus(devices.getId() + "");

            String val = "";
            StringBuilder builder = new StringBuilder();
            builder.append("http://" + devices.getIp() + "/" + devices.getDeviceName() + "/");
            //builder.append("http://192.168.42.29/device5/");
            if (view.getId() == onFab.getId()) {
                if (status.equals(Appconstants.ON)) {
                    val = "0";
                    status = "0";
                } else {
                    val = "1";
                    status = "1";
                }
            } else if (view.getId() == fab1.getId()) {
                val = "1";
            } else if (view.getId() == fab2.getId()) {
                 val = "2";
            } else if (view.getId() == fab3.getId()) {
                 val = "3";
            } else if (view.getId() == fab4.getId()) {
                 val = "4";
            }
            builder.append(val);
        //Log.d("TAG", builder.toString());

        new SendTask().execute(builder.toString());

    }

    private class SendTask extends AsyncTask<String, Void, String> {
        String resp;
        @Override
        protected String doInBackground(String... params) {
            resp = new HttpConnection().sendVal(params[0]);
            Log.d("TAG", resp);
            return resp;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("TAG", s);
        }
    }
}
