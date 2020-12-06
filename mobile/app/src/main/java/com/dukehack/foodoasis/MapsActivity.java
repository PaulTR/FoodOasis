package com.dukehack.foodoasis;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FirebaseAuth mAuth;
    private ContentLoadingProgressBar loadingSpinner;
    private String areaData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_maps);
        loadingSpinner = (ContentLoadingProgressBar) findViewById(R.id.progress);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mAuth.signInWithEmailAndPassword("ptruiz.demo@gmail.com", "asdf1234")
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MapsActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void getData(double lat, double lng) {
        Map<String, Object> data = new HashMap<>();
        data.put("lat", lat);
        data.put("lng", lng);

        FirebaseFunctions.getInstance().getHttpsCallable("getPointScore")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        if (!task.isSuccessful()) {
                            Log.e("Test", "unsuccessful");
                            Exception e = task.getException();
                            Log.e("test", e.getMessage());

                        } else {
                            loadingSpinner.setVisibility(View.GONE);
                            Log.e("Test", "data returned: " + task.getResult().getData().toString());
                            Gson g = new Gson();
                            areaData = g.toJson(task.getResult().getData());
                            if( mAuth.getUid() != null ) {
                                FirebaseFirestore.getInstance().collection("users").document(mAuth.getUid()).collection("previous_queries").add(areaData);
                            }
                        }

                        return null;
                    }
                });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        LatLng denver = new LatLng(39.7392, -104.9903);
        CameraPosition position = new CameraPosition.Builder()
                .zoom(11)
                .target(denver)
                .build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng));
                loadingSpinner.setVisibility(View.VISIBLE);
                getData(latLng.latitude, latLng.longitude);
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                InfoDialog alert = InfoDialog.newInstance(areaData);
                alert.show(getSupportFragmentManager(), "test");
                return false;
            }
        });
    }
}