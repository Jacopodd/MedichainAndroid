package com.example.pazienteclient;

import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

// Definizione dell'interfaccia API
public interface ApiServicePatient {
    @POST("/api/loginPaziente") // Specifica l'endpoint (lo stesso configurato in Express)
    Call<ResponseBody> sendData(@Body LoginClass data);

    @POST("/api/loginFarmacista") // Specifica l'endpoint (lo stesso configurato in Express)
    Call<ResponseBody> loginFarmacista(@Body LoginClass data);

    @POST("/api/getPrescriptions") // Endpoint del nuovo router.post
    Call<ResponseBody> getAllPrescription(@Body JsonObject data);

    @POST("/api/getPrescriptionByIPFSHash") // Endpoint del nuovo router.post
    Call<ResponseBody> getPrescriptionByIPFSHash(@Body JsonObject data);

    @POST("/api/validationPrescription") // Endpoint del nuovo router.post
    Call<ResponseBody> validationPrescription(@Body JsonObject data);

}
