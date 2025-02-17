package com.example.pazienteclient;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Prescription implements Serializable {
    @SerializedName("patientID")
    private String patientId;

    @SerializedName("data")
    private String date;

    @SerializedName("prescription")
    private ArrayList<Medicina> listaFarmaci;

    @SerializedName("ipfsHash")
    private String ipfsHash;

    @SerializedName("transactionID")
    private String transactionID;

    public Prescription(String patientId, String date, ArrayList<Medicina> listaFarmaci, String ipfsHash, String transactionID) {
        this.patientId = patientId;
        this.date = changeFormatDate(date);
        this.listaFarmaci = listaFarmaci;
        this.ipfsHash = ipfsHash;
        this.transactionID = transactionID;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<Medicina> getListaFarmaci() {
        return listaFarmaci;
    }

    public void setListaFarmaci(ArrayList<Medicina> listaFarmaci) {
        this.listaFarmaci = listaFarmaci;
    }

    public String getIpfsHash() {
        return ipfsHash;
    }

    public void setIpfsHash(String ipfsHash) {
        this.ipfsHash = ipfsHash;
    }

    public String changeFormatDate(String isoDate) {
        Instant instant = Instant.parse(isoDate);
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm");
        String formattedDate = zonedDateTime.format(formatter);
        return formattedDate;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
