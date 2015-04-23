package com.project.furnishyourhome.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.furnishyourhome.R;


public class ContainerMyFurnitureFragment extends Fragment {
    private static final String TAG = ContainerMyFurnitureFragment.class.getSimpleName();

    public ContainerMyFurnitureFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_container_my_furniture, container, false);

        Log.d(TAG, "creating empty fragment");
        FragmentTransaction tr = getActivity().getSupportFragmentManager().beginTransaction();
        tr.add(R.id.container_my_furniture_fragment, MyFurnitureFragment.newInstance(), "MyFurnitureFragment");
        tr.commit();

        return rootView;
    }
}
