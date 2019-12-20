package com.example.yummytummy_v01;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

import static app.akexorcist.bluetotohspp.library.BluetoothState.DEVICE_OTHER;
import static app.akexorcist.bluetotohspp.library.BluetoothState.REQUEST_CONNECT_DEVICE;
import static app.akexorcist.bluetotohspp.library.BluetoothState.STATE_CONNECTED;

public class Main2Activity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    Connection con;
    User user;
    TextView header_name;
    TextView header_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_history,
                R.id.nav_account_edit, R.id.nav_connect_bluetooth, R.id.nav_connect_wifi)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        View header = navigationView.getHeaderView(0);
        header_name=header.findViewById(R.id.header_name);
        header_email=header.findViewById(R.id.header_email);
        user= User.createUser(this.getIntent().getExtras().getString("USERNAME"));
        if(user!=null){
            header_name.setText(user.getUsername());
            header_email.setText(user.getEmail());
            Toast.makeText(getApplicationContext(), user.getMyList().size()+"", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getApplicationContext(), "problem retrieving user data", Toast.LENGTH_SHORT).show();
        }


/*
        bluetooth = new BluetoothSPP(this);

        connect = (Button) findViewById(R.id.connect);
        on = (Button) findViewById(R.id.ON);
        text = (TextView) findViewById(R.id.Status);
        TestQuery = (Button) findViewById(R.id.queryTest);
        progressbar = (ProgressBar)findViewById(R.id.progressBar);
        //off = (Button) findViewById(R.id.off);
        text.setText("Turn On bluetooth ");

        bluetooth.setBluetoothStateListener(new BluetoothSPP.BluetoothStateListener() {
            public void onServiceStateChanged(int state) {
                if (state == BluetoothState.STATE_CONNECTED) {
                    Toast.makeText(getApplicationContext(), "Bluetooth is connected", Toast.LENGTH_SHORT).show();
                }
                // Do something when successfully connected
                else if (state == BluetoothState.STATE_CONNECTING) {
                    Toast.makeText(getApplicationContext(), "Bluetooth is connecting", Toast.LENGTH_SHORT).show();
                }
                // Do something while connecting
                else if (state == BluetoothState.STATE_LISTEN) {
                    Toast.makeText(getApplicationContext(), "Bluetooth is listening", Toast.LENGTH_SHORT).show();
                }
                // Do something when device is waiting for connection
                else if (state == BluetoothState.STATE_NONE) {
                    Toast.makeText(getApplicationContext(), "Bluetooth is doing nothing", Toast.LENGTH_SHORT).show();
                }
                // Do something when device don't have any connection
            }
        });

        bluetooth.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            public void onDeviceConnected(String name, String address) {
                connect.setText("Connected to " + name);
                text.setText("Connected to" + name);
            }

            public void onDeviceDisconnected() {
                connect.setText("Connection lost");
            }

            public void onDeviceConnectionFailed() {
                connect.setText("Unable to connect");
            }

        });
//122482
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluetooth.isBluetoothAvailable()) {

                    if (bluetooth.getServiceState() == STATE_CONNECTED) {
                        bluetooth.disconnect();
                        connect.setText("Connect");
                    } else {
                        Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                        startActivityForResult(intent, REQUEST_CONNECT_DEVICE);


                    }

                    //finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Bluetooth is not available", Toast.LENGTH_SHORT).show();
                }

                on.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (bluetooth.getServiceState() == STATE_CONNECTED) {
                            if (!flag) {
                                bluetooth.send(ON, true);
                                flag = true;
                            } else {
                                bluetooth.send(OFF, true);
                                flag = false;
                            }

                        } else {
                            Toast.makeText(getApplicationContext(), "Device not connected", Toast.LENGTH_LONG).show();
                            text.setText("Pump ON");
                        }

                    }
                });


            }
        });*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
