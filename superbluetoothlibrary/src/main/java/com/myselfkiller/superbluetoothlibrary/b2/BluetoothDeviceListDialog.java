package com.myselfkiller.superbluetoothlibrary.b2;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BluetoothDeviceListDialog {

    /**
     * Listener for the {@link com.myselfkiller.superbluetoothlibrary.b2.BluetoothSerialListener}.
     */
    public interface OnDeviceSelectedListener {

        /**
         * A remote Bluetooth device is selected from the dialog.
         *
         * @param device The selected device.
         */
        void onBluetoothDeviceSelected(BluetoothDevice device);

    }

    private Context mContext;
    private OnDeviceSelectedListener mListener;
    private Set<BluetoothDevice> mDevices;
    private List<BluetoothDevice> deviceList;
    private String mTitle;
    private boolean mShowAddress = true;
    private boolean mUseDarkTheme;
    private BluetoothDeviceListItemAdapter bluetoothDeviceListItemAdapter;
    /**
     * Constructor.
     *
     * @param context The {@link Context} to use.
     */
    public BluetoothDeviceListDialog(Context context) {
        mContext = context;
        bluetoothDeviceListItemAdapter = new BluetoothDeviceListItemAdapter(mContext,deviceList,mShowAddress);
    }

    /**
     * Set a listener to be invoked when a remote Bluetooth device is selected.
     *
     * @param listener The {@link com.myselfkiller.superbluetoothlibrary.b2.BluetoothDeviceListDialog.OnDeviceSelectedListener} to use.
     */
    public void setOnDeviceSelectedListener(OnDeviceSelectedListener listener) {
        mListener = listener;
    }

    /**
     * Set the title of the dialog.
     *
     * @param title The title string.
     */
    public void setTitle(String title) {
        mTitle = title;
    }

    /**
     * Set the title of the dialog.
     *
     * @param resId The resource ID of the title string.
     */
    public void setTitle(int resId) {
        mTitle = mContext.getString(resId);
    }

    /**
     * Set the remote Bluetooth devices to be shown on the dialog for selection.
     *
     * @param devices The remote Bluetooth devices.
     */
    public void setDevices(Set<BluetoothDevice> devices) {
        mDevices = devices;

        if (devices != null) {
            deviceList = new ArrayList<BluetoothDevice>(mDevices);
            bluetoothDeviceListItemAdapter.setDevices(deviceList);
//            mAddresses = new String[devices.size()];
//            int i = 0;
//            for (BluetoothDevice d : devices) {
//                mNames[i] = d.getName();
//                mAddresses[i] = d.getAddress();
//                i++;
//            }
        }
    }
    int x = 0;
    public void updateDevices(BluetoothDevice device){
//        boolean hasDv = false;
//        for (BluetoothDevice d : deviceList){
//            if (d.getAddress().equals(device.getAddress())){
//                hasDv = true;
//                break;
//            }
//        }
//        if (!hasDv){
//            deviceList.add(device);
//            mDevices.add(device);
//            bluetoothDeviceListItemAdapter.updateDevices(device);
//        }
        Log.e("hm","updateDevices = "+device.getAddress() +",次数="+x);
        x++;
        if (mDevices.add(device)){
            deviceList.add(device);
            bluetoothDeviceListItemAdapter.updateDevices(device);


        }
    }

    /**
     * Show the devices' MAC addresses on the dialog.
     *
     * @param showAddress Set to true to show the MAC addresses.
     */
    public void showAddress(boolean showAddress) {
        mShowAddress = showAddress;
    }

    /**
     * Force to use the dark version of Material theme on the dialog.
     *
     * @deprecated As of version 0.1.3, the library uses the AppCompat AlertDialog. Styling of the dialog should be done in styles.xml.
     * @param useDarkTheme Set to true to use the dark theme.
     */
    @Deprecated
    public void useDarkTheme(boolean useDarkTheme) {
        mUseDarkTheme = useDarkTheme;
    }

    /**
     * Show the dialog. This must be called after setting the dialog's listener, title and devices.
     */
    boolean isShowing =false;
    public void show() {
        final AlertDialog dialog = new AlertDialog.Builder(mContext)
                .setTitle(mTitle)
                .setAdapter(bluetoothDeviceListItemAdapter, null)
                .create();

        final ListView listView = dialog.getListView();
        bluetoothDeviceListItemAdapter.notifyDataSetChanged();
        if (listView != null) {
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mListener.onBluetoothDeviceSelected(BluetoothSerial.getAdapter(mContext).getRemoteDevice(deviceList.get(position).getAddress()));
                    dialog.cancel();
                    isShowing = dialog.isShowing();
                }
            });
        }
//        dialog.setOnDismissListener(dismissListener);
        dialog.setOnCancelListener(cancelListener);
        dialog.show();
        isShowing = dialog.isShowing();
    }

    public boolean isShowing(){
        return isShowing;
    }
//
//    public void dismiss(){
//        dialog.dismiss();
//    }

    private DialogInterface.OnDismissListener dismissListener;
    private DialogInterface.OnCancelListener cancelListener;
    public void setOnCancelListener(DialogInterface.OnCancelListener cancelListener){
        this.cancelListener = cancelListener;
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener dismissListener){
        this.dismissListener = dismissListener;
    }

}
