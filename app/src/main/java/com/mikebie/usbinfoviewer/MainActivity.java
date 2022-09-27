package com.mikebie.usbinfoviewer;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.serenegiant.usb.USBMonitor;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private Button btnRefresh;
    private TextView devicesInfo;
    private UsbManager manager;
    private USBMonitor mUSBMonitor;
    private Handler handler;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnRefresh = findViewById(R.id.btnRefresh);
        devicesInfo = findViewById(R.id.infoContent);

        handler = new Handler();
        manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        mUSBMonitor = new USBMonitor(this, new USBMonitor.OnDeviceConnectListener() {
            @Override
            public void onAttach(UsbDevice device) {
                Toast.makeText(MainActivity.this, "USB_DEVICE_ATTACHED", Toast.LENGTH_SHORT).show();
                btnRefresh.callOnClick();
            }

            @Override
            public void onDettach(UsbDevice device) {
                Toast.makeText(MainActivity.this, "USB_DEVICE_DETACHED", Toast.LENGTH_SHORT).show();
                btnRefresh.callOnClick();
            }

            @Override
            public void onConnect(UsbDevice device, USBMonitor.UsbControlBlock ctrlBlock, boolean createNew) {

            }

            @Override
            public void onDisconnect(UsbDevice device, USBMonitor.UsbControlBlock ctrlBlock) {

            }

            @Override
            public void onCancel(UsbDevice device) {

            }
        });

        StringBuilder sb = new StringBuilder();

        Runnable setTextView = () -> {
            devicesInfo.setText(sb.toString());
        };

        btnRefresh.setOnClickListener(view -> {
            sb.delete(0, sb.length());
            final HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
            if (deviceList != null && !deviceList.toString().equals("")) {
                int cnt = 0;
                for (final UsbDevice device: deviceList.values() ) {
                    cnt++;
                    sb.append("device " + cnt + ": \n");
                    sb.append("\tName: " + device.getDeviceName() + "\n");
                    sb.append("\tVendor-Id: " + device.getVendorId() + "\n");
                    sb.append("\tProduct-Id: " + device.getProductId() + "\n");
                    sb.append("\tClass: " + device.getDeviceClass() + "\n");
                    sb.append("\tSub-Class: " + device.getDeviceSubclass() + "\n");
                    sb.append("\tProtocol: " + device.getDeviceProtocol() + "\n");
                    sb.append("\tManufacturer Name: " + device.getManufacturerName() + "\n");
                    sb.append("\tProduct Name: " + device.getProductName() + "\n");
                    sb.append("\tVersion: " + device.getVersion() + "\n");
                    sb.append("\n");
                }
            }else {
                sb.append("No device connected!");
            }
            handler.post(setTextView);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mUSBMonitor.register();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUSBMonitor.unregister();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mUSBMonitor.unregister();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mUSBMonitor.register();
    }
}