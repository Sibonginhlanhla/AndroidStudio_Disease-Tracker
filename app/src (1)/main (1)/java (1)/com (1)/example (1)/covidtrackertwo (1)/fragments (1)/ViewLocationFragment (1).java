package com.example.covidtrackertwo.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.covidtrackertwo.data.CheckIn;
import com.example.covidtrackertwo.data.Location;
import com.example.covidtrackertwo.databinding.FragmentViewLocationBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ViewLocationFragment extends Fragment {

    private FragmentViewLocationBinding binding;
    private String locationName;
    private SharedPreferences sharedPref;
    private int infectedCount;
    private String today;
    private static int totalInfectedCount = 0; // Total infected count across all users
    private static String lastCheckedDate = ""; // Last checked date for resetting counts
    private boolean checkedInToday = false; // Flag to track if the user has already checked in today

    private ArrayList<CheckIn> checkInList = new ArrayList<>();

    public ViewLocationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // infectedCount = 0;
        Bundle arguments = getArguments();
        if (arguments != null) {
            // Retrieve the argument values
            locationName = arguments.getString("location_name", "MSL");
            infectedCount = arguments.getInt("infected_count", 0);
        }

        // Get today's date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        today = dateFormat.format(new Date());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentViewLocationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toast.makeText(getContext(), locationName, Toast.LENGTH_SHORT).show();

        getCheckIns();

        // Initialize SharedPreferences
        sharedPref = requireActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);

        String userEmail = sharedPref.getString("userEmail", "");

        // Check if the date has changed since the last check
        if (!today.equals(lastCheckedDate)) {
            resetInfectedCount(); // Reset counts if the date has changed
            checkedInToday = false; // Reset the checked-in flag
        }

        binding.checkInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check_in(userEmail);
            }
        });

    }


    public void processJSON(String jsonString){

        try {
            JSONArray jsonArray = new JSONArray(jsonString);   // convert json string into json array for indexing
            binding.locationNameTextView.setText(locationName);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = new JSONObject(jsonArray.get(i).toString());   // covert each json object string into json objects
                //Integer id = Integer.parseInt((String) jsonObject.get("id"));       // get the id and convert it into an integer
                String locationNameTemp = (String) jsonObject.get("location_name");         // get the name as string
                Integer positiveCount = (Integer) jsonObject.get("positive_count");
                Integer negativeCount = (Integer) jsonObject.get("negative_count");
                String proportionPercentage = (String) jsonObject.get("proportion_percentage");

                if (locationNameTemp.equals(locationName)) {
                    binding.positiveTextview.setText(positiveCount.toString());
                    binding.negativeTextview.setText(negativeCount.toString());
                    binding.proportionTextview.setText(proportionPercentage.toString());
                }

                CheckIn checkInElement = new CheckIn(locationName,  positiveCount, negativeCount, proportionPercentage);     // create the location object

            }

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void getCheckIns() {
        if (getActivity() == null) {
            return;
        }

        OkHttpClient client = new OkHttpClient();
        String URL = "https://lamp.ms.wits.ac.za/home/s2380759/positives.php";

        Request req = new Request.Builder().url(URL).build();
        Call call = client.newCall(req);

        Callback callback = new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getActivity(), "Failed to fetch locations. Please try again.", Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String res = response.body().string().toString();

                // Handle the response here

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        // Update UI or perform other operations with the response


                        processJSON(res);
                        //binding.testTextview.setText(res);
                        //Toast.makeText(getContext(), res, Toast.LENGTH_SHORT).show();
                    });
                }
            }
        };

        call.enqueue(callback);
    }

    private void check_in(String userEmail) {
        if (checkedInToday) {
            Toast.makeText(requireContext(), "You have already checked in today at this location.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Retrieve the status from SharedPreferences
        String userStatus = sharedPref.getString("userStatus", "");

        RequestBody requestBody = new FormBody.Builder()
                .add("email", userEmail)
                .add("status", userStatus)
                .add("location", locationName)
                .add("date", today)
                .build();

        // Create the request
        Request request = new Request.Builder()
                .url("https://lamp.ms.wits.ac.za/home/s2380759/check2.php")
                .post(requestBody)
                .build();

        // Create the OkHttpClient
        OkHttpClient client = new OkHttpClient();

        // Execute the request asynchronously
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(requireContext(), "Failed to check in. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // Handle request response here
                if (isAdded() && getActivity() != null) { // Check if fragment is still attached and activity is available
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string().trim();

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (responseBody.equals("success")) {
                                    getCheckIns();
                                    Toast.makeText(requireContext(), "Check-in successful", Toast.LENGTH_LONG).show();
                                    if (userStatus.equalsIgnoreCase("positive")) {
                                        getCheckIns(); // Increment infected count
                                    }
                                    checkedInToday = true; // Set the checked-in flag
                                } else if (responseBody.startsWith("fail")) {
                                    String errorMessage = responseBody.substring(5).trim();
                                    Toast.makeText(requireContext(), "Check-in failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(requireContext(), "Failed to check in. Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(requireContext(), "Failed to check in. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Clear the binding object to avoid memory leaks
    }

    private void resetInfectedCount() {
        infectedCount = 0;
        totalInfectedCount = 0;
        lastCheckedDate = today;
    }

}
