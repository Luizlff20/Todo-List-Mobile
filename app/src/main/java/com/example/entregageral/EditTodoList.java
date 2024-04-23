package com.example.entregageral;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import android.content.SharedPreferences;

public class EditTodoList extends AppCompatActivity {

    private EditText editTextTitle;
    private EditText editTextDescription;
    private EditText editTextStartDate;
    private EditText editTextEndDate;
    private Button buttonEditTarefa;
    private Button buttonApagarCard;

    private String cardId;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_todo_list);

        editTextTitle = findViewById(R.id.editTextTextCreatTitile);
        editTextDescription = findViewById(R.id.editTextTextCreatDescricao);
        editTextStartDate = findViewById(R.id.editTextDateCreatStartData);
        editTextEndDate = findViewById(R.id.editTextDateCreatEndData);
        buttonEditTarefa = findViewById(R.id.buttonEditTarefa);
        buttonApagarCard = findViewById(R.id.buttonApagarCard);

        Intent intent = getIntent();
        if (intent != null) {
            cardId = intent.getStringExtra("card_id");
            String title = intent.getStringExtra("title");
            String description = intent.getStringExtra("description");
            String startDate = intent.getStringExtra("start_date");
            String endDate = intent.getStringExtra("end_date");

            editTextTitle.setText(title);
            editTextDescription.setText(description);
            editTextStartDate.setText(startDate);
            editTextEndDate.setText(endDate);
        }

        SharedPreferences prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
        token = prefs.getString("token", "");

        buttonEditTarefa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newTitle = editTextTitle.getText().toString();
                String newDescription = editTextDescription.getText().toString();
                String newStartDate = editTextStartDate.getText().toString();
                String newEndDate = editTextEndDate.getText().toString();

                // Realiza a requisição PUT com os dados atualizados
                new UpdateTask().execute(newTitle, newDescription, newStartDate, newEndDate);
            }
        });

        buttonApagarCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abrir a tela DeleteCard quando o botão buttonApagarCard for clicado
                Intent intent = new Intent(EditTodoList.this, DeleteCard.class);
                intent.putExtra("card_id", cardId); // Passar o ID do card para a tela DeleteCard
                startActivity(intent);
            }
        });
    }

    private class UpdateTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String newTitle = params[0];
            String newDescription = params[1];
            String newStartDate = params[2];
            String newEndDate = params[3];

            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL("http://localhost:8080/todo");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("PUT");
                urlConnection.setRequestProperty("Authorization", "Bearer " + token);
                urlConnection.setRequestProperty("Content-Type", "application/json");

                // Criar o JSON com os dados atualizados
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", cardId); // Incluindo o ID
                jsonObject.put("name", newTitle);
                jsonObject.put("description", newDescription);
                jsonObject.put("startDate", newStartDate);
                jsonObject.put("endDate", newEndDate);

                // Escrever o JSON no corpo da requisição
                OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                writer.write(jsonObject.toString());
                writer.flush();

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
                Toast.makeText(EditTodoList.this, "Tarefa atualizada com sucesso!", Toast.LENGTH_SHORT).show();
                // Encerre a atividade e volte para a tela anterior
                finish();
            } else {
                Toast.makeText(EditTodoList.this, "Falha ao atualizar a tarefa. Por favor, tente novamente.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
