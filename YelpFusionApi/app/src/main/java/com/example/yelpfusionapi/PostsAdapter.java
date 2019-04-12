package com.example.yelpfusionapi;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.MyViewHolder> {

    Context context;
    ArrayList<PostModel> posts;

    public PostsAdapter(Context c, ArrayList<PostModel> p){
        context = c;
        posts = p;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView user_name;
        TextView user_locale;
        TextView time_period;
        TextView user_note;
        ImageView user_avatar;

        public MyViewHolder(@NonNull View itemView) {
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
    public PostsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.listview_review_row,viewGroup, false);
        return  new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PostsAdapter.MyViewHolder myViewHolder, int i) {
        myViewHolder.user_name.setText(posts.get(i).user);
        myViewHolder.user_locale.setText(posts.get(i).country + ", " + posts.get(i).language);
        myViewHolder.time_period.setText(posts.get(i).time1 + " - " + posts.get(i).time2);
        myViewHolder.user_note.setText(posts.get(i).note);
        Picasso.get().load(posts.get(i).avatar).into(myViewHolder.user_avatar);

    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}

