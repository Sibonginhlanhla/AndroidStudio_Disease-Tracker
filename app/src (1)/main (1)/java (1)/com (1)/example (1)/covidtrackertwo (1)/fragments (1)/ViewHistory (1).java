package com.example.covidtrackertwo.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.covidtrackertwo.databinding.FragmentViewHistoryBinding;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ViewHistory extends Fragment {

    private FragmentViewHistoryBinding binding;
    private String selectedDate = null;
    private int locationId = -1;

    public ViewHistory() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentViewHistoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        final MaterialDatePicker<Long> datePicker = builder.build();

        binding.queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedDate == null) {
                    datePicker.show(getChildFragmentManager(), "DATE_PICKER");
                } else {
                    Toast.makeText(requireContext(), "Query for date: " + selectedDate, Toast.LENGTH_SHORT).show();
                    selectedDate = null;
                    binding.queryButton.setText("Select Date");
                    gotoQuery();
                }
            }
        });

        datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selectedDate) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String dateString = sdf.format(new Date(selectedDate));
                ViewHistory.this.selectedDate = dateString;
                binding.queryButton.setText("QUERY");
            }
        });

        Bundle arguments = getArguments();
        if (arguments != null) {
            locationId = arguments.getInt("location_id", -1);
        }
    }

    public void gotoQuery() {
        if (selectedDate == null || locationId == -1) {
            Toast.makeText(requireContext(), "Please select a date and a location before querying.", Toast.LENGTH_SHORT).show();
            return;
        }
        OkHttpClient client = new OkHttpClient();
        String URL = "https://lamp.ms.wits.ac.za/home/s2380759/quu.php";

        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("date", selectedDate);
        formBuilder.add("location_id", String.valueOf(locationId));

        RequestBody requestBody = formBuilder.build();
        Request request = new Request.Builder().post(requestBody).url(URL).build();
        Call call = client.newCall(request);

        Callback callback = new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Failed to query history. Please try again.", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    String res = response.body().string().trim();
                    Log.d("Response", res);
                    final int number = Integer.parseInt(res);
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (number == 0) {
                                binding.textView.setText("Number of people infected who pulled up " + number);
                                binding.textView4.setText("Nothing to worry about");
                            } else if (number > 1 && number <= 10) {
                                binding.textView.setText("Number of people infected who pulled up " + number);
                                binding.textView4.setText("Low Risk");
                            } else if (number > 10) {
                                binding.textView.setText("Number of people infected who pulled up " + number);
                                binding.textView4.setText("High Risk");
                            }
                        }
                    });
                }
            }
        };
        call.enqueue(callback);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
