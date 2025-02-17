package com.example.pazienteclient;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class PrescriptionActivity extends AppCompatActivity {

    TextView tvDate;
    LinearLayout containerFarmaci;
    Button generaQrCodeBtn;
    ImageView qrCodeIV;
    Prescription prescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_prescription);

        prescription = (Prescription) getIntent().getSerializableExtra("prescription");

        tvDate = findViewById(R.id.tv_date_value);
        containerFarmaci = findViewById(R.id.container_farmaci);
        generaQrCodeBtn = findViewById(R.id.generaQrCodeBtn);
        qrCodeIV = findViewById(R.id.qrCodeIV);
        qrCodeIV.setVisibility(View.GONE);

        String isoDate = prescription.getDate();
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(isoDate);
        LocalDateTime localDateTime = zonedDateTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm");
        String formattedDate = localDateTime.format(formatter);
        tvDate.setText(formattedDate);

        // Aggiungi i farmaci dinamicamente
        for (Medicina medicina : prescription.getListaFarmaci()) {
            // Crea un nuovo TextView per ogni farmaco
            TextView tvFarmaco = new TextView(this);
            tvFarmaco.setText(medicina.getNome() + " - " + medicina.getDosaggio() + " - " + medicina.getFrequenza());
            tvFarmaco.setTextSize(14);
            tvFarmaco.setTextColor(Color.parseColor("#1f2b5b"));
            tvFarmaco.setPadding(0, 8, 0, 8);

            // Aggiungi il TextView al container
            containerFarmaci.addView(tvFarmaco);
        }

        generaQrCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Generare il codice QR
                try {
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    BitMatrix bitMatrix = new com.google.zxing.qrcode.QRCodeWriter().encode(
                            prescription.toJson(), // Dati in formato JSON
                            BarcodeFormat.QR_CODE,
                            800, // Larghezza
                            800  // Altezza
                    );
                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                    qrCodeIV.setImageBitmap(bitmap); // Mostrare il QR
                    qrCodeIV.setVisibility(View.VISIBLE);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}