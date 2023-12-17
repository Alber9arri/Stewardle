package com.example.precticafinalstewardle;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import java.util.Set;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GameActivity extends AppCompatActivity {
    private static final String BASE_URL = "https://stewardle.com/";
    private ApiService apiService;
    private JsonArray drivers = new JsonArray();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Configura Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        // Crea una instancia de ApiService
        apiService = retrofit.create(ApiService.class);

        // Llama al método para obtener la respuesta como una cadena
        Call<String> call = apiService.getData();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String responseData = response.body();
                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObject = jsonParser.parse(responseData).getAsJsonObject();
                    Set<String> keys = jsonObject.keySet();
                    for (String key : keys) {
                        drivers.add(jsonObject.get(key));
                    }
                } else {
                    //Manejar error de respuesta
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                //Manejar error de peticion
            }
        });
    }
}