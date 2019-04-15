package cs591e1_sp19.eatogether;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
        TextView user_name;
        TextView user_locale;
        TextView time_period;
        TextView user_note;
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

        myViewHolder.user_name.setText(posts.get(i).user);
        myViewHolder.user_locale.setText(posts.get(i).country + ", " + posts.get(i).language);
        myViewHolder.time_period.setText(posts.get(i).time1 + " - " + posts.get(i).time2);
        myViewHolder.user_note.setText(posts.get(i).note);
        Picasso.get().load(posts.get(i).avatar).into(myViewHolder.user_avatar);

        myViewHolder.msg_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), MessageActivity.class);
//                i.putExtra("otherUserId", post.user);
//                i.putExtra("otherUserAvatar", post.avatar);
                AppState.otherChatUserAvatar = post.avatar;
                AppState.otherChatUserId = post.user;

                v.getContext().startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}

