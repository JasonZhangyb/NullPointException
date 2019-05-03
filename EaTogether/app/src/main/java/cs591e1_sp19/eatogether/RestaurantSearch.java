package cs591e1_sp19.eatogether;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.yelp.fusion.client.connection.YelpFusionApi;
import com.yelp.fusion.client.connection.YelpFusionApiFactory;
import com.yelp.fusion.client.models.Business;
import com.yelp.fusion.client.models.Category;
import com.yelp.fusion.client.models.SearchResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static cs591e1_sp19.eatogether.AppState.current_lati;
import static cs591e1_sp19.eatogether.AppState.current_longi;

public class RestaurantSearch extends AppCompatActivity {

    //using a third party library to call and retrieve data from yelp fusion api
    //github link: https://github.com/ranga543/yelp-fusion-android

    ListView lstView;
    ListAdapter lstAdapter;
    YelpFusionApiFactory apiFactory;
    YelpFusionApi yelpFusionApi;
    String apiKey = BuildConfig.YelpApiKey;
    Map<String, String> params;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_search);
        thread.start();
    }

    //using a YelpFusionApi object to query against the API.
    //Instantiate a YelpFusionApi object by using YelpFusionApiFactory with the API key.

    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                apiFactory  = new YelpFusionApiFactory();
                yelpFusionApi = apiFactory.createAPI(apiKey);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    });

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);   //get rid of default behavior.

        // Inflate the menu; this adds items to the action bar
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //open map activity

        int id = item.getItemId();

        if (id == R.id.map) {
            Intent i = new Intent(this, MapsActivity.class);
            startActivity(i);
            return true;
        }

        if (id == R.id.lst) {
            Intent i = new Intent(this, PostsList.class);
            startActivity(i);
            return true;
        }

        if (id == R.id.wish) {
            Intent i = new Intent(this, WishList.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);  //if none of the above are true, do the default and return a boolean.
    }


    public void sendMessage(View view) {
        EditText txt1 = findViewById(R.id.editText);
        EditText txt2 = findViewById(R.id.editText2);
        String message = txt1.getText().toString();
        String location = txt2.getText().toString();
        //specifying parameters for the business search.
        params = new HashMap<>();
        params.put("term", message);
        params.put("location", location);
        //params.put("radius", String.valueOf(AppState.radius));
        //using the getBusinessSearch function to generate a Call object which makes a request to the Search API.
        Call<SearchResponse> call = yelpFusionApi.getBusinessSearch(params);
        call.enqueue(callback);

    }

    //passing in a Callback object to send the request asynchronously
    Callback<SearchResponse> callback = new Callback<SearchResponse>() {
        @Override
        public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
            SearchResponse searchResponse = response.body();
            final ArrayList<Business> businesses = searchResponse.getBusinesses();

            FirebaseDatabase ref = FirebaseDatabase.getInstance();

            DatabaseReference db = ref
                    .getReference()
                    .child("Users")
                    .child(AppState.userID)
                    .child("Nearby");

            db.removeValue();

            current_lati = Double.toString(businesses.get(0).getCoordinates().getLatitude());
            current_longi = Double.toString(businesses.get(0).getCoordinates().getLongitude());

            for(Business bu : businesses) {
                DatabaseReference restaurant = db.child(bu.getId());
                restaurant.setValue(new MapModel(bu.getName(),
                        bu.getPrice(),
                        bu.getRating(),
                        bu.getCategories(),
                        bu.getCoordinates()));

            }

            //custom listview
            lstView = findViewById(R.id.lstView);
            lstAdapter = new CustomAdapter(getApplicationContext(), businesses);
            lstView.setAdapter(lstAdapter);
            lstView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent i = new Intent(getApplicationContext(), RestaurantPost.class);
                    //passing intent to RestaurantPost activity for the Business API.
                    i.putExtra("rest_id", businesses.get(position).getId());
                    Log.v("ID",businesses.get(position).getId());
                    startActivity(i);
                }
            });

        }

        @Override
        public void onFailure(Call<SearchResponse> call, Throwable t) {
            Toast.makeText(RestaurantSearch.this, "failed api call", Toast.LENGTH_SHORT).show();
        }
    };
}



//custom adapter for the custom listview
class CustomAdapter extends BaseAdapter {

    ArrayList<Business> businesses;
    ArrayList<String> name;
    ArrayList<String> img;
    ArrayList<Double> rating;
    ArrayList<ArrayList<Category>> type;
    Context context;


    public CustomAdapter(Context aContext, ArrayList<Business> aBusinesses) {
        name = new ArrayList<>();
        img = new ArrayList<>();
        rating = new ArrayList<>();
        type = new ArrayList<>();
        context = aContext;
        businesses = aBusinesses;

        //retrieving data from yelp api as jason object and storing attributes.
        for (int i = 0, j = businesses.size(); i < j; i++ ) {
            Log.v("test", businesses.get(1).getName());
            name.add(businesses.get(i).getName());
            img.add(businesses.get(i).getImageUrl());
            rating.add(businesses.get(i).getRating());
            type.add(businesses.get(i).getCategories());
        }

    }

    @Override
    public int getCount() {
        return businesses.size();
    }

    @Override
    public Object getItem(int position) {
        return businesses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row;
        String types = "";

        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.listview_row, parent, false);
        }
        else{
            row = convertView;
        }

        TextView business_name = row.findViewById(R.id.business_name);
        TextView business_type = row.findViewById(R.id.business_type);
        ImageView business_img = row.findViewById(R.id.business_img);
        RatingBar business_rating = row.findViewById(R.id.business_rating);


        for (int i = 0; i < type.get(position).size(); i++){
            types += type.get(position).get(i).getTitle();
            if (i != type.get(position).size() - 1) types += ", ";
        }

        business_name.setText(name.get(position));
        business_type.setText(types);
        business_rating.setRating(rating.get(position).floatValue());
        //Picasso is a third party library for showing images from url.
        //https://square.github.io/picasso/
        Picasso.get().load(img.get(position)).resize(500,500).into(business_img);

        return row;
    }
}