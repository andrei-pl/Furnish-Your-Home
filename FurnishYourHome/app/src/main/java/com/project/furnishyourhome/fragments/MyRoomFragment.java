package com.project.furnishyourhome.fragments;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.project.furnishyourhome.MainActivity;
import com.project.furnishyourhome.R;
import com.project.furnishyourhome.adapters.CustomListAdapter;
import com.project.furnishyourhome.database.FYHApp;
import com.project.furnishyourhome.interfaces.DbTableNames;
import com.project.furnishyourhome.models.CanvasView;
import com.project.furnishyourhome.models.CustomBitmap;
import com.project.furnishyourhome.models.CustomListItem;
import com.project.furnishyourhome.models.Furniture;
import com.project.furnishyourhome.models.HolderCount;
import com.project.furnishyourhome.models.Type;
import com.project.furnishyourhome.models.parse.FurnitureParse;
import com.project.furnishyourhome.services.DataCountService;

import org.lucasr.twowayview.TwoWayView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MyRoomFragment extends Fragment implements DbTableNames {
    private static final String TAG = MyRoomFragment.class.getSimpleName();
    private static final int NOTIFICATION_ID = 0;
    private static ArrayList<Type> types = new ArrayList<>();
    private static ArrayList<Furniture> furnitures = new ArrayList<>();
    private static ArrayList<CustomListItem> horizontalListItems;
    private static ArrayList<CustomBitmap> canvasItems;
    private static ArrayList<CustomListItem> chosenItems;
    private static HolderCount holderCount = new HolderCount();

    private CustomListAdapter adapter;
    private CanvasView customCanvas;
    private Intent intent;
    private TaskUpdateList taskUpdateList;
    private Handler updateListHandler;
    private int countDataOnServer = 0;
    private TwoWayView twoWayView;
    private Activity activity;

    //int oldh;
    //int oldw;

    public static MyRoomFragment newInstance() {
        Log.d(TAG, "newInstance()");
        MyRoomFragment f = new MyRoomFragment();
        if((f.horizontalListItems == null || f.horizontalListItems.size() == 0) && f.furnitures != null) {
            f.horizontalListItems = f.convertFurnitureToListItem(furnitures);
        }
        return f;
    }

    public static MyRoomFragment newInstance(Bundle args) {
        Log.d(TAG, "newInstance(Bundle args)");
        MyRoomFragment f = new MyRoomFragment();
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

        if(horizontalListItems == null) {
            horizontalListItems = new ArrayList<>();
        }

        if(canvasItems == null) {
            canvasItems = new ArrayList<>();
            //   arrayList = savedInstanceState.getParcelableArrayList("savedBitmaps");
        }

        if(savedInstanceState != null) {
            Log.d(TAG, "restore from SAVED instance");
           // canvasItems = savedInstanceState.getParcelableArrayList("savedBitmaps");
            chosenItems = savedInstanceState.getParcelableArrayList("chosenItems");
            horizontalListItems = savedInstanceState.getParcelableArrayList("horizontalListItems");

            if(getArguments() != null) {
                if(getArguments().containsKey("horizontalListItems")) {
                    horizontalListItems = getArguments().getParcelableArrayList("horizontalListItems");
                    Log.d(TAG, "overriding SAVED instance, overriding horizontalListItems");
                }
                if(getArguments().containsKey("deletedPosition")) {
                    canvasItems.remove(getArguments().getInt("deletedPosition"));
                    Log.d(TAG, "overriding SAVED instance, removing item from canvasItems");
                }
            }
        } else {
            Log.d(TAG, "no SAVED instance");
        }

        if(horizontalListItems.isEmpty() && getArguments()!=null) {
            Log.d(TAG, "load items from ARGUMENTS");
            horizontalListItems = getArguments().getParcelableArrayList("horizontalListItems");
        } else {
            Log.d(TAG, "no items from ARGUMENTS");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()");
        View rootView = inflater.inflate(R.layout.fragment_my_room, container, false);

        customCanvas = (CanvasView) rootView.findViewById(R.id.cv_room_canvas);
        customCanvas.setBackgroundResource(R.drawable.room);

        customCanvas.setAddedBitmaps(canvasItems);

        twoWayView = (TwoWayView) rootView.findViewById(R.id.twv_furniture);

        this.activity = getActivity();

        // Start service data counter
        taskUpdateList = new TaskUpdateList();
        updateListHandler = new Handler();
        updateListHandler.postDelayed(taskUpdateList, 30000);

        resultReceiver = new MyResultReceiver(null);
        this.intent = new Intent(getActivity(), DataCountService.class);
        this.intent.putExtra("receiver", resultReceiver);
        activity.startService(intent);
        return rootView;
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart()");
        super.onStart();

        this.setTwoWayViewList();

        twoWayView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                CustomListItem item = (CustomListItem) parent.getItemAtPosition(position);

                //resizing bitmap depending on screen before inserting in canvas
                int width = (int) (item.getBitmap().getWidth()*getActivity().getResources().getDisplayMetrics().density);
                int height = (int) (item.getBitmap().getHeight() * getActivity().getResources().getDisplayMetrics().density);
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(item.getBitmap(), width, height, true);

                customCanvas.addNewElement(resizedBitmap);

                chosenItems.add(horizontalListItems.get(position));
                Bundle args = new Bundle();
                args.putParcelableArrayList("chosenItems", chosenItems);

                FragmentTransaction tr = getActivity().getSupportFragmentManager().beginTransaction();
                tr.replace(R.id.container_my_furniture_fragment, MyFurnitureFragment.newInstance(args));
                tr.replace(R.id.container_map_fragment, MapFragment.newInstance(args));
                tr.commit();
                return false;
            }
        });
    }

    private void setTwoWayViewList() {
        adapter = null;
        if(horizontalListItems == null || horizontalListItems.size() == 0){
            adapter = new CustomListAdapter(getActivity(), R.layout.horizontal_list_item, convertFurnitureToListItem(furnitures));

        } else {
            adapter = new CustomListAdapter(getActivity(), R.layout.horizontal_list_item, horizontalListItems);
        }
        adapter.notifyDataSetChanged();
        twoWayView.setAdapter(adapter);
        twoWayView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CustomListItem item = horizontalListItems.get(position);

                //its not the best but work stable
                Bundle args = new Bundle();
                args.putString("title", item.getTitle());
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                Bitmap bitmap = item.getBitmap();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                args.putByteArray("bitmap", byteArray);
                args.putDouble("price", item.getPrice());
                args.putString("dimensions", item.getDimensions());
                args.putString("material", item.getMaterial());
                args.putString("info", item.getInfo());

                FragmentTransaction tr = getActivity().getSupportFragmentManager().beginTransaction();
                tr.replace(R.id.right_drawer, NavDrawerRightFragment.newInstance(args));
                tr.commit();
            }
        });
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume()");
        super.onResume();
        adapter.notifyDataSetChanged();

        /*if(getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // TODO: this fucking recalculation
            recalculateCoordinates(oldw, oldh, canvasItems);
        }*/
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState()");
        //outState.putInt("oldw", customCanvas.getWidth());
        //outState.putInt("oldh", customCanvas.getHeight());
        outState.putParcelableArrayList("savedBitmaps", customCanvas.getAddedBitmaps());
        outState.putParcelableArrayList("chosenItems", chosenItems);
        outState.putParcelableArrayList("horizontalListItems", horizontalListItems);
        canvasItems = customCanvas.getAddedBitmaps();
        super.onSaveInstanceState(outState);
    }

    private class TaskUpdateList implements Runnable {

        @Override
        public void run() {
            if (holderCount.count != countDataOnServer && countDataOnServer > 0) {
                Log.d(TAG, "checkServerCount: "+countDataOnServer);
                Log.d(TAG, "holderCount.count: "+holderCount.count);
                Log.d(TAG, "downloading data");
                loadData(true);

                showNotification();
                Log.d("MyRoomFragment", "The data was updated");
            }
            updateListHandler.postDelayed(taskUpdateList, 5000);
        }

    }

    @Override
    public void onDestroyView() {
        activity.stopService(intent);
        super.onDestroyView();
    }

    private void showNotification() {
//        // Create an intent that will be fired when the user clicks the notification.
//        Intent intent = new Intent(activity, MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity (activity, 0, intent, 0);
//
//        // Use NotificationCompat.Builder to set up our notification.
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(activity);
//
//        // Icon in the notification bar. Also appears in the lower right hand corner of the notification itself.
//        builder.setSmallIcon(R.mipmap.ic_launcher);
//        // The content title, which appears in large type at the top of the notification
//        builder.setContentTitle("New stuff added");
//        // The content text, which appears in smaller text below the title
//        builder.setContentText("See our new products");
//        // Icon which appears on the left of the notification.
//        builder.setLargeIcon(BitmapFactory.decodeResource(activity.getResources(), R.mipmap.ic_launcher));
//        // Notification will disappear after the user taps it, rather than remaining until it's explicitly dismissed.
//        builder.setAutoCancel(true);
//        // Set the intent that will fire when the user taps the notification.
//        builder.setContentIntent(pendingIntent);
//
//        // Immediately display the notification icon in the notification bar.
//        NotificationManager notificationManager = (NotificationManager) activity.getSystemService(activity.NOTIFICATION_SERVICE);
//        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    MyResultReceiver resultReceiver;
    private DataCountService dataService;
    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder binder) {
            DataCountService.MyBinder dataBinder = (DataCountService.MyBinder) binder;
            dataService = dataBinder.getService();
            Log.d(TAG, "Service connected");
        }

        public void onServiceDisconnected(ComponentName className) {
            Log.d(TAG, "Service disconnected");
            dataService = null;
        }
    };

    class UpdateUI implements Runnable {
        public UpdateUI(int count) {
            countDataOnServer = count;
        }

        public void run() {
            Log.d(TAG, "activityCount: " + countDataOnServer);
        }
    }

    private class MyResultReceiver extends ResultReceiver {
        public MyResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultCode == 100) {
                try {
                    activity.runOnUiThread(new UpdateUI(resultData.getInt("count")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

   /* // TODO: this function
    private void recalculateCoordinates(int oldw, int oldh, ArrayList<CustomBitmap> arrayList) {
        Log.d(TAG, "recalculateCoordinates()");
        if ((oldw != 0) && (oldh != 0)) {
//            float f1 = this.customCanvas.getWidth() / this.oldw;  // incorrect

            float currentWidth = customCanvas.getCanvasWidth();// get current canvas width
            float currentHeight = customCanvas.getCanvasHeight();  // get current canvas height

//            Log.d("DIMENTIONS", "w: " + this.customCanvas.getWidth() + " oldw: " + this.oldw + " coef: " + f1);
//            float f2 = this.customCanvas.getHeight() / this.oldh; // incorrect
//
//            Log.d("DIMENTIONS", "h: " + this.customCanvas.getHeight() + " oldh: " + this.oldh + " coef: " + f2);

            for (int i = 0; i < arrayList.size(); i++) {
                CustomBitmap item = arrayList.get(i); // and the problem with ic_no_preview and Y equals 0 disappears :)

                float coefficientX = oldw / (item.getX() + item.getHalfWidth()); // add getHalfWidth to find the center ic_no_preview of the image
                float coefficientY = oldh / (item.getY() + item.getHalfHeight()); // add getHalfHeight to find the center Y of the image

                //float coefficientX = oldw / customCanvas.getCanvasWidth(); // add getHalfWidth to find the center ic_no_preview of the image
                //float coefficientY = oldh / customCanvas.getCanvasHeight(); // add getHalfHeight to find the center Y of the image

                Log.d("DIMENTIONS", "w: " + currentWidth + " oldw: " + oldw + " coef: " + coefficientX);
                Log.d("DIMENTIONS", "h: " + currentHeight + " oldh: " + oldh + " coef: " + coefficientY);

                float nextX = currentWidth / coefficientX;
                float nextY = currentHeight / coefficientY;

                item.setX(nextX);
                item.setY(nextY);
                arrayList.set(i, item);
                customCanvas.setAddedBitmaps(arrayList);
            }
        }
    }*/

    private void loadData(boolean isFromInternet){
        (new GetAsyncResult(isFromInternet)).execute();
    }

    private class GetAsyncResult extends AsyncTask<Void, Void, Void> {

        boolean isFromInternet = false;
        ArrayList<CustomListItem> leftNavitems;

        private GetAsyncResult(boolean isFromInternet) {
            this.isFromInternet = isFromInternet;
            leftNavitems = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                types = new ArrayList<>();
                //menu items for left drawer
                final ParseQuery<ParseObject> typesQuery = ParseQuery.getQuery("Furniture");
                List<ParseObject> parseObjects = typesQuery.find();

                for (ParseObject obj : parseObjects) {
                    String type = obj.getString("type");
                    String id = obj.getObjectId();
                    ParseFile imgParse = obj.getParseFile("icon");
                    byte[] imageByte = new byte[0];
                    try {
                        imageByte = imgParse.getData();
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }

                    Bitmap icon = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
                    CustomListItem item = new CustomListItem(type, icon);

                    //need for DB update
                    Type typeItem = new Type();
                    typeItem.setId(id);
                    typeItem.setType(type);
                    typeItem.setBitmap(icon);
                    types.add(typeItem);
                    leftNavitems.add(item);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            final ParseQuery<FurnitureParse> furnitureItems = ParseQuery.getQuery(FurnitureParse.class);
            List<FurnitureParse> fItems = null;

            try {
                fItems = furnitureItems.find();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            furnitures = new ArrayList<>();
            if (fItems != null) {
                for (FurnitureParse fItem : fItems) {
                    String type = fItem.getType();

                    // Now SofaParse and TableParse became useless
                    Furniture furniture = fItem.getFurniture();
                    furnitures.add(furniture);
                }
            }
            Log.d(TAG, "Data from internet saved");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            int itemsCountInDb = ((FYHApp) activity.getApplication()).getUtilitiesDb().getTableCount(TABLE_FURNITURES);

            //if items in Database are more than the items from internet then clean all data
            if (itemsCountInDb > furnitures.size()) {
                ((FYHApp) activity.getApplication()).getUtilitiesDb().deleteTable(TABLE_FURNITURES);
                ((FYHApp) activity.getApplication()).getUtilitiesDb().deleteTable(TABLE_TYPES);
            }

            if (itemsCountInDb < furnitures.size()) {
                boolean isSavedTypesIntoDB = ((FYHApp) activity.getApplication()).getUtilitiesDb().addTypes(types);
                if (isSavedTypesIntoDB) {
                    Log.d("Database", "Types saved into DB");
                } else {
                    Log.d("Database", "Something went wrong (types)");
                }
                boolean isSavedItemsIntoDB = ((FYHApp) activity.getApplication()).getUtilitiesDb().addItems(furnitures);
                if (isSavedItemsIntoDB) {
                    Log.d("Database", "Items saved into DB");
                } else {
                    Log.d("Database", "Something went wrong (items)");
                }
            }

            holderCount.count = furnitures.size();
            Bundle args = new Bundle();
            ArrayList<CustomListItem> listItems = convertFurnitureToListItem(furnitures);
            args.putParcelableArrayList("horizontalListItems", listItems);
            horizontalListItems = listItems;

            MainActivity.instance.refreshData();
            setTwoWayViewList();

            MyRoomFragment myRoom = MyRoomFragment.newInstance();
            Log.d(TAG, "loading MyRoomFragment.newInstance(args)");
            if(getActivity() != null) {
                FragmentTransaction tr = getActivity().getSupportFragmentManager().beginTransaction();
                tr.replace(R.id.container_my_room_fragment, myRoom, "MyRoomFragment");
                tr.commit();
            }
        }
    }

    private ArrayList<CustomListItem> convertFurnitureToListItem(ArrayList<? extends Furniture> furniture) {
        ArrayList<CustomListItem> listItems = new ArrayList<>();
        for (int i = 0; i < furniture.size(); i++) {
            CustomListItem item = new CustomListItem();
            item.setTitle(furniture.get(i).getName());
            item.setBitmap(furniture.get(i).getDrawable());
            item.setPrice(furniture.get(i).getPrice());
            item.setDimensions(furniture.get(i).getDimensions());
            item.setMaterial(furniture.get(i).getMaterial());
            item.setInfo(furniture.get(i).getInfo());
            item.setStore(furniture.get(i).getStore());
            listItems.add(item);
        }
        return listItems;
    }
}
