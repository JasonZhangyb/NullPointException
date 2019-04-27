package cs591e1_sp19.eatogether;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.media.Rating;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lyft.lyftbutton.LyftButton;
import com.lyft.lyftbutton.RideParams;
import com.lyft.lyftbutton.RideTypeEnum;
import com.lyft.networking.ApiConfig;
import com.squareup.picasso.Picasso;
import com.uber.sdk.android.core.UberSdk;
import com.uber.sdk.android.rides.RideParameters;
import com.uber.sdk.android.rides.RideRequestButton;
import com.uber.sdk.android.rides.RideRequestButtonCallback;
import com.uber.sdk.rides.client.ServerTokenSession;
import com.uber.sdk.rides.client.SessionConfiguration;
import com.uber.sdk.rides.client.error.ApiError;

import java.util.ArrayList;

public class OnGoingActivity extends AppCompatActivity implements RideRequestButtonCallback, OnMapReadyCallback {

    private DatabaseReference ongoingdb;
    private DatabaseReference userdb;
    private TextView rest_name;
    private TextView user_name;
    private RatingBar user_rating;
    private TextView time;
    private Button msg_btn;
    private Button finish_btn;
    private ImageView user_avatar;

    private String username, useravatar;

    float ptner_rating;
    int rating_amount;

    private GoogleMap mMap;

    private String partner_id ="";


    private  String DROPOFF_ADDR;
    private  Double DROPOFF_LAT;
    private  Double DROPOFF_LONG;
    private  String DROPOFF_NICK;
    private  String PICKUP_NICK;
    private  String PICKUP_ADDR = "";

    private static final String ERROR_LOG_TAG = "UberSDK-SampleActivity";


    private static final Double PICKUP_LAT = Double.parseDouble(AppState.current_lati);
    private static final Double PICKUP_LONG = Double.parseDouble(AppState.current_longi);


    //Uber API login
    private static  final  String UBER_CLIENT_ID = "mzKxZx-zIYaBg2uGJjVCFq8dGS8C847H";
    private static  final  String UBER_CLIENT_SECRET = "PVHMJnO4zxF7g6x6AjUcdAZhYZhAetTWdjUAjdby";
    private static  final  String UBER_SEVER_TOKEN = "6rDZtxnzZIVgLFSGjjzLzD8IZtYUzbxPurcBJr4T";
    private static  final  String UBER_REDIRECT_URI = "https://login.uber.com/oauth/v2/authorize?response_type=code&client_id=mzKxZx-zIYaBg2uGJjVCFq8dGS8C847H&redirect_uri=http://localhost:3000/";

    //LYFT API LOGIN
    private static final String LYFT_CLIENT_ID = "iTF26WFfdvLA";
    private static final String LYFT_CLIENT_TOKEN = "21llDWWKvmtY51o4oa6YxqJ65GFWAr0AQKMx0GeUBvKlqGTalzY9zjHMtKjDLgOqFKZH1xBuXaZsi6xVoz9agnksvN1DIzzzXkN9J5uzVDxy/InNbILqBB8=";
    private static final String LYFT_CLIENT_SECRET = "kgiVUELlirL_jBhlJ_yNjNzdi6S9_D5X";


    private SessionConfiguration config;
    private ApiConfig apiConfig;

    private MenuFragment  menu = new MenuFragment();;
    private FragmentManager menu_manager;
    private FragmentTransaction menu_trans;

    public static Float rating_bar = Float.parseFloat("5.0");
    public static String restaurant;
    public static ArrayList<RatingModel> old_reviewers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_going);



        ongoingdb = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("Users")
                .child(AppState.userID)
                .child("Ongoing");

        userdb = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("Users");

/*
        ongoingdb.child("Rest_name").setValue("Fugakyu Japanese Cuisine");
        ongoingdb.child("partner_id").setValue("-LcNvXfmtYgx6_jBvAZy");
        userdb.child(AppState.userID).child("rating").setValue("4.5");
        ongoingdb.child("time1").setValue("1 pm");
        ongoingdb.child("time2").setValue("3 pm");
        ongoingdb.child("latitude").setValue("42.342954");
        ongoingdb.child("longitude").setValue("-71.119374642915");
*/
        rest_name = (TextView) findViewById(R.id.rest_name);
        user_name = (TextView) findViewById(R.id.user_name);
        user_rating = (RatingBar) findViewById(R.id.user_rating);
        time = (TextView) findViewById(R.id.time_created);
        msg_btn = (Button) findViewById(R.id.btn_message);
        finish_btn = (Button) findViewById(R.id.btn_finish);
        user_avatar = (ImageView) findViewById(R.id.user_avatar);


        menu_manager = getSupportFragmentManager();
        menu_trans = menu_manager.beginTransaction();
        menu_trans.add(R.id.menu, menu);

        menu_trans.addToBackStack(null);
        menu_trans.commit();



        msg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Chat.class);
                i.putExtra("post_id", AppState.onGoingPost);
                startActivity(i);

            }
        });

        ongoingdb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                EventModel event = dataSnapshot.getValue(EventModel.class);

                final String restaurant = event.res_name;
                rest_name.setText(restaurant);
                time.setText(event.time1 + " - " + event.time2);
                partner_id = event.guests.get("guest");

                DROPOFF_ADDR = event.res_name;
                DROPOFF_LAT = Double.parseDouble(event.latitude);
                DROPOFF_LONG = Double.parseDouble(event.longitude);
                System.out.println(">>>>>>>>>>>> location: " + DROPOFF_LAT);

                onRide();



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        userdb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                final RatingModel rating = dataSnapshot.child(partner_id).child("Rating").getValue(RatingModel.class);
                username = rating.username;
                useravatar = rating.useravatar;

                user_name.setText(username);
                user_rating.setRating(Float.parseFloat(rating.rating));

                Picasso.get().load(useravatar).into(user_avatar);

                //user_avatar.setImageResource(R.drawable.logo_login2);

                ptner_rating = Float.parseFloat(rating.rating);
                rating_amount =  Integer.parseInt(rating.rating_amount);

                if (rating.reviewers != null) {
                    old_reviewers = rating.reviewers;
                }

                finish_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openRatingDialog(username, useravatar, old_reviewers);

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // rest_name.setText(ongoingdb.child("Rest_name").toString());

        // user_name.setText(userdb.child().child("name").toString());

        // user_rating.setText(userdb.child(AppState.userID).child("rating").toString());
        // time.setText(ongoingdb.child("time1").getKey() + " - " + ongoingdb.child("time2").toString());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_maps);

        mapFragment.getMapAsync(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);   //get rid of default behavior.

        // Inflate the menu; this adds items to the action bar
        getMenuInflater().inflate(R.menu.menu_main, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //open wishlist activity

        int id = item.getItemId();

        if (id == R.id.wish) {
            Intent i = new Intent(this, WishList.class);
            startActivity(i);
            return true;
        }

        if (id == R.id.map) {
            Intent i = new Intent(this, MapsActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);  //if none of the above are true, do the default and return a boolean.
    }

    public void onRide(){
        //build a new session to connect Uber API
        config = new SessionConfiguration.Builder()
                .setClientId(UBER_CLIENT_ID)
                .setClientSecret(UBER_CLIENT_SECRET)
                .setServerToken(UBER_SEVER_TOKEN)
                .setRedirectUri(UBER_REDIRECT_URI)
                .setEnvironment(SessionConfiguration.Environment.SANDBOX)
                .build();

        //initialize Uber SDK
        UberSdk.initialize(config);


        //builf a new session to connect Lyft API
        apiConfig = new ApiConfig.Builder()
                .setClientId(LYFT_CLIENT_ID)
                .setClientToken(LYFT_CLIENT_TOKEN)
                .build();





        RideParameters rideParams = new RideParameters.Builder()
                .setDropoffLocation(
                        DROPOFF_LAT, DROPOFF_LONG, PICKUP_NICK, DROPOFF_ADDR)
                .setPickupLocation(PICKUP_LAT, PICKUP_LONG, PICKUP_NICK, PICKUP_ADDR)
                .build();
// set parameters for the RideRequestButton instance
        ServerTokenSession session = new ServerTokenSession(config);
        RideRequestButton blackButton = (RideRequestButton) findViewById(R.id.uber_button_black);

        blackButton.setRideParameters(rideParams);
        blackButton.setSession(session);
        //blackButton.setCallback(this);
        blackButton.loadRideInformation();


        //
        LyftButton lyftButton = (LyftButton) findViewById(R.id.lyft_button);
        lyftButton.setApiConfig(apiConfig);

        RideParams.Builder rideParamsBuilder = new RideParams.Builder()
                .setPickupLocation(PICKUP_LAT, PICKUP_LONG)
                .setDropoffLocation(DROPOFF_LAT, DROPOFF_LONG);
        rideParamsBuilder.setRideTypeEnum(RideTypeEnum.CLASSIC);

        lyftButton.setRideParams(rideParamsBuilder.build());
        lyftButton.load();

    }

    public void openRatingDialog(final String partner_name, String url, final ArrayList<RatingModel> old_reviewers){
        final Dialog rating_dialog = new Dialog(this);
        rating_dialog.setContentView(R.layout.rating_dialog);

        rating_dialog.setTitle("Rating For Your Partner");
        final RatingBar ratingBar = (RatingBar) rating_dialog.findViewById(R.id.ratingBar);
        TextView name = (TextView) rating_dialog.findViewById(R.id.parner_name);
        ImageView avatar = (ImageView) rating_dialog.findViewById(R.id.partner_avatar);
        Button finish = (Button) rating_dialog.findViewById(R.id.rating_finish);
        final EditText note = rating_dialog.findViewById(R.id.partner_note);

        name.setText(partner_name);
        //avatar.setImageResource(partner_avatar);
        Picasso.get().load(url).into(avatar);


        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                rating_bar = v;
                ratingBar.setRating(v);
            }
        });

        rating_dialog.show();

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference ref_posts = database.getReference("Posts");
                final DatabaseReference ref_users = database.getReference("Users");
                final DatabaseReference ref_chats = database.getReference("ChatsAlt");

                ptner_rating =  (ptner_rating * rating_amount + ratingBar.getRating() ) / (1 + rating_amount);
                rating_amount ++;

                String review = note.getText().toString();

                RatingModel my_review = new RatingModel(
                        AppState.userName,
                        AppState.userAvatar,
                        String.valueOf(rating_bar),
                        restaurant,
                        review
                );

                old_reviewers.add(my_review);

                RatingModel partner =  new RatingModel(
                        username,
                        useravatar,
                        String.valueOf(ptner_rating),
                        String.valueOf(rating_amount),
                        old_reviewers
                );

                ref_users.child(partner_id).child("Rating").setValue(partner);


                rating_dialog.cancel();


                ref_users.child(AppState.userID).child("Ongoing").removeValue();
                ref_users.child(AppState.userID).child("Posts").child(AppState.onGoingPost).removeValue();
                ref_posts.child(AppState.onGoingRes).child(AppState.onGoingPost).removeValue();
                //ref_chats.child(AppState.onGoingPost).removeValue();

                AppState.onGoingRes = null;
                AppState.onGoingPost = null;

                Intent i = new Intent(getApplicationContext(), NoOngingEventNotice.class);
                startActivity(i);
            }
        });

        System.out.println(">>>>>>>>>>>>>. ratingbar: " + ratingBar.getRating());



    }


    @Override
    public void onRideInformationLoaded() {

    }

    @Override
    public void onError(ApiError apiError) {

    }

    @Override
    public void onError(Throwable throwable) {

    }

    public void findCoffee(){

        ongoingdb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                EventModel event = dataSnapshot.getValue(EventModel.class);

                DROPOFF_LAT = Double.parseDouble(event.latitude);
                DROPOFF_LONG = Double.parseDouble(event.longitude);
                StringBuilder stringBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
                stringBuilder.append("location="+DROPOFF_LAT.toString()+","+DROPOFF_LONG.toString());
                stringBuilder.append("&radius="+2000);
                stringBuilder.append("&keyword="+"coffee");
                stringBuilder.append("&key="+getResources().getString(R.string.google_maps_key));

                System.out.println(stringBuilder);

                String url = stringBuilder.toString();

                Object dataTransfer[] = new Object[2];
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                GoogleNearbyPlaces googleNearbyPlaces = new GoogleNearbyPlaces(this);
                googleNearbyPlaces.execute(dataTransfer);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng current_loca = new LatLng(Double.parseDouble(AppState.current_lati), Double.parseDouble(AppState.current_longi));
        MarkerOptions current_marker = new MarkerOptions();
        current_marker.title("Destination");
        current_marker.position(current_loca);

        mMap.addMarker(current_marker);

        findCoffee();

        mMap.moveCamera(CameraUpdateFactory.newLatLng(current_loca));

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mMap.setMinZoomPreference(13);
    }
}
