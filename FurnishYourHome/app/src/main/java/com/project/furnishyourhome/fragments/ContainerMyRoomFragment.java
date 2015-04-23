package com.project.furnishyourhome.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.furnishyourhome.R;


public class ContainerMyRoomFragment extends Fragment {

    public ContainerMyRoomFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_container_my_room, container, false);

        FragmentTransaction tr = getFragmentManager().beginTransaction();
        tr.replace(R.id.container_my_room_fragment, MyRoomFragment.newInstance(), "MyRoomFragment");
        tr.commit();

        return rootView;
    }
}
