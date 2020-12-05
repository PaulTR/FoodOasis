package com.dukehack.foodoasis;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng denver = new LatLng(39.7392, -104.9903);
        mMap.addMarker(new MarkerOptions().position(denver).title("Marker"));
        CameraPosition position = new CameraPosition.Builder()
                .zoom(13)
                .target(denver)
                .build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));
        Map<String, Object> data = new HashMap<>();
        data.put("lat", 39.7392);
        data.put("lng", -104.9903);

        FirebaseFunctions.getInstance().getHttpsCallable("getPointScore")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        Log.e("Test", "we got something!");
                        if (!task.isSuccessful()) {
                            Log.e("Test", "unsuccessful");
                            Exception e = task.getException();
                            Log.e("test", e.getMessage());

                        } else {
                            Log.e("Test", "data returned: " + task.getResult().getData().toString());
                        }

                        return null;
                    }
                });
    }
}