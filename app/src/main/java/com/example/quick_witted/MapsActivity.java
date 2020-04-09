package com.example.quick_witted;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.internal.ConnectionCallbacks;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    FusedLocationProviderClient fusedLocationClient;
    FloatingActionButton mfab;

    int REQUEST_PERMISSION = 1000;
    double lat = 11.9144511, lon = 79.636104;
    float zoom_val = 16.0f;
    String locationProvider = LocationManager.NETWORK_PROVIDER;
    int PROXIMITY_RADIUS = 10000;

    LocationManager mlocationManager;
    LocationListener mlocationListener;

    LatLng curr_loc;
    Location mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.



//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//
//        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
//            @Override
//            public void onSuccess(Location location) {
//                if (location != null) {
//                    lat = location.getLatitude();
//                    lon = location.getLongitude();
//                    Log.d("latitude oncreate", String.valueOf(lat));
//                    Log.d("longitude oncreate", String.valueOf(lon));
//                }
//            }
//        });

        mfab = findViewById(R.id.fab);

        mfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(getApplicationContext(), req_rep.class);
                startActivity(mIntent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        getCurrentLocation();
    }

    private void getCurrentLocation() {
        mlocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mlocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if(location != null){
                    mLocation = location;
                    //lat = mLocation.getLatitude();
                    //lon = mLocation.getLongitude();
                    //curr_loc = new LatLng(lat, lon);
                    Log.d("latitude", String.valueOf(mLocation.getLatitude()));
                    Log.d("longitude", String.valueOf(mLocation.getLongitude()));

                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    supportMapFragment.getMapAsync(MapsActivity.this);
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION);
            return;
        }
        mlocationManager.requestLocationUpdates(locationProvider, 1000, 1000, mlocationListener);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Log.d("latitude onready", String.valueOf(mLocation.getLatitude()));
        Log.d("longitude onready", String.valueOf(mLocation.getLongitude()));

        //curr_loc = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
        curr_loc = new LatLng(lat, lon);


//        Log.d("latitude latlng", String.valueOf(curr_loc.latitude));
//        Log.d("longitude latlng", String.valueOf(curr_loc.longitude));

        mMap.addMarker(new MarkerOptions().position(curr_loc).title("my_location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

        CameraPosition cp = new CameraPosition.Builder().target(curr_loc).zoom(zoom_val).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp));

        String url = getUrl(mLocation.getLatitude(), mLocation.getLongitude(), "hospital");
        Log.d("url", url);
        Object[] DataTransfer = new Object[2];
        DataTransfer[0] = mMap;
        DataTransfer[1] = url;
//        Log.d("onClick", url);
        GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
        getNearbyPlacesData.execute(DataTransfer);

    }

    private String getUrl(double latitude, double longitude, String nearby){
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlacesUrl.append("&type=" + nearby);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + "AIzaSyBMbl7EUDZlXZh6fL7Yi-FHuwe6pvuXChg");
        Log.d("getUrl", googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());
    }

}
