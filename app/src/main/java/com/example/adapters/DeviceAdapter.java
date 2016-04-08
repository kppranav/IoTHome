package com.example.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.containers.Devices;
import com.example.iothome.R;

import java.util.ArrayList;

/**
 * Created by uvionics on 11/2/16.
 */
public class DeviceAdapter extends ArrayAdapter<Devices> {

    Context context;
    ArrayList<Devices> list;

    public DeviceAdapter(Context context, ArrayList<Devices> list){
        super(context, R.layout.row, list);
        this.context = context;
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row, parent, false);
        }

        String type = list.get(position).getDeviceType();
        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
        TextView nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);
        TextView typeTextView = (TextView) convertView.findViewById(R.id.typeTextView);
        TextView powerTextView = (TextView) convertView.findViewById(R.id.powerTextView);
        nameTextView.setText(list.get(position).getDeviceName());
        typeTextView.setText(type);
        powerTextView.setText(list.get(position).getPowerConsumption() + "w");
        if (type.equals("Light")) {
            imageView.setImageResource(R.drawable.light_bulb_icon_white);
        } else if (type.equals("Fan")) {
            imageView.setImageResource(R.drawable.fan_black_md_white);
        } else if (type.equals("Door")) {
            imageView.setImageResource(R.drawable.door_white);
        } else if (type.equals("refrigerator")) {
            imageView.setImageResource(R.drawable.fridge_ic_white);
        } else if (type.equals("Washing machine")) {
            imageView.setImageResource(R.drawable.washing_machine_ic_white);
        }

        return convertView;
    }
}
