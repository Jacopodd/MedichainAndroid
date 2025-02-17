package com.example.pazienteclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText usernameEt, passwordEt;
    Button loginBtn;
    TextView loginTypeTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);

        int scelta = getIntent().getIntExtra("scelta", 0);

        usernameEt = findViewById(R.id.username_field);
        passwordEt = findViewById(R.id.password_field);
        loginBtn = findViewById(R.id.login_button);
        loginTypeTV = findViewById(R.id.login_type);

        if (scelta == 1) loginTypeTV.setText("Paziente");
        else if (scelta == 2) loginTypeTV.setText("Farmacista");
        else loginTypeTV.setText("ERRORE");

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usernameString = usernameEt.getText().toString();
                String passwordString = passwordEt.getText().toString();

                if (usernameString.length() == 0 || passwordString.length() == 0)
                    Toast.makeText(LoginActivity.this, "Inserire tutti i paramentri prima di continuare", Toast.LENGTH_SHORT).show();
                else {
                    // Istanza dell'API
                    ApiServicePatient apiServicePatient = RetrofitClient.getClient().create(ApiServicePatient.class);

                    // Dati da inviare
                    LoginClass data = new LoginClass(usernameString, passwordString);

                    if (scelta == 1) {
                        // Chiamata POST
                        apiServicePatient.sendData(data).enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    try {
                                        // Legge il body della risposta
                                        String responseBody = response.body().string();
                                        // Parla il JSON per ottenere il valore di "message"
                                        JSONObject jsonResponse = new JSONObject(responseBody);
                                        String uniqueId = jsonResponse.getString("message");

                                        // Log e operazioni con il uniqueId
                                        Log.d("LoginActivity", "Unique ID: " + uniqueId);
                                        Toast.makeText(LoginActivity.this, "Login riuscito! ID: " + uniqueId, Toast.LENGTH_SHORT).show();

                                        // Prosegui alla prossima attività
                                        Intent pazienteIntent = new Intent(LoginActivity.this, LoadingActivity.class);
                                        pazienteIntent.putExtra("UNIQUE_ID", uniqueId);
                                        startActivity(pazienteIntent);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Toast.makeText(LoginActivity.this, "Errore parsing risposta", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(LoginActivity.this, "Errore invio dati", Toast.LENGTH_SHORT).show();
                                    Log.e("LoginActivity", "Errore: " + response.code());
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Toast.makeText(LoginActivity.this, "Errore di connessione", Toast.LENGTH_SHORT).show();
                                Log.e("LoginActivity", "Errore: " + t.getMessage());
                            }
                        });
                    } else if (scelta == 2) {
                        // Chiamata POST
                        apiServicePatient.loginFarmacista(data).enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    try {
                                        // Legge il body della risposta
                                        String responseBody = response.body().string();

                                        // Parla il JSON per ottenere il valore di "message"
                                        JSONObject jsonResponse = new JSONObject(responseBody);
                                        String uniqueId = jsonResponse.getString("message");

                                        // Log e operazioni con il uniqueId
                                        Log.d("LoginActivity", "Unique ID: " + uniqueId);
                                        Toast.makeText(LoginActivity.this, "Login riuscito! ID: " + uniqueId, Toast.LENGTH_SHORT).show();

                                        // Prosegui alla prossima attività
                                        Intent farmacistaIntent = new Intent(LoginActivity.this, FarmacistaActivity.class);
                                        farmacistaIntent.putExtra("UNIQUE_ID", uniqueId);
                                        startActivity(farmacistaIntent);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Toast.makeText(LoginActivity.this, "Errore parsing risposta", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(LoginActivity.this, "Errore invio dati", Toast.LENGTH_SHORT).show();
                                    Log.e("LoginActivity", "Errore: " + response.code());
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Toast.makeText(LoginActivity.this, "Errore di connessione", Toast.LENGTH_SHORT).show();
                                Log.e("LoginActivity", "Errore: " + t.getMessage());
                            }
                        });
                    }

                }
            }
        });
    }


}