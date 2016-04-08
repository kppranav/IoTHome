package com.example.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.containers.Devices;
import com.example.db.DataBaseHandler;
import com.example.iothome.R;
import com.example.iothome.SpeedCntrlActivity;
import com.example.utils.Appconstants;
import com.example.utils.ConnectionDetector;
import com.example.utils.DeviceInfo;
import com.example.utils.HttpConnection;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * Created by uvionics on 16/2/16.
 */
public class DeviceOpAdapter extends ArrayAdapter<Devices> {
    Context context;
    ArrayList<Devices> list;
    String status = Appconstants.OFF;
    //String status;
    ConnectionDetector detector;

    DataBaseHandler handler;

    WifiManager manager;

    public DeviceOpAdapter(Context context, ArrayList<Devices> list) {
        super(context, R.layout.row, list);
        this.context = context;
        this.list = list;
        manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        handler = new DataBaseHandler(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row2, parent, false);
        }

        //status = list.get(position).getStatus();
        status = handler.getStatus(list.get(position).getId() + "");

        final String type = list.get(position).getDeviceType();
        TextView nameTextView = (TextView) convertView.findViewById(R.id.nameTextView2);
        TextView typeTextView = (TextView) convertView.findViewById(R.id.typeTextView2);
        TextView powerTextView = (TextView) convertView.findViewById(R.id.powerTextView2);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView2);
        final Button cntrlBtn = (Button) convertView.findViewById(R.id.cntrlSwitch);


            if (status.equals(Appconstants.OFF)) {

                cntrlBtn.setBackgroundResource(R.drawable.off_button);
            } else {
                cntrlBtn.setBackgroundResource(R.drawable.on_button);
            }

        //Log.d("TAG", status);

        nameTextView.setText(list.get(position).getDeviceName());
        typeTextView.setText(list.get(position).getDeviceType());
        powerTextView.setText(list.get(position).getPowerConsumption() + " w");
        if (type.equals("Light")) {
            imageView.setImageResource(R.drawable.light_bulb_icon_white);
        } else if (type.equals("Fan")) {
            imageView.setImageResource(R.drawable.fan_ic_white);
        } else if (type.equals("Door")) {
            imageView.setImageResource(R.drawable.door_white);
        } else if (type.equals("refrigerator")) {
            imageView.setImageResource(R.drawable.fridge_ic_white);
        } else if (type.equals("Washing machine")) {
            imageView.setImageResource(R.drawable.washing_machine_ic_white);
        }

        cntrlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //Log.d("TAG", handler.getStatus(list.get(position).getId() + ""));
               status = handler.getStatus(list.get(position).getId() + "");
                SendTask task = new SendTask();
                Log.d("TAG", status);
                Log.d("TAG", manager.getConnectionInfo().getSSID());
                if (manager.isWifiEnabled()) {

                    if ((manager.getConnectionInfo().getSSID().equals("\"" + Appconstants.SSID + "\""))) {

                        Log.d("TAG", "status : " + status);
                        if (status.equals(Appconstants.OFF)) {
                            task.execute(list.get(position).getIp(), list.get(position).getDeviceName(), "1");
                            status = Appconstants.ON;
                            cntrlBtn.setBackgroundResource(R.drawable.on_button);
                            handler.updateStatus(Appconstants.ON, list.get(position).getId() + "");

                        } else if (status.equals(Appconstants.ON)){

                            task.execute(list.get(position).getIp(), list.get(position).getDeviceName(), "0");
                            status = Appconstants.OFF;
                            cntrlBtn.setBackgroundResource(R.drawable.off_button);
                            handler.updateStatus(Appconstants.OFF, list.get(position).getId() + "");
                        }

                    } else {

                        JSONObject object = new JSONObject();
                       /* SharedPreferences preferences = context.getSharedPreferences
                                (Appconstants.MyPREFERENCES, Context.MODE_PRIVATE);
                        String macid = preferences.getString(Appconstants.MyPREFERENCES, "null");*/
                        String macid = new DeviceInfo().getMacAddr();
                        try {
                            object.put("macid", macid);
                            object.put("device_ip", list.get(position).getIp());
                            object.put("device_mac", list.get(position).getMacId());
                            object.put("device_name", list.get(position).getDeviceName());
                            if (status.equals(Appconstants.OFF)) {
                                cntrlBtn.setBackgroundResource(R.drawable.on_button);
                                object.put("status", "1");
                                status = Appconstants.ON;
                                boolean b = new DataBaseHandler(context).updateStatus("1", list.get(position).getId() + "");

                            } else {
                                cntrlBtn.setBackgroundResource(R.drawable.off_button);
                                object.put("status", "0");
                                status = Appconstants.OFF;
                                boolean b = new DataBaseHandler(context).updateStatus("0", list.get(position).getId() + "");

                            }
                            Log.d("TAG", object.toString());
                            //new SendToBM().execute(object.toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {


                    Log.d("TAG", "Network not available!");

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Warning!");
                    builder.setMessage("You haven't connected to any of the network. Please " +
                            "connect to a network and try again. ");
                    builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

                //notifyDataSetChanged();

            }
        });

        cntrlBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Log.d("TAG", status);
                if (status.equals(Appconstants.ON)) {
                    Devices devices = list.get(position);
                    Log.d("TAG", "Speed Control");
                    Intent intent = new Intent(context, SpeedCntrlActivity.class);
                    intent.putExtra(Appconstants.DEVICE_INFO, devices);
                    context.startActivity(intent);

                } else {

                        Toast.makeText(context, "Please switch on the device first to control...!", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        return convertView;
    }

    static class ViewHolder {
        TextView nameTextView;
        TextView typeTextView;
        TextView powerTextView;
        ImageView imageView;
        Button cntrlButton;
    }

    private class SendTask extends AsyncTask<String, Void, String> {
        //ProgressDialog dialog = new ProgressDialog(context);

        @Override
        protected String doInBackground(String... params) {
            String resp = SendData(params[0], params[1], params[2]);
            Log.d("TAG", resp);
            return null;
        }

        private String SendData(String ip, String name, String status) {

            String url = "http://" + ip + "/" + name + "/" + status;
            Log.d("TAG", url);
            String serverResponse = "";
            HttpClient httpclient = new DefaultHttpClient();
            try {
                HttpGet httpGet = new HttpGet();
                httpGet.setURI(new URI(url));
                HttpResponse httpResponse = httpclient.execute(httpGet);

                InputStream inputStream = null;
                inputStream = httpResponse.getEntity().getContent();
                BufferedReader bufferedReader =
                        new BufferedReader(new InputStreamReader(inputStream));

                StringBuilder builder = new StringBuilder();
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    builder.append(line + "\n");
                }
               serverResponse = builder.toString();
                Log.d("TAG", serverResponse);

                inputStream.close();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.getMessage();
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.getMessage();
                e.printStackTrace();
            }

            return serverResponse;
        }


    }

    private class SendToBM extends AsyncTask<String, Void, String> {
        String resp;
        @Override
        protected String doInBackground(String... params) {
            HttpConnection connection = new HttpConnection();
            resp = connection.sendData(params[0]);
            Log.d("TAG", resp);
            return resp;
        }

        @Override
        protected void onPostExecute(String s) {

            if (s.equals("OK")) {
                Toast.makeText(context, "Data saved Successfully..", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
