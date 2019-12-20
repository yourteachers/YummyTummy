package com.example.yummytummy_v01;

import android.util.Log;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class User {
     int UID;
     String username;
     String password;
     String email;
     Date birthday;
     ArrayList<BottleHistory> myList ;//= new ArrayList<>();

    public User(int UID, String username, String password,String email, Date birthday){//, List<BottleHistory> myList) {
        this.UID = UID;
        this.username = username;
        this.password = password;
        this.email = email;
        this.birthday = birthday;
        //this.myList = myList;
    }

    public int getUID() {
        return UID;
    }

    public void setUID(int UID) {
        this.UID = UID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public ArrayList<BottleHistory> getMyList() {
        return myList;
    }

    public void setMyList(ArrayList<BottleHistory> myList) {
        this.myList = myList;
    }

    public String getEmail() {
        return email;
    }
    public static User createUser(String username){
        User user=null;
        Connection con = connectionClass.establish_Connection();
        try{
            if(con==null){
                //Toast.makeText(this, "Connection problem", Toast.LENGTH_SHORT).show();
            }
            else{
                String query = "SELECT * FROM [dbo].[Users] where username='"+username+"'";
                Statement stat = con.createStatement();
                ResultSet rs = stat.executeQuery(query);
                if(rs.next()){
                    String name=rs.getString("username");
                    String password=rs.getString("password");
                    String email=rs.getString("email");
                    Date birthdate =rs.getDate("birthdate");
                    int uID=rs.getInt("ID");
                    user = new User(uID,name,password,email,birthdate);
                    String query2 = "SELECT * FROM [dbo].[BottlesHistory] where UID='"+user.getUID()+"'";
                    Statement stat2 = con.createStatement();
                    ResultSet rs2= stat2.executeQuery(query2);
                    ArrayList<BottleHistory> bottles = new ArrayList<>();

                    while(rs2.next()){
                        String sonName=rs2.getString("childName");
                        int water=rs2.getInt("water");
                        int scoops=rs2.getInt("scoops");
                        Timestamp date =rs2.getTimestamp("date");
                        bottles.add(new BottleHistory(user.getUID(),water,scoops,sonName,date));
                    }
                    user.setMyList(bottles);


                }

                con.close();

            }


        }
        catch (Exception e){
            Log.e("error here:",e.getMessage());

            return null;
        }
        return user;
    }
}
