package com.example.yelpfusionapi;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.yelp.fusion.client.connection.YelpFusionApi;
import com.yelp.fusion.client.connection.YelpFusionApiFactory;
import com.yelp.fusion.client.models.Business;
import com.yelp.fusion.client.models.Review;
import com.yelp.fusion.client.models.Reviews;

import org.w3c.dom.Text;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantReview extends AppCompatActivity {

    ListView lstView_review;
    ListAdapter lstAdapter_review;
    YelpFusionApiFactory apiFactory;
    YelpFusionApi yelpFusionApi;
    String apiKey = BuildConfig.YelpApiKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_review);

        try {
            apiFactory  = new YelpFusionApiFactory();
            yelpFusionApi = apiFactory.createAPI(apiKey);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String res_id = getIntent().getStringExtra("resID");
        //using the getBusinessReviews function query the Review API.
        Call<Reviews> call = yelpFusionApi.getBusinessReviews(res_id, "en_US");
        call.enqueue(callback);

    }

    Callback<Reviews> callback = new Callback<Reviews>() {
        @Override
        public void onResponse(Call<Reviews> call, Response<Reviews> response) {
            final ArrayList<Review> reviews = response.body().getReviews();
            lstView_review = findViewById(R.id.lstView_review);
            lstAdapter_review = new CustomAdapter_review(getApplicationContext(), reviews);
            lstView_review.setAdapter(lstAdapter_review);

        }

        @Override
        public void onFailure(Call<Reviews> call, Throwable t) {

        }
    };

}

class CustomAdapter_review extends BaseAdapter {

    ArrayList<Review> reviews;
    ArrayList<String> names;
    ArrayList<String> date;
    ArrayList<String> comments;
    ArrayList<String> avatars;
    ArrayList<String> ratings;
    Context context;

    public CustomAdapter_review(Context aContext, ArrayList<Review> aReviews){

        names = new ArrayList<>();
        date = new ArrayList<>();
        comments = new ArrayList<>();
        avatars = new ArrayList<>();
        ratings = new ArrayList<>();
        context = aContext;
        reviews = aReviews;

        for (int i = 0, j = reviews.size(); i < j; i++ ) {
            names.add(reviews.get(i).getUser().getName());
            date.add(reviews.get(i).getTimeCreated());
            comments.add(reviews.get(i).getText());
            avatars.add(reviews.get(i).getUser().getImageUrl());
            ratings.add("Rating: " + Integer.toString(reviews.get(i).getRating()));
        }

    }

    @Override
    public int getCount() {
        return reviews.size();
    }

    @Override
    public Object getItem(int position) {
        return reviews.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row;

        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.listview_review_row, parent, false);
        }
        else{
            row = convertView;
        }

        ImageView user_avatar = row.findViewById(R.id.user_avatar);
        TextView user_name = row.findViewById(R.id.user_name);
        TextView user_rating = row.findViewById(R.id.user_rating);
        TextView time_created = row.findViewById(R.id.time_created);
        TextView user_comment = row.findViewById(R.id.user_comment);

        Picasso.get().load(avatars.get(position)).into(user_avatar);
        user_name.setText(names.get(position));
        user_rating.setText(ratings.get(position));
        time_created.setText(date.get(position));
        user_comment.setText(comments.get(position));

        return row;
    }
}