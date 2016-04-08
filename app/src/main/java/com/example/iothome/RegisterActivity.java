package com.example.iothome;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_WIFI = 1;
    EditText usernameEditText;
    EditText passwordEditText;
    EditText confpasswordEditText;
    Button saveButton;
    WifiManager manager;
    String macid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setUI();
    }

    private void setUI() {

       if (Build.VERSION.SDK_INT >= 23) {
           if (checkSelfPermission(Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_GRANTED) {

           } else {
               if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_WIFI_STATE)) {
                   Toast.makeText(RegisterActivity.this, "Wifi permission is needed for working..!",
                           Toast.LENGTH_SHORT).show();
               }

               requestPermissions(new String[] {Manifest.permission.ACCESS_WIFI_STATE}, REQUEST_WIFI);
           }
       }

        manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
       // macid = info.getMacAddress();
        macid = getMacAddr();
        Log.d("TAG", macid);
        /*Log.d("TAG", info.getSSID());
        Log.d("TAG", info.getRssi() + "");*/


        usernameEditText = (EditText) findViewById(R.id.usernameEditText1);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText1);
        confpasswordEditText = (EditText) findViewById(R.id.confpwdeditText);
        saveButton = (Button) findViewById(R.id.saveButton2);

        saveButton.setOnClickListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == REQUEST_WIFI) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                WifiInfo connectionInfo = manager.getConnectionInfo();
                Log.d("TAG", connectionInfo.getMacAddress());
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    @Override
    public void onClick(View view) {


        if (view.getId() == saveButton.getId()) {
            if (passwordEditText.getText().toString().equals(confpasswordEditText.getText().toString())) {

                new SendData().execute(usernameEditText.getText().toString(), passwordEditText.getText().toString(), macid);

            } else {
                Toast.makeText(RegisterActivity.this, "Passwords are mismatching", Toast.LENGTH_SHORT).show();
                confpasswordEditText.requestFocus();
            }
            //new SendData().execute("param1", "param2", "param3");
        }

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

    private class SendData extends AsyncTask<String, Void, String> {
        String resp = "";
        @Override
        protected String doInBackground(String... params) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("username", params[0]);
                obj.put("password", params[1]);
                obj.put("macid", params[2]);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("TAG", obj.toString());
            resp = SendVal(obj.toString());
            Log.d("TAG", "response : " + resp);
            return resp;
        }

        private String SendVal(String s) {
            BufferedReader reader;
            String text = "";

            try {
                //URL url = new URL("http://192.168.0.114:8082/registration");
                URL url = new URL("http://iothomeautomation.mybluemix.net:80/registration");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);

                //Send data using POST
                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write("val=" + s);
                writer.flush();

                // Response
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = reader.readLine()) != null)
                {
                    // Append server response in string
                    sb.append(line + "\n");
                }


                text = sb.toString();
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            return text;
        }


    }
}
