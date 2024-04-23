package com.example.entregageral;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DeleteCard extends AppCompatActivity {

    private String cardId;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_card);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            cardId = extras.getString("card_id");
        }

        // Obter o token armazenado em SharedPreferences
        SharedPreferences prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
        token = prefs.getString("token", "");

        Button buttonQueroApagar = findViewById(R.id.buttonQueroApagar);
        buttonQueroApagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Executar a AsyncTask para deletar o cartão na API
                new DeleteTask().execute();
                // Retornar para a TelaTodoList
                Intent intent = new Intent(DeleteCard.this, TelaTodoList.class);
                startActivity(intent);
            }
        });

        Button buttonNaoApagar = findViewById(R.id.buttonNaoApagar);
        buttonNaoApagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retornar para a TelaTodoList
                Intent intent = new Intent(DeleteCard.this, TelaTodoList.class);
                startActivity(intent);
            }
        });
    }

    private class DeleteTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            HttpURLConnection urlConnection = null;
            try {
                // Construir a URL para o DELETE
                URL url = new URL("http://localhost:8080/todo/" + cardId);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("DELETE");
                urlConnection.setRequestProperty("Authorization", "Bearer " + token);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setDoOutput(true);

                // Construir o objeto JSON com o ID do cartão
                JSONObject jsonParams = new JSONObject();
                jsonParams.put("id", cardId);

                // Escrever o objeto JSON no corpo da solicitação
                OutputStream outputStream = urlConnection.getOutputStream();
                outputStream.write(jsonParams.toString().getBytes("UTF-8"));
                outputStream.close();

                // Verificar se a resposta foi bem-sucedida (código 200)
                int responseCode = urlConnection.getResponseCode();
                return responseCode == HttpURLConnection.HTTP_OK;
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            if (success) {

            } else {

            }
        }
    }
}
