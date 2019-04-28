package cs591e1_sp19.eatogether;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {

    Context context;
    ArrayList<PostModel> posts;

    public PostAdapter(Context c, ArrayList<PostModel> p){
        context = c;
        posts = p;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView user_name, user_locale, time_period, user_note;
        ImageView user_avatar;
        Button msg_button;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            user_name = itemView.findViewById(R.id.user_name);
            user_locale = itemView.findViewById(R.id.user_rating);
            time_period = itemView.findViewById(R.id.time_created);
            user_note = itemView.findViewById(R.id.user_comment);
            user_avatar = itemView.findViewById(R.id.user_avatar);
            msg_button = itemView.findViewById(R.id.btn_message);

        }
    }

    @NonNull
    @Override
    public PostAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.recyclerview_posts_row,viewGroup, false);
        return  new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final PostAdapter.MyViewHolder myViewHolder, int i) {
        final PostModel post = posts.get(i);

        myViewHolder.user_name.setText(post.user_name);
        //myViewHolder.user_locale.setText(post.country + ", " + post.language);
        myViewHolder.user_locale.setText("Boston" + ", " + "English");
        myViewHolder.time_period.setText(post.time1 + " - " + post.time2);
        myViewHolder.user_note.setText(post.note);
        myViewHolder.msg_button.setText("MESSAGE");
        Picasso.get().load(post.avatar).into(myViewHolder.user_avatar);

        myViewHolder.msg_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(v.getContext(), Chat.class);
                //i.putExtra("otherUserId", post.user);
                //i.putExtra("otherUserAvatar", post.avatar);
                i.putExtra("rest_id", post.restaurant_id);
                i.putExtra("post_id", post.post_id);
                i.putExtra("creator_avatar", post.avatar);
                i.putExtra("creator_id", post.user_id);
                i.putExtra("creator_name", post.user_name);
                i.putExtra("restaurant_name", post.restaurant_name);
                i.putExtra("time1", post.time1);
                i.putExtra("time2", post.time2);
                i.putExtra("date", post.date);
                i.putExtra("month", post.month);
                i.putExtra("year", post.year);
                i.putExtra("latitude", post.latitude);
                i.putExtra("longitude", post.longitude);
                AppState.otherChatUserAvatar = post.avatar;
                AppState.otherChatUserId = post.user_id;

                v.getContext().startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}

