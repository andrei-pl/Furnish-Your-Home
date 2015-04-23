package com.project.furnishyourhome.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.furnishyourhome.R;


public class ContainerMapFragment extends Fragment {
    private static final String TAG = ContainerMapFragment.class.getSimpleName();

    private LocationManager locationManager;

    public ContainerMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        Log.d(TAG, "setUserVisibleHint()");
        super.setUserVisibleHint(isVisibleToUser);
        Log.d(TAG, "isVisibleToUser: "+isVisibleToUser);
        if (isVisibleToUser) {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                Log.d(TAG, "GPS is ON");
            } else {
                Log.d(TAG, "GPS is OFF");
                showGPSDisabledAlertToUser();
            }
        }
    }

    private void showGPSDisabledAlertToUser(){
        Log.d(TAG, "showGPSDisabledAlertToUser()");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.Base_Theme_AppCompat_Dialog));
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Enable GPS", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){
                Log.d(TAG, "positiveButtonClicked");
                Intent callGPSSettingIntent = new Intent(
                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(callGPSSettingIntent);
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){
                Log.d(TAG, "negativeButtonClicked");
                dialog.cancel();
            }
        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_container_map, container, false);

        FragmentTransaction tr = getFragmentManager().beginTransaction();
        tr.replace(R.id.container_map_fragment, MapFragment.newInstance(), "MapFragment");
        tr.commit();

        return rootView;
    }
}