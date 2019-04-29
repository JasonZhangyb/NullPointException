package cs591e1_sp19.eatogether;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class infoAdapter extends RecyclerView.Adapter<infoAdapter.MyViewHolder> {

    Context context;
    ArrayList<RatingModel> reviews;

    public infoAdapter(Context c, ArrayList<RatingModel> r){
        context = c;
        reviews = r;
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView user_name, time_pref, user_note;
        ImageView user_avatar;
        Button msg_button;
        RatingBar user_rating;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            user_name = itemView.findViewById(R.id.user_name);
            user_rating = itemView.findViewById(R.id.user_rating);
//            time_pref = itemView.findViewById(R.id.time_created);
            user_note = itemView.findViewById(R.id.user_comment);
            user_avatar = itemView.findViewById(R.id.user_avatar);
//            msg_button = itemView.findViewById(R.id.btn_message);

        }
    }

    @NonNull
    @Override
    public infoAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.user_review_row,viewGroup, false);
        return  new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final infoAdapter.MyViewHolder myViewHolder, int i) {
        final RatingModel review = reviews.get(i);

//        myViewHolder.msg_button.setVisibility(View.GONE);
        myViewHolder.user_name.setText(review.username + "\'s Review: ");
        myViewHolder.user_rating.setRating(Float.parseFloat(review.rating));
        myViewHolder.user_note.setText(review.review);
        Picasso.get().load(review.useravatar).into(myViewHolder.user_avatar);

    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

}

