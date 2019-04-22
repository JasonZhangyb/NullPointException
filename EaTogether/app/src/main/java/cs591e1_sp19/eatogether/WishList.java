package cs591e1_sp19.eatogether;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class WishList extends AppCompatActivity {

    DatabaseReference reference;
    RecyclerView recyclerView;
    ArrayList<Restaurant>list;
    WishAdapter adapter;
    private RelativeLayout rll_notice;
    private ImageView notice;
    private int count=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_list);
        Firebase.setAndroidContext(this);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<Restaurant>();
        rll_notice= (RelativeLayout) findViewById(R.id.Wish_Notice);


        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(AppState.userID)
                .child("Restaurants");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    Restaurant r = dataSnapshot1.getValue(Restaurant.class);
                    list.add(r);
                }
                adapter = new WishAdapter(WishList.this,list);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(WishList.this,"Opss...Something is wrong",Toast.LENGTH_SHORT).show();
            }
        });

        FirebaseDatabase.getInstance().getReference().child("Users").child(AppState.userID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            if (snapshot.getKey().equals("Restaurants")){
                                count = 1;
                                break;

                            }

                        }
                        if (count==0){
                            notice = new ImageView(WishList.this);
                            notice.setLayoutParams(new RelativeLayout.LayoutParams(600, 600));
                            notice.setImageResource(R.drawable.wishlist_notice);
                            rll_notice.addView(notice);
                        }

                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }
}

