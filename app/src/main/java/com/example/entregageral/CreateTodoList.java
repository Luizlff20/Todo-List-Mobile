package com.example.entregageral;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Locale;

public class CreateTodoList extends AppCompatActivity {

    private EditText editTextTitle;
    private EditText editTextDescription;
    private EditText editTextStartDate;
    private EditText editTextEndDate;
    private Button buttonCreateTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_todo_list);

        editTextTitle = findViewById(R.id.editTextTextCreatTitile);
        editTextDescription = findViewById(R.id.editTextTextCreatDescricao);
        editTextStartDate = findViewById(R.id.editTextDateCreatStartData);
        editTextEndDate = findViewById(R.id.editTextDateCreatEndData);
        buttonCreateTask = findViewById(R.id.buttonCreatTarefa);

        buttonCreateTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Iniciar a atividade TelaTodoList
                Intent intent = new Intent(CreateTodoList.this, TelaTodoList.class);
                startActivity(intent);

                // Enviar a nova tarefa para a API
                sendNewTaskToAPI();
            }
        });

        editTextStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(editTextStartDate);
            }
        });

        editTextEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(editTextEndDate);
            }
        });
    }

    private void sendNewTaskToAPI() {
        // Obter o token armazenado em SharedPreferences
        SharedPreferences prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
        String token = prefs.getString("token", "");

        // Obter os dados da nova tarefa dos campos de entrada
        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();
        String startDate = editTextStartDate.getText().toString();
        String endDate = editTextEndDate.getText().toString();

        // Criar um objeto JSON com os dados da nova tarefa
        JSONObject taskData = new JSONObject();
        try {
            taskData.put("name", title);
            taskData.put("description", description);
            taskData.put("startDate", startDate);
            taskData.put("endDate", endDate);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Enviar a nova tarefa para a API usando um AsyncTask
        new SendTaskToAPI(token, taskData).execute();
    }

    private class SendTaskToAPI extends AsyncTask<Void, Void, Integer> {
        private String authToken;
        private JSONObject taskData;

        public SendTaskToAPI(String authToken, JSONObject taskData) {
            this.authToken = authToken;
            this.taskData = taskData;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL("http://localhost:8080/todo");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                // Adicionar o token de autenticação aos cabeçalhos da solicitação
                urlConnection.setRequestProperty("Authorization", "Bearer " + authToken);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                // Enviar os dados da nova tarefa no corpo da solicitação
                OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                writer.write(taskData.toString());
                writer.flush();
                writer.close();
                return urlConnection.getResponseCode();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }

    }

    public void showDatePickerDialog(final EditText editText) {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String formattedDate = String.format(Locale.US, "%d-%02d-%02d", year, monthOfYear + 1, dayOfMonth);
                editText.setText(formattedDate);
            }
        };

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(CreateTodoList.this, dateSetListener, year, month, day).show();
    }
}