package com.example.pazienteclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.time.LocalDateTime;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoadingValidazioneActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_loading_validazione);

        String prescriptionString = getIntent().getStringExtra("prescriptionString");
        boolean resultCheck = getIntent().getBooleanExtra("resultCheck", false);


        if (prescriptionString != null && prescriptionString.length() > 0) {
            JsonObject prescriptionJson = new com.google.gson.JsonParser().parse(prescriptionString).getAsJsonObject();

            prescriptionJson.addProperty("isValid", resultCheck);
            LocalDateTime now = LocalDateTime.now();
            prescriptionJson.addProperty("dateValidation", String.valueOf(now));


            // Istanza dell'API
            ApiServicePatient apiServicePatient = RetrofitClient.getClient().create(ApiServicePatient.class);

            apiServicePatient.validationPrescription(prescriptionJson).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            // Leggi il corpo della risposta come stringa
                            String responseBody = response.body().string();

                            Intent intent = new Intent(LoadingValidazioneActivity.this, FarmacistaActivity.class);
                            intent.putExtra("messageValidation", responseBody);
                            intent.putExtra("code", response.code());
                            startActivity(intent);

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(LoadingValidazioneActivity.this, "Errore parsing risposta", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoadingValidazioneActivity.this, FarmacistaActivity.class);
                            intent.putExtra("messageValidation", "Errore generico!");
                            intent.putExtra("code", response.code());
                            startActivity(intent);
                        }



                    } else {
                        Toast.makeText(LoadingValidazioneActivity.this, "FarmacistaActivity, Errore invio dati", Toast.LENGTH_SHORT).show();
                        Log.e("LoadingValidazioneActivity", "Errore: " + response.code());
                        Intent intent = new Intent(LoadingValidazioneActivity.this, FarmacistaActivity.class);
                        intent.putExtra("messageValidation", "Errore invio dati!");
                        intent.putExtra("code", response.code());
                        startActivity(intent);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(LoadingValidazioneActivity.this, "Errore di connessione", Toast.LENGTH_SHORT).show();
                    Log.e("LoadingValidazioneActivity", "Errore: " + t.getMessage());
                    Intent intent = new Intent(LoadingValidazioneActivity.this, FarmacistaActivity.class);
                    intent.putExtra("messageValidation", "Erorre di connessione");
                    intent.putExtra("code", 500);
                    startActivity(intent);
                }
            });
        } else {
            Toast.makeText(this, "Errore, riprovare più tardi.", Toast.LENGTH_SHORT).show();
        }


    }
}