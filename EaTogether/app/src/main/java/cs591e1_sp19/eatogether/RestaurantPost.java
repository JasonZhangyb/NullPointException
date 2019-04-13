package cs591e1_sp19.eatogether;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.yelp.fusion.client.connection.YelpFusionApi;
import com.yelp.fusion.client.connection.YelpFusionApiFactory;
import com.yelp.fusion.client.models.Business;
import com.yelp.fusion.client.models.Review;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantPost extends AppCompatActivity {

    Firebase mRef;
    YelpFusionApiFactory apiFactory;
    YelpFusionApi yelpFusionApi;
    String apiKey = BuildConfig.YelpApiKey;

    RecyclerView posts_view;
    PostAdapter posts_adapter;

    ArrayList<PostModel> posts_lst;
    TextView res_name;
    TextView res_rating;
    TextView res_review;
    ImageView res_img;
    ImageView img1;
    ImageView img2;
    ImageView img3;
    Button post;
    MaterialFavoriteButton favorite;



    //the blank space on the bottom is for showing existing posts ones the back end is finished.
    //the post button does not have any functionality for now, since we do not have a back end yet.
    //click on the TextView review to see the reviews.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_post);

        Firebase.setAndroidContext(this);

        favorite=(MaterialFavoriteButton)findViewById(R.id.fav);
        //mRef = new Firebase("https://yelpfusionapi-b5637.firebaseio.com/Restaurants");

        try {
            apiFactory  = new YelpFusionApiFactory();
            yelpFusionApi = apiFactory.createAPI(apiKey);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String res_id = getIntent().getStringExtra("rest_id");

        //use the getBusiness function to query the Business API.
        Call<Business> call = yelpFusionApi.getBusiness(res_id);
        call.enqueue(callback);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        return super.onCreateOptionsMenu(menu);   //get rid of default behavior.

        // Inflate the menu; this adds items to the action bar
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //open wishlist activity

        int id = item.getItemId();

        if (id == R.id.wish) {
            Toast.makeText(getBaseContext(), "wishlist", Toast.LENGTH_LONG).show();
            Intent i = new Intent(this, WishList.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);  //if none of the above are true, do the default and return a boolean.
    }




    Callback<Business> callback = new Callback<Business>() {
        @Override
        public void onResponse(Call<Business> call, Response<Business> response) {
            final Business business = response.body();

            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference("Posts").child(business.getId());

            res_name = findViewById(R.id.res_name);
            res_rating = findViewById(R.id.res_rating);
            res_review = findViewById(R.id.res_review);
            res_img = findViewById(R.id.res_img);
            img1 = findViewById(R.id.img1);
            img2 = findViewById(R.id.img2);
            img3 = findViewById(R.id.img3);
            post = findViewById(R.id.btn_post);
            posts_lst = new ArrayList<>();

            res_name.setText(business.getName());
            res_rating.setText("Rating: " + Double.toString(business.getRating()));
            Picasso.get().load(business.getImageUrl()).resize(150,150).into(res_img);
            Picasso.get().load(business.getPhotos().get(0)).resize(100,100).into(img1);
            Picasso.get().load(business.getPhotos().get(1)).resize(100,100).into(img2);
            Picasso.get().load(business.getPhotos().get(2)).resize(100,100).into(img3);

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                    {
                        PostModel r = dataSnapshot1.getValue(PostModel.class);
                        System.out.println(r);
                        posts_lst.add(r);
                    }
                    posts_view = findViewById(R.id.recView_post);
                    posts_view.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    posts_adapter = new PostAdapter(getApplicationContext(), posts_lst);
                    posts_view.setAdapter(posts_adapter);


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            post.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getApplicationContext(), NewPost.class);
                    i.putExtra("resID", business.getId());
                    startActivity(i);
                }
            });

            res_review.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getApplicationContext(), RestaurantReview.class);
                    i.putExtra("resID", business.getId());
                    startActivity(i);
                }
            });

            favorite.setOnFavoriteChangeListener(  // add restaurant if to database if user like it
                    new MaterialFavoriteButton.OnFavoriteChangeListener() {
                        @Override
                        public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                            DatabaseReference mRef = database.getReference("Restaurants");
                            DatabaseReference noRef = mRef.child(business.getId());
                            DatabaseReference nameRef = noRef.child("Name");
                            DatabaseReference imageRef = noRef.child("imageUrl");
                            if (favorite == true){

                                nameRef.setValue(business.getName());

                                imageRef.setValue(business.getImageUrl());

                            }
                            else {
                                nameRef.setValue(null);
                                imageRef.setValue(null);

                            }
                        }
                    });

        }

        @Override
        public void onFailure(Call<Business> call, Throwable t) {
            Log.v("error", "failed api call");
        }
    };
}


