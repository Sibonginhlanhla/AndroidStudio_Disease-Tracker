package com.example.covidtrackertwo.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.covidtrackertwo.R;
import com.example.covidtrackertwo.adapter.HistoryAdapter;
import com.example.covidtrackertwo.data.History;
import com.example.covidtrackertwo.data.Location;
import com.example.covidtrackertwo.databinding.FragmentHistoryBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HistoryFragment extends Fragment {

    private FragmentHistoryBinding binding;

    private HistoryAdapter adapter;
    private RecyclerView recyclerView;
    private ArrayList<History> historyList = new ArrayList<>();

    public HistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout using view binding
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        // Set up the RecyclerView
        binding.historyRecylerview.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new HistoryAdapter(historyList);
        getHistory();


        binding.historyRecylerview.setAdapter(adapter);
    }

    public void processJSON(String jsonString) {
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = new JSONObject(jsonArray.get(i).toString());
                String locationName = (String) jsonObject.get("location_name");
                String date = (String) jsonObject.get("check_in_date");
                History history = new History(locationName, date);
                historyList.add(history);
            }
//            // Construct History data here with the fetched locations data
//            for (Location location : historyList) {
//                itemList.add(new History("email@wits.com", location.getLocationName(), getCurrentDate(), "negative"));
//            }

            binding.historyRecylerview.getAdapter().notifyDataSetChanged();

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void getHistory() {
        if (getActivity() == null) {
            return;
        }

        OkHttpClient client = new OkHttpClient();
        String URL = "https://lamp.ms.wits.ac.za/home/s2380759/his.php";

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

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        processJSON(res);
                    });
                }
            }
        };

        call.enqueue(callback);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Clear the binding object to avoid memory leaks
    }

    private String getCurrentDate() {
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return dateFormat.format(today);
    }
}
