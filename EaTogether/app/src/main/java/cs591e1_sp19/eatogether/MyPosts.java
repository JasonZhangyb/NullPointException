package cs591e1_sp19.eatogether;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.view.View.GONE;

public class MyPosts extends AppCompatActivity {

    RecyclerView my_posts_view;
    MyPostsAdapter my_posts_adapter;

    ArrayList<PostModel> posts_lst;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref_posts = database.getReference("Posts");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts);



        posts_lst = new ArrayList<>();

        ref_posts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    for (DataSnapshot dataSnapshot2: dataSnapshot1.getChildren()){
                        PostModel r = dataSnapshot2.getValue(PostModel.class);
                        if (AppState.userID.equals(r.user_id)){
                            posts_lst.add(r);
                        }
                    }
                }

                my_posts_view = findViewById(R.id.recView_my_post);
                my_posts_view.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                my_posts_adapter = new MyPostsAdapter(getApplicationContext(), posts_lst);
                my_posts_view.setAdapter(my_posts_adapter);
                Log.v("test_my_post", "setting up adapter");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

class MyPostsAdapter extends RecyclerView.Adapter<MyPostsAdapter.MyViewHolder> {

    Context context;
    ArrayList<PostModel> posts;

    MyPostsAdapter(Context c, ArrayList<PostModel> p){
        context = c;
        posts = p;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView res_name, user_locale, time_period, user_note;
        ImageView res_img;
        Button msg_btn;
        RatingBar user_rating;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            res_name = itemView.findViewById(R.id.user_name);
            user_locale = itemView.findViewById(R.id.user_locale);
            time_period = itemView.findViewById(R.id.time_created);
            user_note = itemView.findViewById(R.id.user_comment);
            res_img = itemView.findViewById(R.id.user_avatar);
            msg_btn = itemView.findViewById(R.id.btn_message);
            user_rating = itemView.findViewById(R.id.user_rating);

        }
    }

    @NonNull
    @Override
    public MyPostsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.recyclerview_posts_row,viewGroup, false);
        return  new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyPostsAdapter.MyViewHolder myViewHolder, int i) {
        final PostModel post = posts.get(i);

        myViewHolder.user_rating.setVisibility(GONE);
        myViewHolder.res_name.setText(post.restaurant_name);
        //myViewHolder.user_locale.setText(post.country + ", " + post.language);
        myViewHolder.user_locale.setText(post.month + "/" + post.date + "/" + post.year);
        myViewHolder.time_period.setText(post.time1 + " - " + post.time2);
        myViewHolder.user_note.setText(post.note);
        myViewHolder.msg_btn.setText("DELETE");
        Picasso.get().load(post.restaurant_img).into(myViewHolder.res_img);

        myViewHolder.msg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference ref_posts = database.getReference("Posts");
                DatabaseReference ref_users = database.getReference("Users");
                ref_posts.child(post.restaurant_id).child(post.post_id).removeValue();
                ref_users.child(AppState.userID).child("Posts").child(post.post_id).removeValue();
                Intent i = new Intent(view.getContext(), MapsActivity.class);
                view.getContext().startActivity(i);
            }
        });

        myViewHolder.res_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, RestaurantPost.class);
                i.putExtra("rest_id", post.restaurant_id);
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}
