package com.example.entregageral;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button loginButton;
    private Button cadastroButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        loginButton = findViewById(R.id.loginButton);
        cadastroButton = findViewById(R.id.cadastroButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        cadastroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abrir a tela de cadastro quando o botão "Realizar Cadastro" for clicado
                Intent intent = new Intent(MainActivity.this, Cadastro.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser() {
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();

        JSONObject postData = new JSONObject();
        try {
            postData.put("email", email);
            postData.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new SendPostRequest().execute(postData.toString());
    }

    private class SendPostRequest extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL("http://localhost:8080/user/login");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");

                BufferedOutputStream outputStream = new BufferedOutputStream(urlConnection.getOutputStream());
                outputStream.write(params[0].getBytes());
                outputStream.flush();
                outputStream.close();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
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
                Log.d("Response", result);
                try {
                    JSONObject jsonResponse = new JSONObject(result);
                    String message = jsonResponse.getString("message");
                    if (message.contains("Bearer")) {
                        String[] parts = message.split("Bearer ");
                        if (parts.length > 1) {
                            String token = parts[1];
                            // Armazena o token em SharedPreferences
                            SharedPreferences.Editor editor = getSharedPreferences("myPrefs", MODE_PRIVATE).edit();
                            editor.putString("token", token);
                            editor.apply();
                            Toast.makeText(MainActivity.this, "Autenticação bem-sucedida", Toast.LENGTH_SHORT).show();
                            // Verifica se o login foi bem-sucedido antes de redirecionar para TelaTodoList
                            Intent intent = new Intent(MainActivity.this, TelaTodoList.class);
                            startActivity(intent);
                            finish(); // Finaliza a atividade atual para impedir que o usuário volte à tela de login
                        } else {
                            Toast.makeText(MainActivity.this, "Token não encontrado na resposta", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Autenticação falhou: Bearer não encontrado", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
