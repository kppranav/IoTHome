package com.example.iothome;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.adapters.DeviceOpAdapter;
import com.example.containers.Devices;
import com.example.db.DataBaseHandler;
import com.example.utils.Appconstants;
import com.example.utils.HttpConnection;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeviceListActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener, View.OnClickListener, View.OnLongClickListener {

    ListView deviceListView;
    DataBaseHandler handler;
    DeviceOpAdapter adapter;
    WifiManager manager;
    WifiConfiguration config;
    Button emergencyButton;
    Button powerButton;
    WifiInfo info;

    String macid = "";
    String routerid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);
        setUI();
        deviceListView.setOnItemLongClickListener(this);
        deviceListView.setOnItemClickListener(this);
        setupWifi();

        macid = getMacAddr();
        Log.d("TAG", "mac id : " + macid);

        /*SharedPreferences preferences = getSharedPreferences(Appconstants.MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("macid", macid);
        editor.commit();*/

    }

    private void setupWifi() {

        manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        //Wifi configuration
        config = new WifiConfiguration();
        config.SSID = Appconstants.SSID;
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        config.status = WifiConfiguration.Status.ENABLED;

        //Connect and enable connection
        int netid = manager.addNetwork(config);
        if (netid < 0) {
            Toast.makeText(getApplicationContext(), "Could not able to connnect to the network" +
                    " iot Gateway", Toast.LENGTH_SHORT).show();
        } else {

            boolean b = manager.enableNetwork(netid, true);
            manager.setWifiEnabled(true);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        int rssi = info.getRssi();
        Log.d("TAG", rssi + " dB");
        Log.d("TAG", info.toString());
        routerid = info.getBSSID();

    }


    // Method to get mac Address
    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(Integer.toHexString(b & 0xFF) + ":");
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "02:00:00:00:00:00";
    }

    private void setUI() {
        deviceListView = (ListView) findViewById(R.id.deviceListView);
        emergencyButton = (Button) findViewById(R.id.emergencyButton);
        powerButton = (Button) findViewById(R.id.poweron);

        handler = new DataBaseHandler(this);
        ArrayList<Devices> devices = handler.getAllDevices();
        adapter = new DeviceOpAdapter(this, devices);
        deviceListView.setAdapter(adapter);
        deviceListView.setDividerHeight(5);

        emergencyButton.setOnClickListener(this);
        powerButton.setOnClickListener(this);


        emergencyButton.setOnLongClickListener(this);
        powerButton.setOnLongClickListener(this);


    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        Toast.makeText(DeviceListActivity.this, "Long click", Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {

      /*  Devices devices = (Devices) adapterView.getItemAtPosition(pos);
        Intent intent = new Intent(DeviceListActivity.this, ControlActivity.class);
        intent.putExtra(Appconstants.DEVICE_INFO, devices);
        startActivity(intent);*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.device_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        DataBaseHandler handler = new DataBaseHandler(DeviceListActivity.this);
        ArrayList<Devices> devices = handler.getAllDevices();
        if (item.getItemId() == R.id.sync) {

            JSONObject object = new JSONObject();
            try {
                object.put("macid", macid);
                object.put("routerid", routerid);
                JSONArray array = new JSONArray();
                for (Devices device:devices) {
                    JSONObject object1 = new JSONObject();
                    object1.put("macid", device.getMacId());
                    object1.put("ip", device.getIp());
                    object1.put("name", device.getDeviceName());
                    array.put(object1);
                }
                object.put("data", array);

                Log.d("TAG", object.toString());
                new SyncTask().execute(object.toString());


            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (item.getItemId() == R.id.poweron) {

            for (int i = 0; i < devices.size(); i++) {
                new SendTask().execute(devices.get(i).getIp(), devices.get(i).getDeviceName(), "1");
                handler.updateStatus("1", devices.get(i).getId() + "");
            }

            deviceListView.invalidateViews();
            adapter.notifyDataSetChanged();

        } else if (item.getItemId() == R.id.poweroff) {

            for (int i = 0; i < devices.size(); i++) {
                new SendTask().execute(devices.get(i).getIp(), devices.get(i).getDeviceName(), "0");
                handler.updateStatus("0", devices.get(i).getId() + "");
            }
            deviceListView.invalidateViews();
            adapter.notifyDataSetChanged();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == emergencyButton.getId()) {
            Toast.makeText(DeviceListActivity.this, "Long press the button to shutdown the power...", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(DeviceListActivity.this, "Long press the button to switch on the power...", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public boolean onLongClick(View view) {

        ArrayList<String> ips = new ArrayList<String>();

        if (view.getId() == emergencyButton.getId()) {
            DataBaseHandler handler = new DataBaseHandler(this);
            ArrayList<Devices> devices = handler.getAllDevices();
            for (int i = 0; i < devices.size(); i++) {
                new SendTask().execute(devices.get(i).getIp(), devices.get(i).getDeviceName(), "0");
                handler.updateStatus("0", devices.get(i).getId() + "");
            }

        } else if (view.getId() == powerButton.getId()) {

            DataBaseHandler handler = new DataBaseHandler(this);
            ArrayList<Devices> devices = handler.getAllDevices();
            for (int i = 0; i < devices.size(); i++) {
                new SendTask().execute(devices.get(i).getIp(), devices.get(i).getDeviceName(), "1");
                handler.updateStatus("1", devices.get(i).getId() + "");
            }

        }

        deviceListView.invalidateViews();
        adapter.notifyDataSetChanged();

        return false;
    }

    private class SendTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            String s = SendData(params[0], params[1], params[2]);
            Log.d("TAG", s);
            return s;
        }



        private String SendData(String ip, String name, String status) {
            String resp = "";

            try {
                URL url = new URL("http://" + ip + "/" + name + "/" + status);
                Log.d("TAG", url.toString());
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder builder = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    builder.append(line + "\n");
                }
                resp = builder.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return resp;
        }
    }

    private class SyncTask extends AsyncTask<String, Void, String> {
        HttpConnection connection = new HttpConnection();
        String resp;
        ProgressDialog dialog = new ProgressDialog(DeviceListActivity.this);

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Syncing.....");
            dialog.show();

        }

        @Override
        protected String doInBackground(String... params) {
            resp = connection.send(params[0]);
            return resp;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("TAG", s);
            dialog.dismiss();
            if (s.equals("OK")) {
                Toast.makeText(DeviceListActivity.this, "Data syncing is successfull....", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
