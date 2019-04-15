package cs591e1_sp19.eatogether;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class WishAdapter extends RecyclerView.Adapter<WishAdapter.MyViewHolder> {

    Context context;
    ArrayList<Restaurant> restaurants;

    public WishAdapter(Context c, ArrayList<Restaurant> r){
        context = c;
        restaurants = r;
    }

    @NonNull
    @Override
    public WishAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.restaurant_item,viewGroup, false);
        return  new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) { //retrieve data and show it in the view
        holder.tvName.setText(restaurants.get(position).getName());
        Picasso.get().load(restaurants.get(position).getImageUrl()).into(holder.imgRestaurant);

    }

    @Override
    public int getItemCount() {   //get the number of restaurants in wishlist
        return restaurants.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView tvName;
        public ImageView imgRestaurant;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.RestaurantName);
            imgRestaurant = itemView.findViewById(R.id.imgRestaurant);
        }
    }

}
