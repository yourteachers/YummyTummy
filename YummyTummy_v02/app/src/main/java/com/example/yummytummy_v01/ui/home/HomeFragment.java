package com.example.yummytummy_v01.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.yummytummy_v01.R;
import com.example.yummytummy_v01.User;

import java.sql.Connection;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

import static app.akexorcist.bluetotohspp.library.BluetoothState.DEVICE_OTHER;
import static app.akexorcist.bluetotohspp.library.BluetoothState.REQUEST_CONNECT_DEVICE;
import static app.akexorcist.bluetotohspp.library.BluetoothState.STATE_CONNECTED;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    Bundle args ;
    final String ON = "p";
    final String OFF = "o";
    final String servo = "s";
    final String temp= "t";
    final String activateMachine="f";
    Boolean flag =  false;
    BluetoothSPP bluetooth;
    TextView text;// pump status
    Button connect;//to connect devices on bluetooth
    Button PumpOn;//send '1' or '0'
    Button Servo;
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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        args= getActivity().getIntent().getExtras();
        bluetooth = new BluetoothSPP(getActivity());

        connect = (Button) root.findViewById(R.id.connect);
        PumpOn = (Button) root.findViewById(R.id.ON);
        text = (TextView) root.findViewById(R.id.Status);
        Servo = (Button) root.findViewById(R.id.servoOn);
        tempBut = (Button) root.findViewById(R.id.tempBut);
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
                Toast.makeText(getActivity(), "recieved sometthing", Toast.LENGTH_SHORT).show();
                tempText.setText(message);
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

                                text.setText("Servo activated");


                        } else {
                            Toast.makeText(getActivity(), "Device not connected", Toast.LENGTH_LONG).show();

                        }

                    }
                });
        tempBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluetooth.getServiceState() == STATE_CONNECTED) {

                    bluetooth.send(activateMachine, true);



                } else {
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
                        text.setText("Pump ON");
                    } else {
                        bluetooth.send(OFF, true);
                        flag = false;
                        text.setText("Pump OFF");
                    }

                } else {
                    Toast.makeText(getActivity(), "Device not connected", Toast.LENGTH_LONG).show();

                }

            }
        });
                return root;
    }
}