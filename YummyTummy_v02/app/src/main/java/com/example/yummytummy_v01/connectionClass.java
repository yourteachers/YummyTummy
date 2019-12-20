package com.example.yummytummy_v01;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class connectionClass {
    @SuppressLint("newApi")
    public static Connection establish_Connection(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String connectionURL;
        try{
            Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
            connectionURL="jdbc:jtds:sqlserver://yummytummy-server.database.windows.net:1433;DatabaseName=YummyTummy;user=yummyAdmin@yummytummy-server;password=Juliankhalidaya1995;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";
            //old connection
          //  connectionURL="jdbc:jtds:sqlserver://yummytummy.database.windows.net:1433;DatabaseName=Users;user=yummyAdmin@yummytummy;password=Juliankhalidaya1995;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";
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
}
