package com.example.covidtrackertwo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.covidtrackertwo.LOGIN;
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

public class SIGN_UP extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "MyPrefs2";
    private static final String KEY_USERNAME = "username";

    EditText eTName, eTEmail, eTPassword;
    Button tempButton, signUpButton;
    private boolean isEmailValid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        eTName = findViewById(R.id.editTextFullName);
        eTEmail = findViewById(R.id.editTextTextEmailAddress);
        eTPassword = findViewById(R.id.editTextTextPassword);

        tempButton = findViewById(R.id.temporaryButton);
        signUpButton = findViewById(R.id.signUpButton);
        signUpButton.setEnabled(false);

        tempButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SIGN_UP.this, MainActivity.class));
            }
        });

        eTEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Do nothing
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                if (eTEmail.getText().toString().matches(emailPattern) && editable.length() > 0) {
                    Toast.makeText(getApplicationContext(), "Valid email address", Toast.LENGTH_SHORT).show();
                    isEmailValid = true;
                } else {
                    //  Toast.makeText(getApplicationContext(), " ", Toast.LENGTH_SHORT).show();
                    eTEmail.setError("Invalid email");
                    isEmailValid = false;
                }

                signUpButton.setEnabled(isEmailValid);
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoSignIn();
            }
        });

        Button switchButton2 = findViewById(R.id.signIn);
        switchButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SIGN_UP.this, LOGIN.class);
                startActivity(intent);
            }
        });
    }

    public void gotoSignIn() {
        String name = eTName.getText().toString();
        String email = eTEmail.getText().toString();
        String password = eTPassword.getText().toString();

        OkHttpClient client = new OkHttpClient();
        String URL = "https://lamp.ms.wits.ac.za/home/s2380759/bhalisa.php";

        FormBody.Builder formBuilder = new FormBody.Builder()
                .add("name", name)
                .add("email", email)
                .add("password", password);

        RequestBody requestBody = formBuilder.build();
        Request request = new Request.Builder()
                .url(URL)
                .post(requestBody)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SIGN_UP.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String resp = response.body().string();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("Response", resp.trim()); // Log the response for debugging purposes

                        if (resp.trim().equals("email exists")) {
                            Toast.makeText(SIGN_UP.this, "User Already Exists", Toast.LENGTH_SHORT).show();
                        } else if (resp.trim().equals("success")) {
                            Toast.makeText(SIGN_UP.this, "Signed Up", Toast.LENGTH_SHORT).show();

                            // Store the username in SharedPreferences
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(KEY_USERNAME, name);
                            editor.apply();

                            Intent intent = new Intent(SIGN_UP.this, LOGIN.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(SIGN_UP.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
