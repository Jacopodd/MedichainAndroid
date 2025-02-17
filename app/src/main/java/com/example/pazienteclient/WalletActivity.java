package com.example.pazienteclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WalletActivity extends AppCompatActivity implements WalletAdapter.OnItemClickListener{

    List<Prescription> prescriptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_wallet);

        // Recupera i dati passati dall'Intent
        prescriptions = (List<Prescription>) getIntent().getSerializableExtra("prescriptions");

        if (prescriptions != null) {
            Toast.makeText(this, "Ricette trovate! :D", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Dati non ricevuti :(", Toast.LENGTH_SHORT).show();
        }

        // Configura il RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerViewWallet);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        WalletAdapter adapter = new WalletAdapter((Context) this, prescriptions, (WalletAdapter.OnItemClickListener) this); //dates
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(int position, Prescription prescription) {
        // Azione quando si clicca sulla card
        Toast.makeText(this, "Cliccato elemento " + (position + 1) + " con IPFS Hash " + prescription.getIpfsHash(), Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(WalletActivity.this, PrescriptionActivity.class);
        intent.putExtra("prescription", (Serializable) prescription);
        startActivity(intent);
    }

    public void sortPrescriptionsByDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

        // Ordina la lista usando il comparatore
        Collections.sort(prescriptions, (p1, p2) -> {
            LocalDateTime date1 = LocalDateTime.parse(p1.getDate(), formatter);
            LocalDateTime date2 = LocalDateTime.parse(p2.getDate(), formatter);
            return date1.compareTo(date2);
        });
    }
}