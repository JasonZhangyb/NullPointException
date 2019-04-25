package cs591e1_sp19.eatogether;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.yelp.fusion.client.models.Business;
import com.yelp.fusion.client.models.SearchResponse;

import com.yelp.fusion.client.connection.YelpFusionApi;
import com.yelp.fusion.client.connection.YelpFusionApiFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static cs591e1_sp19.eatogether.AppState.current_lati;
import static cs591e1_sp19.eatogether.AppState.current_longi;

public class ProfileChangeActivity extends AppCompatActivity {

    private ImageView avatar;
    private TextView userName;
    private CheckBox male;
    private CheckBox female;
    private EditText loc;
    private EditText language;
    private EditText oneFood, twoFood, threeFood;
    private Button submit;
    private CheckBox twoK, fiveK, tenK;
    private int radius = 1000;

    private String databaseURL = "https://eatogether-cs591.firebaseio.com";
    private StorageReference mStorageRef;

    String apiKey = BuildConfig.YelpApiKey;
    Map<String, String> params;

    Firebase mRef;

    private String USER_NAME; //THIS IS A GLOBAL VALUE, SINCE THE USERNAME IS FIXED WHEN PASSED TO THIS PAGE

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri; //POINT TO AN IMAGE

    //Yelp Class
    YelpFusionApiFactory apiFactory;
    YelpFusionApi yelpFusionApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilechange);
        Firebase.setAndroidContext(this);
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");

        avatar = (ImageView) findViewById(R.id.avatar);
        male = (CheckBox) findViewById(R.id.checkbox_male);
        female = (CheckBox) findViewById(R.id.checkBox_female);
        userName = (TextView) findViewById(R.id.user_name);
        loc = (EditText) findViewById(R.id.userLocation);
        language = (EditText) findViewById(R.id.userLanguage);
        oneFood = (EditText) findViewById(R.id.food_one);
        twoFood = (EditText) findViewById(R.id.food_two);
        threeFood = (EditText) findViewById(R.id.food_three);

        twoK = findViewById(R.id.checkbox_2km);
        fiveK = findViewById(R.id.checkBox_5km);
        tenK = findViewById(R.id.checkBox_10km);

        submit = (Button) findViewById(R.id.applyButton);

        userName.setText(AppState.userName);
        USER_NAME = userName.getText().toString();

        mRef = new Firebase("https://eatogether-cs591.firebaseio.com/Users");
        //Yelp set up
        try {
            apiFactory  = new YelpFusionApiFactory();
            yelpFusionApi = apiFactory.createAPI(apiKey);
        } catch (Exception e) {
            e.printStackTrace();
        }

        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                female.setChecked(false);
                male.setChecked(true);
            }
        });

        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                male.setChecked(false);
                female.setChecked(true);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFirebase();
            }
        });

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        twoK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                twoK.setChecked(true);
                fiveK.setChecked(false);
                tenK.setChecked(false);
                radius = 1000;
                AppState.radius = 1000;
            }
        });

        fiveK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                twoK.setChecked(false);
                fiveK.setChecked(true);
                tenK.setChecked(false);
                radius = 3000;
                AppState.radius = 3000;
            }
        });

        tenK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                twoK.setChecked(false);
                fiveK.setChecked(false);
                tenK.setChecked(true);
                radius = 5000;
                AppState.radius = 5000;
            }
        });

        //set avatar as user setting
        setProfile();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData()!= null){
            mImageUri = data.getData();
            Log.v("URI string:", mImageUri.toString());
            Picasso.get().load(mImageUri).into(avatar);                 //LOAD IMAGE URL TO IMAGE VIEW
        }
    }

    private void uploadFirebase() {
        String name = AppState.userID;

        String gender = male.isChecked() ? "male" : "female";

        String location = edit2String(loc);
        String language_user = edit2String(language);

        String favOne = edit2String(oneFood);
        String favTwo = edit2String(twoFood);
        String favThree = edit2String(threeFood);

//        Firebase nameRegi = new Firebase(databaseURL + "/Users/" + name + "/name");
//        nameRegi.setValue(name);

        Firebase genderRegi = new Firebase(databaseURL + "/Users/" + name + "/gender");
        genderRegi.setValue(gender);

        Firebase locRegi = new Firebase(databaseURL + "/Users/" + name + "/location");
        locRegi.setValue(location);

        Firebase languageRegi = new Firebase(databaseURL + "/Users/" + name + "/language");
        languageRegi.setValue(language_user);

        Firebase favoritesRegi = new Firebase(databaseURL + "/Users/" + name + "/fav");
        favoritesRegi.child("one").setValue(favOne);
        favoritesRegi.child("two").setValue(favTwo);
        favoritesRegi.child("three").setValue(favThree);

        uploadAvatar();

        updateNearby();

        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
        startActivity(intent);

    }

    private String edit2String(EditText editText) {
        Editable temp = editText.getText();
        if(temp == null) {
            return "";
        } else {
            return temp.toString();
        }
    }

    private void openFileChooser(){  //CREATE AN INTENT
        Intent intent= new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);             //GET DATA BACK
    }

    private String getFileExtension(Uri uri){                            //RETURN FILE EXTENSION
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadAvatar() {
        if(mImageUri != null) {
            final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            fileReference.putFile(mImageUri).continueWithTask(
                    new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if(!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return fileReference.getDownloadUrl();
                        }
                    }
            ).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        mRef.child(AppState.userID).child("avatar").setValue(downloadUri.toString());
                        Toast.makeText(ProfileChangeActivity.this, "Changes has been applied", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(ProfileChangeActivity.this, "Faild change Profile", Toast.LENGTH_LONG).show();
                    }
                }
            });

        }
    }

    private void updateNearby() {
        params = new HashMap<>();

        params.put("latitude", current_lati);
        params.put("longitude", current_longi);
        params.put("radius", String.valueOf(radius));

        Call<SearchResponse> call = yelpFusionApi.getBusinessSearch(params);
        call.enqueue(callback);
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
                restaurant.setValue(new MapModel(bu.getName(),
                        bu.getPrice(),
                        bu.getRating(),
                        bu.getCategories(),
                        bu.getCoordinates()));
            }


        }
        @Override
        public void onFailure(Call<SearchResponse> call, Throwable t) {
            // HTTP error happened, do something to handle it.
        }
    };

    private void setProfile() {
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot userSnapShot : dataSnapshot.getChildren()) {
                    if(userSnapShot.getKey().equals(AppState.userID)) {
                        if(userSnapShot.child("avatar").getValue() != null) {
                            Picasso.get()
                                    .load(userSnapShot.child("avatar").getValue().toString())
                                    .into(avatar);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
}
