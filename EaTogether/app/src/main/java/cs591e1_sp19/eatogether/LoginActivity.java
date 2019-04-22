package cs591e1_sp19.eatogether;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yelp.fusion.client.connection.YelpFusionApi;
import com.yelp.fusion.client.connection.YelpFusionApiFactory;
import com.yelp.fusion.client.models.Business;
import com.yelp.fusion.client.models.SearchResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static cs591e1_sp19.eatogether.AppState.current_lati;
import static cs591e1_sp19.eatogether.AppState.current_longi;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    EditText loginEmail,loginPassword;
    Button loginButton,registerButton,newPassButton;

    private static final int RC_SIGN_IN = 9001;
    private SignInButton signInButton;
    FirebaseAuth firebaseAuth;
    GoogleApiClient mGoogleApiClient;

    //Yelp Class
    YelpFusionApiFactory apiFactory;
    YelpFusionApi yelpFusionApi;

    String apiKey = BuildConfig.YelpApiKey;
    Map<String, String> params;


    private static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        loginEmail = (EditText) findViewById(R.id.userEmail);
        loginPassword = (EditText) findViewById(R.id.loginPassword);
        loginButton = (Button) findViewById(R.id.loginButton);
        registerButton = (Button) findViewById(R.id.registerButton);
        signInButton = (SignInButton) findViewById(R.id.google_sign_in_button);
        newPassButton = (Button) findViewById(R.id.yeniSifreButton);

        firebaseAuth = FirebaseAuth.getInstance();

        //Yelp set up
        try {
            apiFactory  = new YelpFusionApiFactory();
            yelpFusionApi = apiFactory.createAPI(apiKey);
        } catch (Exception e) {
            e.printStackTrace();
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(LoginActivity.this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signIn();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String emailS = loginEmail.getText().toString();
                final String passwordS = loginPassword.getText().toString();

                if(TextUtils.isEmpty(emailS)){
                    Toast.makeText(getApplicationContext(),"Please fill in the required fields",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(passwordS)){
                    Toast.makeText(getApplicationContext(),"Please fill in the required fields",Toast.LENGTH_SHORT).show();
                }

                if(passwordS.length()<6){
                    Toast.makeText(getApplicationContext(),"Password must be at least 6 characters",Toast.LENGTH_SHORT).show();
                }

                logIn(emailS, passwordS);
            }
        });
    }

    private void signIn() {
        Intent signIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signIntent,RC_SIGN_IN);
    }

    private void logIn(final String email, final String password) {
        FirebaseDatabase mref = FirebaseDatabase.getInstance();

        DatabaseReference db = mref
                .getReference()
                .child("Users");

        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot userSnapShot : dataSnapshot.getChildren()) {
                    if(userSnapShot.child("email").getValue().equals(email) && userSnapShot.child("password").getValue().equals(password)) {
                        Toast.makeText(getApplicationContext(), "Login Successfully", Toast.LENGTH_SHORT).show();

                        AppState.isLoggedIn = true;
                        AppState.userID = userSnapShot.getKey();
                        AppState.userName = userSnapShot.child("name").getValue().toString();
                        AppState.userRating = Float.parseFloat(userSnapShot.child("user_rating").getValue().toString());
                        AppState.ratingAmount = Integer.parseInt(userSnapShot.child("rating_amount").getValue().toString());
                        Log.v("test", AppState.userName);

                        if (userSnapShot.hasChild("Post")) {
                            AppState.userPost = userSnapShot.child("Post").child("PostID").getValue().toString();
                            Log.v("test_login", AppState.userPost);
                        } else {
                            AppState.userPost = null;
                        }

                    }
                }

                if(!AppState.isLoggedIn) {
                    Toast.makeText(getApplicationContext(), "login Faild, try again", Toast.LENGTH_SHORT).show();
                } else {
                    // login successfully, we can access the newarby restaurants
                    params = new HashMap<>();

                    if(getLocation()){

                        params.put("latitude", current_lati);
                        params.put("longitude", current_longi);
                        params.put("radius", "3000");

                        Call<SearchResponse> call = yelpFusionApi.getBusinessSearch(params);
                        call.enqueue(callback);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private boolean getLocation() {
        if (ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (LoginActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            locationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            Location location2 = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            if (location != null) {
                double latti = location.getLatitude();
                double longi = location.getLongitude();
                current_lati = String.valueOf(latti);
                current_longi = String.valueOf(longi);
                return true;

            } else if (location1 != null) {
                double latti = location1.getLatitude();
                double longi = location1.getLongitude();
                current_lati = String.valueOf(latti);
                current_longi = String.valueOf(longi);
                return true;

            } else if (location2 != null) {
                double latti = location2.getLatitude();
                double longi = location2.getLongitude();
                current_lati = String.valueOf(latti);
                current_longi = String.valueOf(longi);
                return true;

            } else {

                Toast.makeText(this, "Unble to Trace your location", Toast.LENGTH_SHORT).show();
                return false;

            }
        }

        return false;
    }

    Callback<SearchResponse> callback = new Callback<SearchResponse>() {
        @Override
        public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
            SearchResponse searchResponse = response.body();
            // Update UI text with the searchResponse.
            final ArrayList<Business> businesses = searchResponse.getBusinesses();

            FirebaseDatabase mref = FirebaseDatabase.getInstance();

            DatabaseReference db = mref
                    .getReference()
                    .child("Users")
                    .child(AppState.userID)
                    .child("Nearby");

            db.removeValue();

            for(Business bu : businesses) {
                DatabaseReference restaurant = db.child(bu.getId());
                restaurant.child("name").setValue(bu.getName());
                restaurant.child("location").setValue(bu.getCoordinates());
                restaurant.child("rating").setValue(bu.getRating());
                restaurant.child("type").setValue(bu.getCategories());
                restaurant.child("price").setValue(bu.getPrice());
            }

            Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
           // Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
           // Intent intent = new Intent(getApplicationContext(), OnGoingActivity.class);
            startActivity(intent);
        }
        @Override
        public void onFailure(Call<SearchResponse> call, Throwable t) {
            // HTTP error happened, do something to handle it.
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()){
                GoogleSignInAccount account = result.getSignInAccount();
                authWithGoogle(account);
            }
        }
    }

    private void authWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    finish();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Auth Error",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
