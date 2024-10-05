package com.example.covidtrackertwo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.covidtrackertwo.MainActivity;
import com.example.covidtrackertwo.R;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LOGIN extends AppCompatActivity {
    private EditText eTEmail, eTPassword;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPref = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        eTEmail = findViewById(R.id.editTextTextEmailAddress);
        eTPassword = findViewById(R.id.editTextTextPassword);

        findViewById(R.id.signIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoSignIn();
            }
        });
    }

    public void gotoSignIn() {
        String email = eTEmail.getText().toString();
        String password = eTPassword.getText().toString();

        OkHttpClient client = new OkHttpClient();
        String URL = "https://lamp.ms.wits.ac.za/home/s2380759/ngena.php";

        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("email", email);
        formBuilder.add("password", password);

        RequestBody requestBody = formBuilder.build();
        Request request = new Request.Builder().post(requestBody).url(URL).build();
        Call call = client.newCall(request);

        Callback callback = new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LOGIN.this, "Invalid Credentials. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String res = response.body().string().toString();
                Log.d("Response", res.trim());
                if (res.trim().equals("success")) {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("userEmail", email);
                    editor.apply();

                    startActivity(new Intent(LOGIN.this, MainActivity.class));
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LOGIN.this, "Invalid Credentials. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        };
        call.enqueue(callback);
    }
}
