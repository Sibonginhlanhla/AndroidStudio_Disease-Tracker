package com.example.covidtrackertwo.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.covidtrackertwo.databinding.FragmentProfileBinding;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private SharedPreferences sharedPref;
    private SharedPreferences sharedPref2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout using view binding
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        Context context = requireContext();
        sharedPref = context.getSharedPreferences("MyPrefs", MODE_PRIVATE);

        // Retrieve user email from SharedPreferences
        String userEmail = sharedPref.getString("userEmail", "");

        // Set the email text
        binding.emailTextView.setText(userEmail);

        binding.button.setOnClickListener(view -> {
            String status = "negative";
            updateStatus(status, userEmail);
        });

        binding.button2.setOnClickListener(view -> {
            String status = "positive";
            updateStatus(status, userEmail);
        });

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Update status TextView based on SharedPreferences when the fragment starts
        String status = sharedPref.getString("userStatus", "");
        binding.textView3.setText("Current Status: " + status);
    }

    private void updateStatus(String status, String userEmail) {
        binding.textView3.setText("Current Status: " + status);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("userStatus", status);
        editor.apply();
        RequestBody requestBody = new FormBody.Builder()
                .add("email", userEmail)
                .add("status", status)
                .build();
        String url = status.equals("positive") ?
                "https://lamp.ms.wits.ac.za/home/s2380759/posss.php" :
                "https://lamp.ms.wits.ac.za/home/s2380759/neggg.php";
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // Handle request response here
                if (!response.isSuccessful()) {
                    // Handle the case where the response was not successful
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Clear the binding object to avoid memory leaks
    }
}
