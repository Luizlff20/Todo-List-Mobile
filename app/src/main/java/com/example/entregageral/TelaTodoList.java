package com.example.entregageral;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TelaTodoList extends AppCompatActivity {

    private ListView listView;
    private List<CardItem> cardItemList;
    private CardListAdapter adapter;
    private Button addButtonCreateList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_todo_list);

        listView = findViewById(R.id.listView);
        cardItemList = new ArrayList<>();
        adapter = new CardListAdapter(this, cardItemList);
        listView.setAdapter(adapter);

        addButtonCreateList = findViewById(R.id.addButtonCreateList);
        addButtonCreateList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abrir a tela CreateTodoList quando o botão addButtonCreateList for clicado
                Intent intent = new Intent(TelaTodoList.this, CreateTodoList.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Obter o token armazenado em SharedPreferences
        SharedPreferences prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
        String token = prefs.getString("token", "");

        // Chamar o AsyncTask para obter os dados da API com o token de autenticação
        new GetProtectedDataRequest(token).execute();
    }

    // AsyncTask para obter os dados da API com autenticação
    private class GetProtectedDataRequest extends AsyncTask<Void, Void, String> {

        private String authToken;

        public GetProtectedDataRequest(String authToken) {
            this.authToken = authToken;
        }

        @Override
        protected String doInBackground(Void... voids) {
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL("http://localhost:8080/todo");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                // Adicionar o token de autenticação aos cabeçalhos da solicitação
                urlConnection.setRequestProperty("Authorization", "Bearer " + authToken);

                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                reader.close();
                inputStream.close();
                return stringBuilder.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                cardItemList.clear(); // Limpar a lista atual de cartões
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String id = jsonObject.getString("id"); // Obtém o id do objeto JSON
                        String title = jsonObject.getString("name");
                        String description = jsonObject.getString("description");
                        String startDate = jsonObject.getString("startDate");
                        String endDate = jsonObject.getString("endDate");
                        cardItemList.add(new CardItem(id, title, description, startDate, endDate));
                    }
                    // Notificar o adapter sobre as mudanças na lista de itens do card
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
