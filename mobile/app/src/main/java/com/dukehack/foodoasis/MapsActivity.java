package com.dukehack.foodoasis;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.gson.Gson;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FirebaseAuth mAuth;
    private ContentLoadingProgressBar loadingSpinner;
    private String areaData;
    private TileOverlay overlay;
    private List<Marker> groceryMarkers = new ArrayList<Marker>();

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
                                Log.e("Test", "user found: " + mAuth.getUid());
                                FirebaseFirestore.getInstance().collection("users").document(mAuth.getUid()).collection("previous_queries").add(task.getResult().getData());
                            } else {
                                Log.e("Test", "user not found");
                            }
                        }

                        return null;
                    }
                });
    }

    private Bitmap getBitmap(int drawableRes) {
        Drawable drawable = getResources().getDrawable(drawableRes);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);

        return bitmap;
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

        //Grocery Store Markers
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("/grocery_stores");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                MarkerOptions options;
                GroceryStore store;
                for(DataSnapshot snapshot1 : snapshot.getChildren()) {
                    store = snapshot1.getValue(GroceryStore.class);
                    options = new MarkerOptions().position(new LatLng(Double.valueOf(store.point_y), Double.valueOf(store.point_x))).icon(BitmapDescriptorFactory.fromBitmap(getBitmap(R.drawable.circle))).title("Yearly Sales Volume: $" + store.sales_vol);
                    groceryMarkers.add(mMap.addMarker(options));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        /* //heat map
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("/counties");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final Map<String, Double> income = new HashMap<>();

                for(DataSnapshot snapshot1 : snapshot.getChildren()) {
                    income.put(snapshot.getKey(), snapshot1.getValue(County.class).per_capita_income/100000);
                    Log.e("Test", "income: " + income.get(snapshot.getKey()));
                }

                FirebaseFirestore.getInstance().collection("bus_stop").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<WeightedLatLng> list = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                list.add(new WeightedLatLng(new LatLng(document.getDouble("lat"), document.getDouble("lng")), income.containsKey(document.getString("county")) ? income.get(document.getString("county")): 1 ));
                            }

                    HeatmapTileProvider provider = new HeatmapTileProvider.Builder()
                            .opacity(0.6)
                            .weightedData(list)
                            .build();

                    overlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(provider));
                        }
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        */
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                mMap.addMarker(new MarkerOptions().position(latLng));
                loadingSpinner.setVisibility(View.VISIBLE);
                Log.e("Test", "lat: " + latLng.latitude + " long: " + latLng.longitude);
                getData(latLng.latitude, latLng.longitude);
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if( groceryMarkers.contains(marker)) {
                    marker.showInfoWindow();
                    return true;
                } else {
                    InfoDialog alert = InfoDialog.newInstance(areaData);
                    alert.show(getSupportFragmentManager(), "test");
                    return false;
                }
            }
        });
    }
}