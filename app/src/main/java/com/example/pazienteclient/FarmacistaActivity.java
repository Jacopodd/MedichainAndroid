package com.example.pazienteclient;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FarmacistaActivity extends AppCompatActivity {

    Button scansionaBtn;
    TextView rispostaFarmacistaTV;

    JsonObject jsonObject = new JsonObject();
    String prescriptionScanned;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_farmacista);

        String uniqueId = getIntent().getStringExtra("UNIQUE_ID");
        jsonObject.addProperty("uniqueId", uniqueId);

        scansionaBtn = findViewById(R.id.apri_scansioneBtn);
        rispostaFarmacistaTV = findViewById(R.id.rispostaFarmacistaTV);

        getValidationResponse();

        Activity a = this;
        scansionaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apriScansione();
            }
        });
    }

    public void apriScansione() {
        IntentIntegrator integrator = new IntentIntegrator(this);

        // Configura alcune opzioni
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Scansiona un QR Code");
        integrator.setCameraId(0); // 0 per la fotocamera posteriore
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Ottieni il risultato della scansione
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);


        if (result != null) {
            if (result.getContents() != null) {
                prescriptionScanned = result.getContents();
                JsonObject prescriptionJson = new com.google.gson.JsonParser().parse(prescriptionScanned).getAsJsonObject();
                String ipfsHash = prescriptionJson.get("ipfsHash").getAsString();

                JsonObject ipfsHashScanned = new JsonObject();
                ipfsHashScanned.addProperty("ipfsHashScanned", ipfsHash);


                // Istanza dell'API
                ApiServicePatient apiServicePatient = RetrofitClient.getClient().create(ApiServicePatient.class);

                apiServicePatient.getPrescriptionByIPFSHash(ipfsHashScanned).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            try {
                                // Leggi il corpo della risposta come stringa
                                String responseBody = response.body().string();
                                rispostaFarmacistaTV.setText(responseBody);


                                boolean isEqual = checkPrescription(prescriptionJson, responseBody);

                                String prescriptionString = prescriptionJson.toString();
                                Intent validationIntent = new Intent(FarmacistaActivity.this, LoadingValidazioneActivity.class);
                                validationIntent.putExtra("prescriptionString", prescriptionString);
                                validationIntent.putExtra("resultCheck", isEqual);
                                startActivity(validationIntent);


                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(FarmacistaActivity.this, "Errore parsing risposta", Toast.LENGTH_SHORT).show();
                            }



                        } else {
                            Toast.makeText(FarmacistaActivity.this, "FarmacistaActivity, Errore invio dati", Toast.LENGTH_SHORT).show();
                            Log.e("FarmacistaActivity", "Errore: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(FarmacistaActivity.this, "Errore di connessione", Toast.LENGTH_SHORT).show();
                        Log.e("FarmacistaActivity", "Errore: " + t.getMessage());
                    }
                });



            } else {
                Toast.makeText(this, "Scansione annullata", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    public boolean checkPrescription(JsonObject prescriptionScannedJson, String prescriptionRetrieve) {
        String ipfsHashScanned = prescriptionScannedJson.get("ipfsHash").getAsString();
        String patientIdScanned = prescriptionScannedJson.get("patientID").getAsString();
        if (ipfsHashScanned.length() > 0 && patientIdScanned.length() > 0 && prescriptionRetrieve.length() > 0) {

            JsonObject prescriprionRetrieveJson = new com.google.gson.JsonParser().parse(prescriptionRetrieve).getAsJsonObject();

            if (!prescriprionRetrieveJson.entrySet().isEmpty()) {
                String patientIdRetrieve = prescriprionRetrieveJson.get("patientID").getAsString();

                if (patientIdRetrieve.length() > 0) {

                    if (patientIdRetrieve.equals(patientIdScanned)) {
                        String dataScanned = prescriptionScannedJson.get("data").getAsString();
                        String dataRetrieve = prescriprionRetrieveJson.get("data").getAsString();

                        if ((dataScanned.length() > 0 && dataRetrieve.length() > 0) && (dataScanned.equals(dataRetrieve))) {
                            boolean isEqualDrugs = compareDrugs(prescriptionScannedJson, prescriprionRetrieveJson);
                            if (isEqualDrugs) return false;
                            else {
                                insertMessage("Ricetta contraffatta! Medicine diverse!");
                                return true;
                            }
                        } else {
                            insertMessage("Ricetta contraffatta! Date errate!");
                            return true;
                        }
                    } else {
                        insertMessage("Ricetta contraffatta! Errore Hash o ID Paziente");
                        return true;
                    }
                } else {
                    insertMessage("Ricetta contraffatta! Hash o ID Paziente errati (len)");
                    return true;
                }
            } else {
                insertMessage("Ricetta contraffatta! Non è stata trovata nessuna ricetta con quell'ID");
                return true;
            }
        } else {
            insertMessage("Errore generico!");
            return true;
        }
    }

    public static boolean compareDrugs(JsonObject obj1, JsonObject obj2) {
        // Controlla se entrambe le chiavi "prescription" esistono
        if (!obj1.has("prescription") || !obj2.has("prescription")) {
            return false; // Una delle chiavi non è presente
        }

        // Estrai i due array "prescription"
        JsonArray prescription1 = obj1.getAsJsonArray("prescription");
        JsonArray prescription2 = obj2.getAsJsonArray("prescription");

        // Controlla se gli array hanno la stessa lunghezza
        if (prescription1.size() != prescription2.size()) {
            return false; // Lunghezza diversa
        }

        // Confronta ogni elemento degli array
        for (int i = 0; i < prescription1.size(); i++) {
            JsonElement element1 = prescription1.get(i);
            JsonElement element2 = prescription2.get(i);

            // Confronta gli oggetti JSON
            if (!element1.equals(element2)) {
                return false; // Elementi diversi
            }
        }

        return true; // Tutti gli elementi sono uguali
    }

    public void insertMessage(String message) {
        String text = rispostaFarmacistaTV.getText().toString();
        text += "\n\n" + message;

        rispostaFarmacistaTV.setText(text);
    }

    public void getValidationResponse() {
        String message = getIntent().getStringExtra("messageValidation");
        int code = getIntent().getIntExtra("code", 0);

        if (message != null && message.length() > 0 && code != 0) {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(message);
                String messageString = jsonObject.getString("message");
                if (code == 200) rispostaFarmacistaTV.setTextColor(Color.GREEN);
                else rispostaFarmacistaTV.setTextColor(Color.RED);

                rispostaFarmacistaTV.setText(messageString);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }
    }
}