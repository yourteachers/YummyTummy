package com.example.yummytummy_v01;

import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.sax.StartElementListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

import static app.akexorcist.bluetotohspp.library.BluetoothState.DEVICE_OTHER;
import static app.akexorcist.bluetotohspp.library.BluetoothState.REQUEST_CONNECT_DEVICE;
import static app.akexorcist.bluetotohspp.library.BluetoothState.STATE_CONNECTED;

public class Connect extends Fragment {

    private ConnectViewModel mViewModel;
    Button BTconnect;
    Button startAll;
    Button startNoHeat;
    TextInputEditText water;
    TextInputEditText scoops;
    TextInputEditText child;
    TextInputLayout waterInputLayout;
    TextInputLayout childInputLayout;
    TextInputLayout scoopsInputLayout;
    BluetoothSPP bluetooth;
    final String activateMachineNoHeat="fn";
    final String activateMachine="f";
    User user;
    Bundle args ;
    String wAmount;
    String sAmount;
    String childName;
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

    public static Connect newInstance() {
        return new Connect();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.connect_fragment, container, false);
        BTconnect = root.findViewById(R.id.BTconnect);
        startAll = root.findViewById(R.id.StartAll);
        startNoHeat = root.findViewById(R.id.StartNoHeat);
        water = root.findViewById(R.id.Water);
        scoops = root.findViewById(R.id.Scoops);
        child = root.findViewById(R.id.childName);
        waterInputLayout = root.findViewById(R.id.watertextInputLayout);
        childInputLayout = root.findViewById(R.id.childtextInputLayout);
        scoopsInputLayout = root.findViewById(R.id.scoopstextInputLayout);
        bluetooth = new BluetoothSPP(getActivity());
        args= getActivity().getIntent().getExtras();
        user= User.createUser(args.getString("USERNAME"));
        startAll.setEnabled(false);
        startNoHeat.setEnabled(false);

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
                BTconnect.setText("Connected to " + name);
                startAll.setEnabled(true);
                startNoHeat.setEnabled(true);
            }

            public void onDeviceDisconnected() {
                BTconnect.setText("Connection lost");
                startAll.setEnabled(false);
                startNoHeat.setEnabled(false);
            }

            public void onDeviceConnectionFailed() {
                BTconnect.setText("Unable to connect");
            }

        });

        bluetooth.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            public void onDataReceived(byte[] data, String message) {
                // Do something when data incoming
                Toast.makeText(getActivity(), "recieved sometthing", Toast.LENGTH_SHORT).show();
               //the machine responded with OK meaning it started the proccess of creating a bottle
                if(message=="OK"){
                    user.addBottle(wAmount,sAmount,childName);
                }
               // tempText.setText(message);
            }
        });
        BTconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluetooth.isBluetoothAvailable()) {

                    if (bluetooth.getServiceState() == STATE_CONNECTED) {
                        bluetooth.disconnect();
                        BTconnect.setText("CONNECT VIA BLUETOOTH");
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
        startAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* String waterAmount = water.getText().toString();
                String scoopsAmount = scoops.getText().toString();
                String name = child.getText().toString();

                if(validate(waterAmount,scoopsAmount,name)){
                    wAmount = waterAmount;
                    sAmount = scoopsAmount;
                    childName=name;
                    user.addBottle(wAmount,sAmount,childName);
                    Toast.makeText(getActivity(), "Bottle Added", Toast.LENGTH_LONG).show();
                }*/
                if (bluetooth.getServiceState() == STATE_CONNECTED) {
                    String waterAmount = water.getText().toString();
                    String scoopsAmount = scoops.getText().toString();
                    String name = child.getText().toString();

                    if(validate(waterAmount,scoopsAmount,name)){
                        wAmount = waterAmount;
                        sAmount = scoopsAmount;
                        childName=name;
                        bluetooth.send(activateMachine+","+waterAmount+","+scoopsAmount, true);
                    }




                } else {
                    Toast.makeText(getActivity(), "Device not connected", Toast.LENGTH_LONG).show();

                }
            }
        });
        startNoHeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluetooth.getServiceState() == STATE_CONNECTED) {
                    String waterAmount = water.getText().toString();
                    String scoopsAmount = scoops.getText().toString();
                    String name = child.getText().toString();

                    if(validate(waterAmount,scoopsAmount,name)){
                        wAmount = waterAmount;
                        sAmount = scoopsAmount;
                        childName=name;
                        bluetooth.send(activateMachineNoHeat+","+waterAmount+","+scoopsAmount, true);
                    }




                } else {
                    Toast.makeText(getActivity(), "Device not connected", Toast.LENGTH_LONG).show();

                }
            }
        });
        return root;
    }
    private boolean validate(String waterAmount,String scoopsAmount,String name){
        if(waterAmount.length()==0){
            waterInputLayout.setError("Enter amount of water between 100-500 mL");
            scoopsInputLayout.setError(null);
            childInputLayout.setError(null);
            return false;
        }
        if(scoopsAmount.length()==0){
            scoopsInputLayout.setError("Enter amount of scoops between 1-4");
            waterInputLayout.setError(null);
            childInputLayout.setError(null);
            return false;
        }
        if(name.length()==0){
            childInputLayout.setError("Enter a child name");
            waterInputLayout.setError(null);
            scoopsInputLayout.setError(null);
            return false;
        }
        int wAmount = Integer.parseInt(waterAmount);
        int sAmount = Integer.parseInt(scoopsAmount);
        if(wAmount>500 || wAmount<100){
            waterInputLayout.setError("Enter amount of water between 100-500 mL");
            scoopsInputLayout.setError(null);
            return false;
        }
        if(sAmount<1 || sAmount>4){
            scoopsInputLayout.setError("Enter amount of scoops between 1-4");
            waterInputLayout.setError(null);
            return false;
        }
        return true;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ConnectViewModel.class);
        // TODO: Use the ViewModel
    }

}
