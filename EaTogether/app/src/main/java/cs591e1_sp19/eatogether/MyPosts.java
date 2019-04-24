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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MyPosts extends AppCompatActivity {

    RecyclerView my_posts_view;
    MyPostsAdapter my_posts_adapter;

    ArrayList<PostModel> posts_lst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref_posts = database.getReference("Posts");

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
}

class MyPostsAdapter extends RecyclerView.Adapter<MyPostsAdapter.MyViewHolder> {

    Context context;
    ArrayList<PostModel> posts;

    MyPostsAdapter(Context c, ArrayList<PostModel> p){
        context = c;
        posts = p;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView user_name, user_locale, time_period, user_note;
        ImageView user_avatar;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            user_name = itemView.findViewById(R.id.user_name);
            user_locale = itemView.findViewById(R.id.user_rating);
            time_period = itemView.findViewById(R.id.time_created);
            user_note = itemView.findViewById(R.id.user_comment);
            user_avatar = itemView.findViewById(R.id.user_avatar);

        }
    }

    @NonNull
    @Override
    public MyPostsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.listview_review_row,viewGroup, false);
        return  new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyPostsAdapter.MyViewHolder myViewHolder, int i) {
        final PostModel post = posts.get(i);

        myViewHolder.user_name.setText(post.user_name);
        myViewHolder.user_locale.setText(post.country + ", " + post.language);
        myViewHolder.time_period.setText(post.time1 + " - " + post.time2);
        myViewHolder.user_note.setText(post.note);
        Picasso.get().load(post.avatar).into(myViewHolder.user_avatar);

    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}
