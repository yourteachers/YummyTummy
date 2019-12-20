package com.example.yummytummy_v01;

import java.sql.Date;
import java.sql.Timestamp;

public class BottleHistory {
    private
        int UID;
        int waterAmount;
        int scoops;
        String sonName;
        Timestamp preperationDate;

    public BottleHistory(int ID,int water,int scoops,String name,Timestamp preperationDate){
        this.UID=ID;
        this.waterAmount=water;
        this.scoops=scoops;
        this.sonName=name;
        this.preperationDate=preperationDate;
    }
    public int getUID() {
        return UID;
    }

    public int getWaterAmount() {
        return waterAmount;
    }

    public int getScoops() {
        return scoops;
    }

    public String getSonName() {
        return sonName;
    }

    public Timestamp getPreperationDate() {
        return preperationDate;
    }
}
