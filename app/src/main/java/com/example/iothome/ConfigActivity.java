package com.example.iothome;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.adapters.DeviceAdapter;
import com.example.containers.Devices;
import com.example.db.DataBaseHandler;
import com.example.utils.Appconstants;

import java.util.ArrayList;

public class ConfigActivity extends AppCompatActivity {

    ListView listView = null;
    DataBaseHandler db = null;
    DeviceAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Configuration");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        FloatingActionButton actionButton = (FloatingActionButton) findViewById(R.id.fab);

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ConfigActivity.this, NewDeviceActivity.class));
            }
        });

        listView = (ListView) findViewById(R.id.listView);
        db = new DataBaseHandler(this);
        ArrayList<Devices> devices = db.getAllDevices();
        adapter = new DeviceAdapter(this, devices);
        listView.setAdapter(adapter);
        listView.setDividerHeight(5);

        registerForContextMenu(listView);

    }

    @Override
    protected void onResume() {
        super.onResume();
        ArrayList<Devices> devices = db.getAllDevices();
        adapter = new DeviceAdapter(this, devices);
        listView.setAdapter(adapter);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Select action..");
        menu.add("Edit");
        menu.add("Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        Devices device = null;
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();
        int position = info.position;

        if (adapter != null) {
            device = adapter.getItem(position);
        }

        if (item.getTitle().equals("Edit")) {

            if (device != null) {
                Intent intent = new Intent(ConfigActivity.this, EditDeviceActivity.class);
                intent.putExtra(Appconstants.DEVICE_INFO, device);
                startActivity(intent);
            }

        } else if (item.getTitle().equals("Delete")) {
            boolean val;
            if (device != null) {
                val = db.deleteDevice(device.getId() + "");
                if (val) {
                    Toast.makeText(ConfigActivity.this, "Deleted Successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ConfigActivity.this, "Deletion Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            return false;
        }
        return true;

    }
}