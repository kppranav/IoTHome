package com.example.utils;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by uvionics on 15/3/16.
 */
public class HttpConnection {

    public String send(String s) {

        BufferedReader reader;
        String text = "";

        try {
            //URL url = new URL("http://192.168.0.101:8082/SENSOR_AIR");
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

    public String sendData(String s) {

        BufferedReader reader;
        String text = "";

        try {
            URL url = new URL("http://192.168.0.105:8082/test");
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

    public String sendVal(String s) {

        BufferedReader reader;
        String text = "";

        try {
            URL url = new URL(s);
            Log.d("TAG", url.toString());
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                builder.append(line + "\n");
            }
            text = builder.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return text;
    }
}
