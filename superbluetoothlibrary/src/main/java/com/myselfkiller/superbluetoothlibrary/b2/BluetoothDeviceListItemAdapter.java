package com.myselfkiller.superbluetoothlibrary.b2;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;


public class BluetoothDeviceListItemAdapter extends BaseAdapter implements View.OnClickListener {

    private final Context mContext;
//    private final String[] mNames, mAddresses;
    private List<BluetoothDevice> devices;
    private final boolean mShowAddress;

    public BluetoothDeviceListItemAdapter(Context context, List<BluetoothDevice> devices, boolean showAddress) {
        mContext = context;
//        if (null == devices){
//            devices = new ArrayList<BluetoothDevice>();
//        }else {
//        }
        this.devices = devices;
//        mNames = names;
//        mAddresses = addresses;
        mShowAddress = showAddress;
    }

    public void updateDevices(BluetoothDevice device){
//        if (null == devices){
//            devices = new ArrayList<BluetoothDevice>();
//        }
//        devices.add(device);
        notifyDataSetChanged();
    }

    public void setDevices(List<BluetoothDevice> devices){
        this.devices = devices;
    }

    @Override
    public int getCount() {
        return devices.size();
    }

    @Override
    public String getItem(int position) {
        return devices.get(position).getAddress();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DeviceItem view = new DeviceItem(mContext);
        ((TextView) view.getChildAt(0)).setText(devices.get(position).getName());
        ((TextView) view.getChildAt(1)).setText(devices.get(position).getAddress());
        return view.getView();
    }

    @Override
    public void onClick(View v) {

    }

}
