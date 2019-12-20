package com.example.yummytummy_v01;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

import static app.akexorcist.bluetotohspp.library.BluetoothState.*;


public class MainActivity extends AppCompatActivity {
    final String ON = "l";
    final String OFF = "0";
    Boolean flag =  false;
    BluetoothSPP bluetooth;
    TextView text;// pump status
    Button connect;//to connect devices on bluetooth
    Button on;//send '1' or '0'
    Button TestQuery;
    ProgressBar progressbar;
    Connection con;

    //Button off;
    public void onActivityResult(int requestCode,int resultCode,Intent intent) {
        if(requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if(resultCode == Activity.RESULT_OK) {

                bluetooth.setupService();
                bluetooth.startService(DEVICE_OTHER);
                bluetooth.connect(intent);
                Toast.makeText(getApplicationContext(), "request code = connect device", Toast.LENGTH_SHORT).show();
            }
        } else if(requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if(resultCode == Activity.RESULT_OK) {
                bluetooth.setupService();
                bluetooth.startService(DEVICE_OTHER);
                Toast.makeText(getApplicationContext(), "request code = enable bt", Toast.LENGTH_SHORT).show();

            } else {
                // Do something if user doesn't choose any device (Pressed back)
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bluetooth = new BluetoothSPP(this);

        connect = (Button) findViewById(R.id.connect);
        on = (Button) findViewById(R.id.ON);
        text = (TextView) findViewById(R.id.Status);
        TestQuery = (Button) findViewById(R.id.servoOn);
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

                TestQuery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       // Toast.makeText(getApplicationContext(), "testing query", Toast.LENGTH_LONG).show();
                        CheckLogin checklogin = new CheckLogin();
                        checklogin.execute("");
                        // testing the insertion into the database
                        try {
                            String query = "insert into [users_info].[users_info] (username,password,email,dateofbirth) values ('testinsert','123','1testcom','6940-12-07')"; //query for inserting
                           // String query = "DELETE FROM [users_info].[users_info] WHERE username='testinsert'";
                            con = (new connectionClass()).establish_Connection();
                            if(con==null){
                                Toast.makeText(getApplicationContext(), "connection is null", Toast.LENGTH_LONG).show();
                            }
                            Toast.makeText(getApplicationContext(), "connection is not null", Toast.LENGTH_LONG).show();
                            Statement stat = con.createStatement();
                            Toast.makeText(getApplicationContext(), "statement working", Toast.LENGTH_LONG).show();
                            int rs = stat.executeUpdate(query);
                            Toast.makeText(getApplicationContext(),  Integer.toString(rs), Toast.LENGTH_LONG).show();

                            Toast.makeText(getApplicationContext(), "query executed succesfuly", Toast.LENGTH_LONG).show();
                            //con.close();
                        }
                            catch(Exception e){

                                Log.e("sql error in inserting",e.getMessage());
                            }
                    }
                });
            }
        });
    }
        public class CheckLogin extends AsyncTask<String,String,String>{
            String z = "";
            Boolean isSuccess=false;
            String name1="";

            protected void onPreExecute(){
                    progressbar.setVisibility(View.VISIBLE);
              //  Toast.makeText(getApplicationContext(), "pre execute", Toast.LENGTH_LONG).show();
            }

            public Boolean getSuccess() {
                return isSuccess;
            }
            @Override
            public void onPostExecute(String r){
                progressbar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), r, Toast.LENGTH_LONG).show();
                if(isSuccess){
                    text.setText("succesfuly retrieved user:"+name1);
                }
            }
            @Override
            public String doInBackground(String... params){
                try{
                    con = (new connectionClass()).establish_Connection();
                    if(con==null){
                        z = "check your Internet Access!";
                    }
                    else{
                        String query = "SELECT * FROM [users_info].[users_info] where username='julian'";
                        Statement stat = con.createStatement();
                        ResultSet rs = stat.executeQuery(query);
                        if(rs.next()){
                            name1 = rs.getString("username");
                            z = "Query succesful";
                            isSuccess = true;
                            //con.close();

                        }
                        else{
                            z = "invalid query";
                            isSuccess = false;
                        }
                    }
                }
                catch(Exception e){
                    z = e.getMessage();
                    isSuccess=false;
                    Log.e("sql error",z);
                }
                return z;
            }
        }
/*
        @SuppressLint("newApi")
        public Connection connectionclass(){
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Connection connection = null;
            String connectionURL=null;
            try{
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                connectionURL="jdbc:jtds:sqlserver://yummytummy.database.windows.net:1433;DatabaseName=Users;user=yummyAdmin@yummytummy;password=Juliankhalidaya1995;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";
                connection = DriverManager.getConnection(connectionURL);

            }
            catch(SQLException e){
                Log.e("error here 1:"+e.getErrorCode(), e.getMessage());
            }
            catch(ClassNotFoundException e){
                Log.e("error here 2:", e.getMessage());
            }
            catch(Exception e){
                Log.e("error here 3:", e.getMessage());
            }
            return connection;
        }




        off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetooth.send(OFF, true);
            }
        });


    }

    public void OnConnectClick(View v)
    {
        TextView text = (TextView) findViewById(R.id.Status);
        Toast.makeText(this, "Clicked on Connect", Toast.LENGTH_LONG).show();
        text.setText("Connected");
    }
    public void onSwitch(View v)
    {
        TextView text = (TextView) findViewById(R.id.Status);
        Toast.makeText(this, "Clicked on switch", Toast.LENGTH_LONG).show();
        text.setText("Pump ON");
    }*/
}
