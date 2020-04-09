package com.example.quick_witted;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;

class GetNearbyPlacesData extends AsyncTask<Object, String, String> {

    String googlePlacesData;
    GoogleMap mMap;
    String url;
    
    @Override
    protected String doInBackground(Object... objects) {
        try{
            mMap = (GoogleMap) objects[0];
            url = (String) objects[1];
            DownloadUrl downloadUrl = new DownloadUrl();
            googlePlacesData = downloadUrl.readUrl(url);
            Log.d("GooglePlacesReadTask", "doInBackground Exit");
        }catch (Exception e){
            Log.d("doinback", e.toString());
        }
        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d("GooglePlacesReadTask", "onPostExecute Entered");
        List<HashMap<String, String>> nearbyPlacesList = null;
        DataParser dataParser = new DataParser();
        nearbyPlacesList =  dataParser.parse(result);
        ShowNearbyPlaces(nearbyPlacesList);
        Log.d("GooglePlacesReadTask", "onPostExecute Exit");
        super.onPreExecute();
    }

    private void ShowNearbyPlaces(List<HashMap<String, String>> nearbyPlacesList) {
        for (int i = 0; i < nearbyPlacesList.size(); i++) {
            Log.d("onPostExecute","Entered into showing locations");
            HashMap<String, String> googlePlace = nearbyPlacesList.get(i);
            double lat = Double.parseDouble(googlePlace.get("lat"));
            double lng = Double.parseDouble(googlePlace.get("lng"));
            String placeName = googlePlace.get("place_name");
            LatLng latLng = new LatLng(lat, lng);
            mMap.addMarker(new MarkerOptions().position(latLng).title(placeName).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        }
    }
}
