package com.project.furnishyourhome.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.project.furnishyourhome.R;
import com.project.furnishyourhome.adapters.CustomListAdapter;
import com.project.furnishyourhome.models.CustomListItem;

import java.util.ArrayList;


public class MyFurnitureFragment extends Fragment {
    private static final String TAG = MyFurnitureFragment.class.getSimpleName();

    private static ArrayList <CustomListItem> chosenItems;
    private ListView listView;
    private CustomListAdapter adapter;
    private TextView tvTotalPrice;
    private TextView tvEmptyList;
    private double totalPrice;

    public static MyFurnitureFragment newInstance() {
        Log.d(TAG, "newInstance()");
        return new MyFurnitureFragment();
    }

    public static MyFurnitureFragment newInstance(Bundle args) {
        Log.d(TAG, "newInstance(Bundle args)");
        MyFurnitureFragment f = new MyFurnitureFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);

        if(chosenItems == null) {
            chosenItems = new ArrayList<>();
        }
        if(savedInstanceState != null) {
            Log.d(TAG, "restore from SAVED instance");
            chosenItems = savedInstanceState.getParcelableArrayList("chosenItems");
        } else {
            Log.d(TAG, "no SAVED instance");
        }

        if(chosenItems.isEmpty() && getArguments()!=null) {
            Log.d(TAG, "load items from ARGUMENTS");
            chosenItems = getArguments().getParcelableArrayList("chosenItems");
        } else {
            Log.d(TAG, "no items from ARGUMENTS");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()");
        View rootView = inflater.inflate(R.layout.fragment_my_furniture, container, false);

        tvEmptyList = (TextView) rootView.findViewById(R.id.tv_empty_list_info);

        listView = (ListView) rootView.findViewById(R.id.lv_my_furniture);

        tvTotalPrice = (TextView) rootView.findViewById(R.id.tv_total_price);
        totalPrice = 0;
        for (int i=0; i<chosenItems.size(); i++) {
            totalPrice += chosenItems.get(i).getPrice();
        }
        tvTotalPrice.setText(getResources().getString(R.string.total_price)+totalPrice+getResources().getString(R.string.currency));
        return rootView;
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart()");
        super.onStart();
        adapter = new CustomListAdapter(getActivity().getApplicationContext(), R.layout.favourites_list_item, chosenItems);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               //TODO: implement some code here
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showDeleteAlertToUser(position);
                return false;
            }
        });
    }

    private void showDeleteAlertToUser(final int position){
        Log.d(TAG, "showDeleteAlertToUser");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.Base_Theme_AppCompat_Dialog));
        alertDialogBuilder.setMessage("Do you really want to delete this item?");
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){
                Log.d(TAG, "positiveButtonClicked");
                totalPrice -= chosenItems.get(position).getPrice();
                chosenItems.remove(position);
                adapter.notifyDataSetChanged();
                tvTotalPrice.setText(getResources().getString(R.string.total_price)+totalPrice+getResources().getString(R.string.currency));

                MyRoomFragment fragment = (MyRoomFragment) getActivity().getSupportFragmentManager().findFragmentByTag("MyRoomFragment");
                Fragment.SavedState myFragmentState = getActivity().getSupportFragmentManager().saveFragmentInstanceState(fragment);
                Bundle roomArgs = new Bundle();
                Bundle mapArgs = new Bundle();

                roomArgs.putInt("deletedPosition", position);
                mapArgs.putParcelableArrayList("chosenItems", chosenItems);

                FragmentTransaction tr = getActivity().getSupportFragmentManager().beginTransaction();
                MyRoomFragment newFragment = MyRoomFragment.newInstance(roomArgs);
                newFragment.setInitialSavedState(myFragmentState);
                tr.replace(R.id.container_my_room_fragment, newFragment, "MyRoomFragment");
                tr.replace(R.id.container_map_fragment, MapFragment.newInstance(mapArgs));
                tr.commit();
                onResume();

            }
        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){
                Log.d(TAG, "negativeButtonClicked");
                dialog.cancel();
                onResume();
            }
        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume()");
        super.onResume();
        adapter.notifyDataSetChanged();
        checkIfListIsEmpty();
    }

    private void checkIfListIsEmpty(){
        Log.d(TAG, "checkIfListIsEmpty()");
        if(chosenItems != null && chosenItems.isEmpty()) {
            tvEmptyList.setVisibility(View.VISIBLE);
            tvEmptyList.setText("List is empty.");
        } else {
            tvEmptyList.setText("");
            tvEmptyList.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState(Bundle outState)");
        outState.putParcelableArrayList("chosenItems", chosenItems);
        super.onSaveInstanceState(outState);
    }
}