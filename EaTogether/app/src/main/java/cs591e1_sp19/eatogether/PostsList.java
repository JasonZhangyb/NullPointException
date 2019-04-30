package cs591e1_sp19.eatogether;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
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
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.yelp.fusion.client.models.Business;
import com.yelp.fusion.client.models.Category;

import java.util.ArrayList;
import java.util.Map;

public class PostsList extends AppCompatActivity {

    ListView lstView_list;
    ListAdapter lstAdapter_list;

    ArrayList<MapModel> lst_res;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref_posts = database.getReference("Posts");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts_list);

        ref_posts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lst_res = new ArrayList<>();
                for (DataSnapshot restaurants: dataSnapshot.getChildren()){
                    for (DataSnapshot posts: restaurants.getChildren()){
                        if (posts.child("user_id").getValue().toString().equals(AppState.userID)){
                            break;
                        } else {
                            MapModel res = new MapModel(
                                    posts.child("restaurant_name").getValue().toString(),
                                    posts.child("restaurant_img").getValue().toString(),
                                    posts.child("restaurant_id").getValue().toString(),
                                    posts.child("res_rating").getValue().toString(),
                                    posts.child("res_type").getValue().toString()
                            );

                            lst_res.add(res);
                            break;
                        }
                    }
                }

                lstView_list = findViewById(R.id.lstView_list);
                lstAdapter_list = new PostLstAdapter(getApplicationContext(), lst_res);
                lstView_list.setAdapter(lstAdapter_list);
                lstView_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        Intent i = new Intent(getApplicationContext(), RestaurantPost.class);
                        i.putExtra("rest_id", lst_res.get(position).res_id);
                        startActivity(i);
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

        if (id == R.id.lst) {
            Intent i = new Intent(this, PostsList.class);
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

}

class PostLstAdapter extends BaseAdapter {

    ArrayList<MapModel> restaurants;
    Context context;


    public PostLstAdapter(Context aContext, ArrayList<MapModel> aRestaurant) {
        context = aContext;
        restaurants = aRestaurant;
    }

    @Override
    public int getCount() {
        return restaurants.size();
    }

    @Override
    public Object getItem(int position) {
        return restaurants.get(position);
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
            row = inflater.inflate(R.layout.listview_row, parent, false);
        }
        else{
            row = convertView;
        }

        TextView business_name = row.findViewById(R.id.business_name);
        TextView business_type = row.findViewById(R.id.business_type);
        ImageView business_img = row.findViewById(R.id.business_img);
        RatingBar business_rating = row.findViewById(R.id.business_rating);


        business_name.setText(restaurants.get(position).res_name);
        business_type.setText(restaurants.get(position).type_str);
        business_rating.setRating(Float.parseFloat(restaurants.get(position).res_rating_str));
        //Picasso is a third party library for showing images from url.
        //https://square.github.io/picasso/
        Picasso.get().load(restaurants.get(position).res_img).resize(500,500)
                .into(business_img);

        return row;
    }
}