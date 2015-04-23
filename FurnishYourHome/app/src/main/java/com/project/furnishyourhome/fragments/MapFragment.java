package com.project.furnishyourhome.fragments;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.project.furnishyourhome.R;
import com.project.furnishyourhome.models.CustomListItem;

import java.util.ArrayList;


public class MapFragment extends Fragment {
    private static final String TAG = MapFragment.class.getSimpleName();
    private final float TOP_VIEW = 12.0f;

    private static ArrayList<CustomListItem> storesLocations;
    private static Location myLocation;

    private GoogleMap map;
    private LocationManager locationManager;

    public static MapFragment newInstance() {
        Log.d(TAG, "newInstance()");
        return new MapFragment();
    }

    public static MapFragment newInstance (Bundle args){
        Log.d(TAG, "newInstance (Bundle args)");
        MapFragment f = new MapFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);

        if(storesLocations == null) {
            storesLocations = new ArrayList<>();
        }

        if(myLocation == null){
            myLocation = new Location("");
        }
        
        if(savedInstanceState != null) {
            Log.d(TAG, "restore form SAVED instance");
            storesLocations = savedInstanceState.getParcelableArrayList("storesLocations");
            double[] doubleArray = savedInstanceState.getDoubleArray("location");
            if(doubleArray != null) {
                Location newLocation = new Location("");
                newLocation.setLatitude(doubleArray[0]);
                newLocation.setLongitude(doubleArray[1]);
                this.myLocation = newLocation;
            }
        } else {
            Log.d(TAG, "no SAVED instance");
        }

        if(storesLocations.isEmpty() && getArguments()!=null) {
            Log.d(TAG, "load items from ARGUMENTS");
            storesLocations = getArguments().getParcelableArrayList("chosenItems");
        } else {
            Log.d(TAG, "no items from ARGUMENTS");
        }
        Log.d(TAG, "storesLocations.size(): "+storesLocations.size());

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()");
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        MapsInitializer.initialize(getActivity().getApplicationContext());
        map = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        return rootView;
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart()");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume()");
        super.onResume();
        trackMyPosition();
        showStoresOnMap();
        if(myLocation != null){
            showMeOnTheMap(myLocation);
        }
    }

    private void trackMyPosition() {
        Log.d(TAG, "trackMyPosition()");
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
               if(location != null) {
                   myLocation = location;
                   showMeOnTheMap(location);
               }
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}
            @Override
            public void onProviderEnabled(String provider) {}
            @Override
            public void onProviderDisabled(String provider) {}
        };
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10f, locationListener);
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
    }

    private void showMeOnTheMap(Location location) {
        Log.d(TAG, "showMeOnTheMap()");
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        map.clear();
        showStoresOnMap();

        MarkerOptions myLocation = new MarkerOptions();
        myLocation.position(new LatLng(lat, lng));
        myLocation.title("me");
        myLocation.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_me));
        map.addMarker(myLocation);
        map.animateCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng) , TOP_VIEW) );
        Log.d(TAG, "me: "+lat+" "+lng);
    }

    private void showStoresOnMap() {
        Log.d(TAG, "showStoresOnMap()");
        for(int i=0; i<storesLocations.size(); i++) {
            double lat = storesLocations.get(i).getStore().getLocation().getLatitude();
            double lng = storesLocations.get(i).getStore().getLocation().getLongitude();


            MarkerOptions storeLocation = new MarkerOptions();
            storeLocation.position(new LatLng(lat, lng));
            storeLocation.title(storesLocations.get(i).getStore().getName());
            storeLocation.icon(BitmapDescriptorFactory.fromBitmap(storesLocations.get(i).getStore().getLogo()));
            map.addMarker(storeLocation);
            Log.d(TAG, storesLocations.get(i).getStore().getName()+": "+lat+" "+lng);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState()");
        outState.putParcelableArrayList("storesLocations", storesLocations);
        if(myLocation != null) {
            outState.putDoubleArray("location", new double[]{
                    myLocation.getLatitude(),
                    myLocation.getLongitude()
            });
        }
        super.onSaveInstanceState(outState);
    }

}