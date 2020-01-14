package com.example.yummytummy_v01;

import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

import static app.akexorcist.bluetotohspp.library.BluetoothState.DEVICE_OTHER;
import static app.akexorcist.bluetotohspp.library.BluetoothState.REQUEST_CONNECT_DEVICE;
import static app.akexorcist.bluetotohspp.library.BluetoothState.STATE_CONNECTED;

public class Debug extends Fragment {

    private DebugViewModel mViewModel;
    Bundle args ;
    final String ON = "p";
    final String OFF = "o";
    final String servo = "s";
    final String temp= "t";
    final String HeatOn="h";
    final String HeatOff="c";
    Boolean flag =  false;
    Boolean heatFlag =  false;
    BluetoothSPP bluetooth;
    TextView text;// pump status
    Button connect;//to connect devices on bluetooth
    Button PumpOn;//send '1' or '0'
    Button Servo;
    Button heatBut;
    Button tempBut;
    TextView tempText;
    ProgressBar progressbar;
    Connection con;
    User user;
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if(resultCode == Activity.RESULT_OK) {

                bluetooth.setupService();
                bluetooth.startService(DEVICE_OTHER);
                bluetooth.connect(intent);
                Toast.makeText(getActivity(), "request code = connect device", Toast.LENGTH_SHORT).show();
            }
        } else if(requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if(resultCode == Activity.RESULT_OK) {
                bluetooth.setupService();
                bluetooth.startService(DEVICE_OTHER);
                Toast.makeText(getActivity(), "request code = enable bt", Toast.LENGTH_SHORT).show();

            } else {
                // Do something if user doesn't choose any device (Pressed back)
            }
        }
    }

    public static Debug newInstance() {
        return new Debug();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.debug_fragment, container, false);
        args= getActivity().getIntent().getExtras();
        bluetooth = new BluetoothSPP(getActivity());

        connect = (Button) root.findViewById(R.id.connect);
        PumpOn = (Button) root.findViewById(R.id.ON);
        text = (TextView) root.findViewById(R.id.Status);
        Servo = (Button) root.findViewById(R.id.servoOn);
        tempBut = (Button) root.findViewById(R.id.tempBut);
        heatBut = (Button) root.findViewById(R.id.heatBut);
        tempText =  root.findViewById(R.id.tempText);
        progressbar = (ProgressBar)root.findViewById(R.id.progressBar);




        bluetooth.setBluetoothStateListener(new BluetoothSPP.BluetoothStateListener() {
            public void onServiceStateChanged(int state) {
                if (state == BluetoothState.STATE_CONNECTED) {
                    Toast.makeText(getActivity(), "Bluetooth is connected", Toast.LENGTH_SHORT).show();
                }
                // Do something when successfully connected
                else if (state == BluetoothState.STATE_CONNECTING) {
                    Toast.makeText(getActivity(), "Bluetooth is connecting", Toast.LENGTH_SHORT).show();
                }
                // Do something while connecting
                else if (state == BluetoothState.STATE_LISTEN) {
                    Toast.makeText(getActivity(), "Bluetooth is listening", Toast.LENGTH_SHORT).show();
                }
                // Do something when device is waiting for connection
                else if (state == BluetoothState.STATE_NONE) {
                    Toast.makeText(getActivity(), "Bluetooth is doing nothing", Toast.LENGTH_SHORT).show();
                }
                // Do something when device don't have any connection
            }
        });

        bluetooth.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            public void onDeviceConnected(String name, String address) {
                connect.setText("Connected to " + name);
            }

            public void onDeviceDisconnected() {
                connect.setText("Connection lost");
            }

            public void onDeviceConnectionFailed() {
                connect.setText("Unable to connect");
            }

        });

        bluetooth.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            public void onDataReceived(byte[] data, String message) {
                // Do something when data incoming
                //Toast.makeText(getActivity(), "recieved something", Toast.LENGTH_SHORT).show();
                if(message.contains(".")){
                    tempText.setText("Temperature received: "+message);
                }
                else{
                    text.setText("Status: "+message);
                }

            }
        });

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluetooth.isBluetoothAvailable()) {

                    if (bluetooth.getServiceState() == STATE_CONNECTED) {
                        bluetooth.disconnect();
                        connect.setText("Connect");
                    } else {
                        Intent intent = new Intent(getActivity(), DeviceList.class);
                        startActivityForResult(intent, REQUEST_CONNECT_DEVICE);


                    }

                    //finish();
                } else {
                    Toast.makeText(getActivity(), "Bluetooth is not available", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Servo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluetooth.getServiceState() == STATE_CONNECTED) {

                    bluetooth.send(servo+'\r', true);

                    //text.setText("Servo activated");


                } else {
                    Toast.makeText(getActivity(), "Device not connected", Toast.LENGTH_LONG).show();

                }

            }
        });
        heatBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluetooth.getServiceState() == STATE_CONNECTED) {
                    if (!heatFlag) {
                        bluetooth.send(HeatOn+'\r', true);
                        heatFlag = true;
                        //tempText.setText("Heat ON");
                    } else {
                        bluetooth.send(HeatOff+'\r', true);
                        heatFlag = false;
                        //tempText.setText("Heat OFF");
                    }



                } else {
                    Toast.makeText(getActivity(), "Device not connected", Toast.LENGTH_LONG).show();

                }

            }
        });
        tempBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bluetooth.getServiceState() == STATE_CONNECTED) {
                    bluetooth.send(temp+'\r', true);
                }
                else {
                    Toast.makeText(getActivity(), "Device not connected", Toast.LENGTH_LONG).show();

                }
            }
        });
        PumpOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluetooth.getServiceState() == STATE_CONNECTED) {
                    if (!flag) {
                        bluetooth.send(ON, true);
                        flag = true;
                        //text.setText("Pump ON");
                    } else {
                        bluetooth.send(OFF, true);
                        flag = false;
                       // text.setText("Pump OFF");
                    }

                } else {
                    Toast.makeText(getActivity(), "Device not connected", Toast.LENGTH_LONG).show();

                }

            }
        });
        return  root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(DebugViewModel.class);
        // TODO: Use the ViewModel
    }

}
