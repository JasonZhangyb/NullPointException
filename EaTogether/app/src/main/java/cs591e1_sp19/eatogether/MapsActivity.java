package cs591e1_sp19.eatogether;

import android.app.Dialog;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewDebug;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
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
import static cs591e1_sp19.eatogether.AppState.onGoingPost;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    TextView countTxt;

    private GoogleMap mMap;
    private ImageView rest_search;
    private ImageView inv;
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
    private RelativeLayout inv_layout;


    LatLng current_loca = new LatLng(Double.parseDouble(current_lati), Double.parseDouble(current_longi));


    String databaseURL = "https://eatogether-cs591.firebaseio.com/";

    Firebase mRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Firebase.setAndroidContext(this);
        mRef = new Firebase(databaseURL + "Users");


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

        mRef.child(AppState.userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("Invite")){
                    PostModel invitation = dataSnapshot.child("Invite").getValue(PostModel.class);
                    AppState.onGoingPost = invitation.post_id;
                    AppState.onGoingRes = invitation.restaurant_id;
                    final String creator_id = invitation.user_id;
                    final String creator_name = invitation.user_name;
                    final String creator_avatar = invitation.avatar;
                    final String res_name = invitation.restaurant_name;
                    final String time1 = invitation.time1;
                    final String time2 = invitation.time2;
                    Toast.makeText(MapsActivity.this, "You just received an invitation!", Toast.LENGTH_SHORT).show();

                    inv_layout = findViewById(R.id.inv);
                    inv = new ImageView(getApplicationContext());
                    inv.setLayoutParams(new RelativeLayout.LayoutParams(150, 150));
                    inv.setImageResource(R.drawable.ic_notifications_black_24dp);
                    inv_layout.addView(inv);

                    inv_layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.v("test_dialog",creator_name);
                            openInvDialog(
                                    creator_name,
                                    creator_avatar,
                                    "temp",
                                    res_name,
                                    creator_id,
                                    time1,
                                    time2);
                        }
                    });

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        mRef.child(AppState.userID).child("Nearby").addValueEventListener(new ValueEventListener() {
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

    public void openInvDialog(String name, String avatar, String note, String res, String id, String t1, String t2) {
        final Dialog inv_dialog = new Dialog(this);
        inv_dialog.setContentView(R.layout.invitation_dialog);

        inv_dialog.setTitle("Invitation from:");
        TextView inv_name = inv_dialog.findViewById(R.id.inv_name);
        TextView inv_res = inv_dialog.findViewById(R.id.inv_restaurant);
        TextView inv_note = inv_dialog.findViewById(R.id.inv_note);
        ImageView inv_avatar = inv_dialog.findViewById(R.id.inv_avatar);
        Button inv_accept = inv_dialog.findViewById(R.id.inv_accept);

        inv_name.setText(name);
        inv_res.setText(res);
        inv_note.setText(note);
        Picasso.get().load(avatar).into(inv_avatar);

        final String res_id = res;
        final String partner_id = id;
        final String time1 = t1;
        final String time2 = t2;

        inv_dialog.show();

        inv_accept.setOnClickListener(new View.OnClickListener() {
            HashMap<String, String> guest1 = new HashMap<>();
            HashMap<String, String> guest2 = new HashMap<>();
            @Override
            public void onClick(View view) {
                guest1.put("guest", partner_id);
                EventModel event = new EventModel(
                        AppState.userID,
                        AppState.onGoingRes,
                        res_id,
                        AppState.onGoingPost,
                        // TODO: change current to destination
                        current_lati,
                        current_longi,
                        guest1,
                        time1,
                        time2);

                guest2.put("guest", AppState.userID);
                EventModel event_guest = new EventModel(
                        partner_id,
                        AppState.onGoingRes,
                        res_id,
                        AppState.onGoingPost,
                        current_lati,
                        current_longi,
                        guest2,
                        time1,
                        time2);

                mRef.child(AppState.userID).child("Ongoing").setValue(event);
                mRef.child(partner_id).child("Ongoing").setValue(event_guest);

                Intent i = new Intent(getApplicationContext(), OnGoingActivity.class);
                startActivity(i);
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
                mRef.child(AppState.userID).child("Nearby").addValueEventListener(new ValueEventListener() {

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
