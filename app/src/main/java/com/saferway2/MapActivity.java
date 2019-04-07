package com.saferway2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MapActivity";
    private GoogleMap mMap;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private Boolean locationPermissionsGranted = false;
    private static final int Location_Permission_RequestCode = 1234;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final float DEFAULT_ZOOM = 15f;
    private android.support.v7.widget.Toolbar toolbar;
    String radius = "1000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getLocationPermission();
        createMenu();
    }

    private void createMenu() {
        toolbar = findViewById(R.id.toolBar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("SaferWay");
        getSupportActionBar().setIcon(R.drawable.logo1);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mapview_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId()){
            case R.id.satellite:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;
            case R.id.terrain:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                return true;
            case R.id.hybrid:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;
            case R.id.normal:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    private void getDeviceLocation(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try{
            if(locationPermissionsGranted){
                Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()) {
                            Location currentLoc = (Location) task.getResult();

                            moveCamera(new LatLng(currentLoc.getLatitude(),
                                    currentLoc.getLongitude()), DEFAULT_ZOOM, "My Location");
                            String latitude = Double.toString(currentLoc.getLatitude());
                            String longitude = Double.toString(currentLoc.getLongitude());
                            getLocation(latitude, longitude, radius);
                        }
                        else{
                            Toast.makeText(MapActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){

        }
    }


    private void moveCamera(LatLng latLng, float zoom, String title)
    {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title(title)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        mMap.addMarker(options);
    }

    private void setUpMap(){
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapActivity.this);
    }

    private void getLocationPermission(){
        String[] permissions = {FINE_LOCATION, COARSE_LOCATION};
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationPermissionsGranted = true;
                setUpMap();
            }else{
                ActivityCompat.requestPermissions(this, permissions, Location_Permission_RequestCode);
            }
        }else{
            ActivityCompat.requestPermissions(this, permissions, Location_Permission_RequestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        locationPermissionsGranted = false;
        switch(requestCode){
            case Location_Permission_RequestCode:{
                if(grantResults.length >0){
                    for(int i = 0; i < grantResults.length; ++i)
                    {
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            locationPermissionsGranted = false;
                            return;
                        }
                    }
                    locationPermissionsGranted = true;
                    setUpMap();
                }
            }
        }
    }

    public void getLocation(String latitude, String longitude, String radius) {
        System.out.println("HEREEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
        String u = "https://data.lacity.org/resource/7fvc-faax.json?$where=within_circle(location_1,%20" +
                latitude + ",%20" + longitude + ",%20"+radius+")";
        System.out.println(u);
        Executor executor = Executors.newSingleThreadExecutor();
        GoUrl urlcrap = new GoUrl(u);
        executor.execute(urlcrap);

        while (urlcrap.getfinished() == false){
        }

        try{
            System.out.println("URLSHIT:");
            System.out.println(urlcrap.getToprint());
            String s = urlcrap.getToprint();
            String strArray[] = s.split("\"coordinates\":");
            String crimeArray[] = s.split("\"crm_cd_desc\":\"");
            String infoArray[] = s.split("\"date_occ\":\"");

            int l = strArray.length;
            ArrayList<ArrayList<String>> locations = new ArrayList<ArrayList<String>>(l);
            for (int i = 1; i < l; i++){
                String c = strArray[i].split("]")[0].substring(1);
                String longitude2 = c.split(",")[0];
                String latitude2 = c.split(",")[1];
                String crime = crimeArray[i-1].split("\"")[0];
                String info = infoArray[i-1].split("T")[0];
                ArrayList<String> location = new ArrayList<String>();
                location.add(latitude2);
                location.add(longitude2);


                if (!locations.contains(location)){
                    locations.add(location);

                System.out.println(latitude2+" " +longitude2);
                LatLng temp = new LatLng(Double.parseDouble(latitude2),
                        Double.parseDouble(longitude2));
                MarkerOptions options = new MarkerOptions()
                        .position(temp)
                        .title(crime)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.dot))
                        .snippet("Date of Occurrence: " + info);
                mMap.addMarker(options);}
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        mMap = googleMap;
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));

        if(locationPermissionsGranted){
            getDeviceLocation();
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED){
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
        }
    }

}
