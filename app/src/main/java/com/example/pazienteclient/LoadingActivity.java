package com.example.pazienteclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoadingActivity extends AppCompatActivity {

    List<Prescription> prescriptions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_loading);

        String uniqueId = getIntent().getStringExtra("UNIQUE_ID"); //Mi arriva

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("uniqueId", uniqueId);

        // Istanza dell'API
        ApiServicePatient apiServicePatient = RetrofitClient.getClient().create(ApiServicePatient.class);

        apiServicePatient.getAllPrescription(jsonObject).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        // Leggi il corpo della risposta come stringa
                        String responseBody = response.body().string();

                        // Usa Gson per deserializzare la stringa in una lista di Prescription
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Prescription>>() {}.getType();
                        prescriptions = gson.fromJson(responseBody, listType);

                        Intent walletIntent = new Intent(LoadingActivity.this, WalletActivity.class);
                        walletIntent.putExtra("prescriptions", (Serializable) prescriptions);
                        startActivity(walletIntent);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(LoadingActivity.this, "Errore parsing risposta", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoadingActivity.this, "WalletActivity, Errore invio dati", Toast.LENGTH_SHORT).show();
                    Log.e("WalletActivity", "Errore: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(LoadingActivity.this, "Errore di connessione", Toast.LENGTH_SHORT).show();
                Log.e("LoadingActivity", "Errore: " + t.getMessage());
            }
        });
    }
}