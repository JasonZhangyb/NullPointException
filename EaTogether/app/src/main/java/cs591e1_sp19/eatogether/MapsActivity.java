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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import static android.view.View.GONE;
import static cs591e1_sp19.eatogether.AppState.current_lati;
import static cs591e1_sp19.eatogether.AppState.current_longi;
import static cs591e1_sp19.eatogether.AppState.onGoingPost;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.view.GestureDetectorCompat;
import android.view.MotionEvent;
import android.view.GestureDetector;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    TextView countTxt;

    private GoogleMap mMap;
    private ImageView rest_search;
    private ImageView inv;
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
    private String myString = "MAP";

    private SensorManager s_m;

    private float accel_current;
    private float accel_last;
    private float shake;
    private int shake_point=20;
    private GestureDetectorCompat gestureDetector;

    LatLng current_loca = new LatLng(Double.parseDouble(current_lati), Double.parseDouble(current_longi));

    //database real time url
    String databaseURL = "https://eatogether-cs591.firebaseio.com/";

    Firebase mRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //connect the firebase
        Firebase.setAndroidContext(this);
        mRef = new Firebase(databaseURL + "Users");

        // add search button dynamically
        rll_search= (RelativeLayout) findViewById(R.id.search);
        rest_search = new ImageView(this);
        rest_search.setLayoutParams(new RelativeLayout.LayoutParams(150, 150));
        rest_search.setImageResource(R.drawable.search02);
        rll_search.addView(rest_search);


        s_m = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        s_m.registerListener(sensorListener, s_m.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

        accel_current = SensorManager.GRAVITY_EARTH; //THE CURRENT ACCELERATION
        accel_last = SensorManager.GRAVITY_EARTH; //TO STORE THE PREVIOUS ACCELERATION
        shake = 0.00f;



        // if click the search button, then jump to the search page
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

        //Send value to menu fragment to indicate which icon is pressed
        Bundle args = new Bundle();
        args.putString("value","MAP");
        menu.putArguments(args);

        // show the notification icon on the map page
        inv_layout = findViewById(R.id.inv);
        inv = new ImageView(getApplicationContext());
        inv.setLayoutParams(new RelativeLayout.LayoutParams(150, 150));
        inv.setImageResource(R.drawable.ic_notifications_black_24dp);


        //If user press the notification btn
        mRef.child(AppState.userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                inv_layout.removeView(inv);
                if (dataSnapshot.hasChild("Invite")){

                    //If user accept invitation, then store some value
                    PostModel invitation = dataSnapshot.child("Invite").getValue(PostModel.class);
                    AppState.onGoingPost = invitation.post_id;
                    AppState.onGoingRes = invitation.restaurant_id;
                    final String creator_id = invitation.user_id;
                    final String creator_name = invitation.user_name;
                    final String creator_avatar = invitation.avatar;
                    final String res_name = invitation.restaurant_name;
                    final String time1 = invitation.time1;
                    final String time2 = invitation.time2;
                    final String latitude = invitation.latitude;
                    final String longitude = invitation.longitude;
                    final String status = invitation.note;

                    //if user receive an invitation
                    if (status.equals("onGoing")) {
                        Toast.makeText(MapsActivity.this, "You just received an invitation!", Toast.LENGTH_SHORT).show();
                        inv_layout.addView(inv);
                    } else {
                        //if the host withdraw the invitation
                        Toast.makeText(MapsActivity.this,
                                "Oops, looks like the invitation has been withdrawn", Toast.LENGTH_SHORT).show();
                    }

                    // invitation dialog
                    inv_layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.v("test_dialog",creator_name);
                            openInvDialog(
                                    creator_name,
                                    creator_avatar,
                                    //"temp",
                                    res_name,
                                    creator_id,
                                    time1,
                                    time2,
                                    latitude,
                                    longitude,
                                    status);
                        }
                    });

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        // Shows the Nearby restaurant with your setting radius
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


    //public String getMyData(){
    //return myString;
    // }

    //This is a dialog for invitation. When user click the notification icon, this dialog will present

    public void openInvDialog(String name, String avatar, String res, String id,
                              String t1, String t2, String lat, String lon, String note) {

        final Dialog inv_dialog = new Dialog(this);
        final String status = note;
        inv_dialog.setContentView(R.layout.invitation_dialog);

        TextView inv_name = inv_dialog.findViewById(R.id.inv_name);
        TextView inv_res = inv_dialog.findViewById(R.id.inv_restaurant);
        //TextView inv_note = inv_dialog.findViewById(R.id.inv_note);
        ImageView inv_avatar = inv_dialog.findViewById(R.id.inv_avatar);
        Button inv_accept = inv_dialog.findViewById(R.id.inv_accept);
        Button inv_decline = inv_dialog.findViewById(R.id.inv_decline);

        if (status.equals("onGoing")) {

            //inv_dialog.setTitle("Invitation from:");
            inv_name.setText(name);
            inv_res.setText(res);
            //inv_note.setText(note);
            Picasso.get().load(avatar).into(inv_avatar);

            final String res_id = res;
            final String partner_id = id;
            final String time1 = t1;
            final String time2 = t2;
            final String latitude = lat;
            final String longitude = lon;

            //contains accept and decline choice

            inv_dialog.show();
            inv_accept.setText("ACCEPT");
            inv_decline.setText("DECLINE");

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
                            // finished: changed current to destination
                            latitude,
                            longitude,
                            guest1,
                            time1,
                            time2);

                    guest2.put("guest", AppState.userID);
                    EventModel event_guest = new EventModel(
                            partner_id,
                            AppState.onGoingRes,
                            res_id,
                            AppState.onGoingPost,
                            latitude,
                            longitude,
                            guest2,
                            time1,
                            time2);

                    mRef.child(AppState.userID).child("Ongoing").setValue(event);
                    mRef.child(partner_id).child("Ongoing").setValue(event_guest);
                    mRef.child(AppState.userID).child("Invite").removeValue();
                    inv_layout.removeView(inv);
                    inv_dialog.cancel();

                    Intent i = new Intent(getApplicationContext(), OnGoingActivity.class);
                    startActivity(i);
                }
            });

            //if click on the accept btn

            inv_decline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mRef.child(AppState.userID).child("Invite").removeValue();
                    inv_layout.removeView(inv);
                    inv_dialog.cancel();
                    Toast.makeText(MapsActivity.this, "Invitation is declined", Toast.LENGTH_SHORT).show();
                }
            });
        } else {

            //if user click withdraw in the dialog, then the invitation will be wirhdrew

            inv_name.setText("Oops, looks like " + name);
            inv_res.setText("has withdrawn the invitation");
            //inv_note.setText(note);
            // TODO: change avatar into an "Oops" icon if possible
            Picasso.get().load(avatar).into(inv_avatar);

            final String res_id = res;
            final String partner_id = id;
            final String time1 = t1;
            final String time2 = t2;
            final String latitude = lat;
            final String longitude = lon;

            inv_dialog.show();
            inv_accept.setText("BACK");
            inv_accept.setVisibility(View.VISIBLE);
            inv_decline.setVisibility(GONE);

            inv_accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mRef.child(AppState.userID).child("Invite").removeValue();
                    AppState.onGoingPost = null;
                    AppState.onGoingRes = null;
                    inv_layout.removeView(inv);
                    inv_dialog.cancel();
                }
            });

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
    public void onBackPressed() {
// super.onBackPressed();
// Not calling **super**, disables back button in current screen.
    }


    //This function is for the Google Map performance. It will shows all the nearby restaurants in the Google Map

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.moveCamera(CameraUpdateFactory.newLatLng(current_loca));

        //set the Map type and zoom range
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMinZoomPreference(13);



        //set menu appear and disappear
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

        // if click any marker, a layout with restaurant info will shows on map
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

                //set the clicked marker a diff color

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

                //if click the restaurant info layout, then jump to the restaurant post page
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
    private final SensorEventListener sensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {

            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            accel_last = accel_current;
            accel_current = (float)Math.sqrt(x*x + y*y + z*z);
            float delta = accel_current - accel_last;
            shake = shake * 0.9f + delta; //GET THE SHAKING NUMBER

            if (shake>shake_point){
                Intent intent = new Intent(MapsActivity.this, ChatsList.class);

                startActivity(intent);
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
}
