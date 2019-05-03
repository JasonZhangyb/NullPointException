package com.example.yelpfusionapi;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.ViewDebug;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {


    private GoogleMap mMap;
    private ImageView rest_search;
    //private int click_times = 0;
    //private String marker_id = "";
    private Marker previous_marker;
    private int count = 0;
    private int set_menu = 0;

    private String rest_id;
    private String price;
    private float rating;
    private String info;

    private RestaurantInfo fragment;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private RelativeLayout rll_restaurant;
    private RelativeLayout rll_search;
    private RelativeLayout rll_menu;

    private TestImage test_img = new TestImage();
    private FragmentManager test_manager;
    private FragmentTransaction test_trans;





    LatLng current_loca = new LatLng(42.3500397, -71.1093047);


    String databaseURL = "https://eatogether-cs591.firebaseio.com/";

    Firebase mRef;
    Firebase databaseRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Firebase.setAndroidContext(this);
        mRef = new Firebase(databaseURL + "Nearby");


        rll_search= (RelativeLayout) findViewById(R.id.search);
        rest_search = new ImageView(this);
        rest_search.setLayoutParams(new RelativeLayout.LayoutParams(150, 150));
        rest_search.setImageResource(R.drawable.search03);
        rll_search.addView(rest_search);


        rll_menu = (RelativeLayout)findViewById(R.id.menu);






        rll_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, MainActivity.class);

                startActivity(intent);
            }
        });

       // search = (ImageView) findViewById(R.id.search);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

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






        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                   // String data = snapshot.getKey();
                    LatLng restaurant_loca = new LatLng(snapshot.child("location").child("latitude").getValue(Double.class), snapshot.child("location").child("longitude").getValue(Double.class));
                    MarkerOptions restaurant_marker = new MarkerOptions();
                    restaurant_marker.title(snapshot.child("name").getValue(String.class));
                    restaurant_marker.position(restaurant_loca);
                    mMap.addMarker(restaurant_marker);

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        mMap.moveCamera(CameraUpdateFactory.newLatLng(current_loca));

        //set the Map type and zoom range
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMinZoomPreference(13);





        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                System.out.println("????????????????Click on map");
                if(count != 0){
                    previous_marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));


                    fragmentManager.beginTransaction().remove(fragment).commit();



                }

                if(set_menu % 2 == 0){

                    test_manager = getSupportFragmentManager();
                    test_trans = test_manager.beginTransaction();
                    test_trans.add(R.id.menu, test_img);

                    test_trans.addToBackStack(null);
                    test_trans.commit();



                }
                else{
                    test_manager.beginTransaction().remove(test_img).commit();
                }
                set_menu++;

                count = 0;
            }
        });



        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                //If click the marker twice, then go to the main activity
                if(set_menu % 2 != 0){
                    test_manager.beginTransaction().remove(test_img).commit();
                }
                set_menu = 1;

                if(count != 0){
                    previous_marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    fragmentManager.beginTransaction().remove(fragment).commit();


                }
                count++;

                float color = 37;

                marker.setIcon(BitmapDescriptorFactory.defaultMarker(color));

                previous_marker = marker;
                //click_times++;


                fragment = new RestaurantInfo();

                //read data from firebase
                mRef.addValueEventListener(new ValueEventListener() {

                    @Override

                    public void onDataChange(DataSnapshot dataSnapshot) {
                        System.out.println("I'm here!!!!!!!!!");
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            String data = snapshot.child("name").getValue(String.class);
                            if(data.equals(marker.getTitle())){
                                System.out.println("I'm here!!!!!!!!!");
                                rest_id = snapshot.getKey();
                                price = snapshot.child("price").getValue(String.class);
                                rating = snapshot.child("rating").getValue(float.class);
                                info = snapshot.child("type").child("0").child("title").getValue(String.class);
                                System.out.println("!!!!!!!!!!!! " + price + " " + rating  + " "+ info);

                                //send data to

                                Bundle bundle = new Bundle();
                                bundle.putString("rest_name", marker.getTitle());
                                bundle.putString("rest_price", price);
                                bundle.putFloat("rest_rating", rating);
                                bundle.putString("rest_info", info);
                                fragment.setArguments(bundle);

                                fragmentManager = getSupportFragmentManager();
                                transaction = fragmentManager.beginTransaction();
                                transaction.add(R.id.fragment, fragment);

                                transaction.addToBackStack(null);
                                transaction.commit();



                            }
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });

                rll_restaurant = (RelativeLayout) findViewById(R.id.fragment);


                rll_restaurant.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MapsActivity.this, RestaurantPost.class);

                        intent.putExtra("rest_id", rest_id);
                        startActivity(intent);
                    }
                });





                return false;
            }
        });





    }
}
