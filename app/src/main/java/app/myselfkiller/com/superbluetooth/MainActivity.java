package app.myselfkiller.com.superbluetooth;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.myselfkiller.superbluetoothlibrary.SBluetooth;
import com.myselfkiller.superbluetoothlibrary.SBluetoothListener;


public class MainActivity extends CheckPermissionsActivity implements SBluetoothListener {
    private Button serarch_btu,connect_btu;
    private SBluetooth sBluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        serarch_btu = findViewById(R.id.search_btu);
        connect_btu = findViewById(R.id.connect_btu);

        serarch_btu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sBluetooth.searchBluetooth();
            }
        });

        connect_btu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        sBluetooth = SBluetooth.getInstance(this);
        sBluetooth.setListener(this);
    }

    @Override
    public void onBluetoothNotSupported() {

    }

    @Override
    public void onBluetoothDisabled() {

    }

    @Override
    public void onBluetoothDeviceDisconnected() {

    }

    @Override
    public void onConnectingBluetoothDevice() {

    }

    @Override
    public void onBluetoothDeviceConnected(String name, String address) {

    }

    @Override
    public void onBluetoothSerialRead(String message) {

    }

    @Override
    public void onBluetoothSerialWrite(String message) {

    }

    @Override
    public void onBluetoothDeviceFound(BluetoothDevice device) {

    }
}
