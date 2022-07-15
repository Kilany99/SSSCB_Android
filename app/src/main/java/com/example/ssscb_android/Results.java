package com.example.ssscb_android;

import com.google.gson.annotations.SerializedName;

public class Results {

    @SerializedName("PostedDataId")
    public int PostedDataId;
    @SerializedName("crimeScreenshot")
    public String CrimeScreenshot;
    @SerializedName("anomalyDateTime")
    public String AnomalyDateTime;
    @SerializedName("anomalyType")
    public String anomalyType;
    @SerializedName("actionPriority")
    public String ActionPriority;
    @SerializedName("ZoneID")
    public int ZoneID;
    @SerializedName("respone")
    public String respone;



    public Results(String name,String dt1, int Zone, String AnomalyType,String priority1) {
        this.PostedDataId = 0;
        this.CrimeScreenshot = name;
        this.AnomalyDateTime = dt1;
        this.ZoneID = Zone;
        this.anomalyType = AnomalyType;
        this.ActionPriority = priority1;
        this.respone = "";

    }


}