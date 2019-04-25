package cs591e1_sp19.eatogether;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.firebase.client.ChildEventListener;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewDebug;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.yelp.fusion.client.connection.YelpFusionApi;
import com.yelp.fusion.client.connection.YelpFusionApiFactory;
import com.yelp.fusion.client.models.Business;
import com.yelp.fusion.client.models.Coordinates;
import com.yelp.fusion.client.models.SearchResponse;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.provider.AlarmClock.EXTRA_MESSAGE;
import static cs591e1_sp19.eatogether.AppState.current_lati;
import static cs591e1_sp19.eatogether.AppState.current_longi;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {


    private GoogleMap mMap;
    private ImageView rest_search;
    //private int click_times = 0;
    //private String marker_id = "";
    private Marker previous_marker;
    private MarkerOptions restaurant_marker = new MarkerOptions();
    private int count = 0;
    private int set_menu = 0;


    private String rest_id;
    private String price;
    private float rating;
    private String info;

    private RestaurantInfo fragment;
    private MenuFragment  menu = new MenuFragment();;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private FragmentManager menu_manager;
    private FragmentTransaction menu_trans;
    private RelativeLayout rll_restaurant;
    private RelativeLayout rll_search;


    LatLng current_loca = new LatLng(Double.parseDouble(current_lati), Double.parseDouble(current_longi));


    String databaseURL = "https://eatogether-cs591.firebaseio.com/";

    Firebase mRef;
    Firebase databaseRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Firebase.setAndroidContext(this);
        mRef = new Firebase(databaseURL + "Users/" + AppState.userID + "/Nearby/");


        rll_search= (RelativeLayout) findViewById(R.id.search);
        rest_search = new ImageView(this);
        rest_search.setLayoutParams(new RelativeLayout.LayoutParams(150, 150));
        rest_search.setImageResource(R.drawable.search02);
        rll_search.addView(rest_search);


        rll_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, RestaurantSearch.class);

                startActivity(intent);
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        //Add menu dynamically
        menu_manager = getSupportFragmentManager();
        menu_trans = menu_manager.beginTransaction();
        menu_trans.add(R.id.menu, menu);

        menu_trans.addToBackStack(null);
        menu_trans.commit();



        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    // String data = snapshot.getKey();
                    MapModel data = snapshot.getValue(MapModel.class);

                    double latitude = data.location.getLatitude();
                    double longitude = data.location.getLongitude();
                    LatLng restaurant_loca = new LatLng(latitude, longitude);
                    //MarkerOptions restaurant_marker = new MarkerOptions();
                    restaurant_marker.title(data.res_name);
                    restaurant_marker.position(restaurant_loca);
                    mMap.addMarker(restaurant_marker);

                }
            }


            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


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

        mMap.moveCamera(CameraUpdateFactory.newLatLng(current_loca));

        //set the Map type and zoom range
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMinZoomPreference(13);




/*
//store data in firebase
        for(int i = 0; i < rest_name.length; i++){
            Firebase nameRegi = new Firebase(databaseURL + "/Restaurants/" + rest_name[i] + "/name");
            nameRegi.setValue(rest_name[i]);
            Firebase priceRegi = new Firebase(databaseURL + "/Restaurants/" + rest_name[i] + "/price");
            priceRegi.setValue(rest_price[i]);

            Firebase ratingRegi = new Firebase(databaseURL + "/Restaurants/" + rest_name[i] + "/rating");
            ratingRegi.setValue(rest_rating[i]);

            Firebase infoRegi = new Firebase(databaseURL + "/Restaurants/" + rest_name[i] + "/info");
            infoRegi.setValue(rest_info[i]);

            Firebase latRegi = new Firebase(databaseURL + "/Restaurants/" + rest_name[i] + "/lat");
            latRegi.setValue(rest_lat[i]);

            Firebase longRegi = new Firebase(databaseURL + "/Restaurants/" + rest_name[i] + "/long");
            longRegi.setValue(rest_long[i]);


        }
*/


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                System.out.println("????????????????Click on map");
                if(count != 0){
                    previous_marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));


                    fragmentManager.beginTransaction().remove(fragment).commit();


                }

                if(set_menu % 2 != 0){

                    menu_manager = getSupportFragmentManager();
                    menu_trans = menu_manager.beginTransaction();
                    menu_trans.add(R.id.menu, menu);

                    menu_trans.addToBackStack(null);
                    menu_trans.commit();



                }
                else{
                    menu_manager.beginTransaction().remove(menu).commit();
                }

                set_menu++;
                count = 0;
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                //If click the marker twice, then go to the main activity
                if(set_menu % 2 == 0){
                    menu_manager.beginTransaction().remove(menu).commit();
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

                //read data from firebase
                mRef.addValueEventListener(new ValueEventListener() {

                    @Override

                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            MapModel data = snapshot.getValue(MapModel.class);



                            if(data.res_name.equals(marker.getTitle())){
                                rest_id = snapshot.getKey();
                                price = data.res_price;
                                rating = data.res_rating.floatValue();
                                info = data.type.get(0).getTitle();


                                Bundle bundle = new Bundle();
                                bundle.putString("rest_name", marker.getTitle());
                                bundle.putString("rest_price", price);
                                bundle.putFloat("rest_rating", rating);
                                bundle.putString("rest_info", info);


                                fragmentManager = getSupportFragmentManager();
                                Fragment old_frag = fragmentManager.findFragmentByTag("marker");

                                if (old_frag != null) {
                                    fragmentManager.beginTransaction().remove(old_frag).commitAllowingStateLoss();
                                }

                                fragment = new RestaurantInfo();
                                fragment.setArguments(bundle);

                                fragmentManager = getSupportFragmentManager();
                                transaction = fragmentManager.beginTransaction();
                                transaction.add(R.id.fragment, fragment, "marker");

                                //transaction.addToBackStack(null);
                                transaction.commitAllowingStateLoss();
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



                //Bundle frag_bundle = fragment.getArguments();
                //System.out.println(">>>>>>>>>>>>" + frag_bundle.getBoolean("ifClick") );

                /*
                if((click_times > 1 && marker_id.equals(marker.getId()))){
                    click_times = 0;
                    marker_id = "";




                }

                marker_id = marker.getId();
                */
                return false;
            }
        });





    }
}
