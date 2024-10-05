package com.example.covidtrackertwo.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.covidtrackertwo.R;
import com.example.covidtrackertwo.adapter.LocationsAdapter;
import com.example.covidtrackertwo.data.Location;
import com.example.covidtrackertwo.databinding.FragmentLocationsBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LocationsFragment extends Fragment {

    private FragmentLocationsBinding binding;
    private RecyclerView recyclerView;
    private LocationsAdapter adapter;
    private ArrayList<Location> locationsList = new ArrayList<>();

    public LocationsFragment() {
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
        binding = FragmentLocationsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*
        binding.getLocationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocations();
            }
        });*/

        getLocations();

        // Retrieve the NavController from the NavHostFragment
        NavController navController = Navigation.findNavController(view);

        // Set up the RecyclerView
        recyclerView = binding.locationsReclerview;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Create the adapter and set it to the RecyclerView
        adapter = new LocationsAdapter(locationsList, navController, new LocationsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String location_name_arg) {
                Bundle bundle = new Bundle();
                bundle.putString("location_name", location_name_arg);
                navController.navigate(R.id.action_locationsFragment_to_viewLocationFragment, bundle);
            }
        });
        recyclerView.setAdapter(adapter);


    }

    public void processJSON(String jsonString){

        if (locationsList.size() != 0) {
            locationsList = new ArrayList<>();
        }

        try {
            JSONArray jsonArray = new JSONArray(jsonString);   // convert json string into json array for indexing
            Log.e("LocationsFragment", jsonArray.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = new JSONObject(jsonArray.get(i).toString());   // covert each json object string into json objects
                //Integer id = Integer.parseInt((String) jsonObject.get("id"));       // get the id and convert it into an integer
                String locationName = (String) jsonObject.get("location_name");         // get the name as string
                Location location = new Location(0, locationName);     // create the location object
                locationsList.add(location);    // add the location object to the arraylist
            }
            recyclerView.getAdapter().notifyDataSetChanged();

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void getLocations() {
        if (getActivity() == null) {
            return;
        }

        OkHttpClient client = new OkHttpClient();
        String URL = "https://lamp.ms.wits.ac.za/home/s2380759/locations.php";

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Clear the binding object to avoid memory leaks
    }
}