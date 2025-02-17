package com.example.pazienteclient;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Medicina implements Serializable {
    @SerializedName("drugName")
    private String nome;

    @SerializedName("dosage")
    private String dosaggio;

    @SerializedName("frequency")
    private String frequenza;

    public Medicina(String nome, String dosaggio, String frequenza) {
        this.nome = nome;
        this.dosaggio = dosaggio;
        this.frequenza = frequenza;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDosaggio() {
        return dosaggio;
    }

    public void setDosaggio(String dosaggio) {
        this.dosaggio = dosaggio;
    }

    public String getFrequenza() {
        return frequenza;
    }

    public void setFrequenza(String frequenza) {
        this.frequenza = frequenza;
    }
}
