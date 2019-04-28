package cs591e1_sp19.eatogether;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jakewharton.processphoenix.ProcessPhoenix;
import com.squareup.picasso.Picasso;

import static android.content.Intent.EXTRA_TEXT;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class ProfileActivity extends AppCompatActivity {

    private Button goWish, goProf, logout, myPosts, refresh;

    private ImageView avatar;
    private TextView userName, location, tagOne, tagTwo, tagThree, ratingValue;

    private MenuFragment  menu = new MenuFragment();;
    private FragmentManager menu_manager;
    private FragmentTransaction menu_trans;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        goWish = findViewById(R.id.wish_list);
        goProf = findViewById(R.id.profile_change);
        logout = findViewById(R.id.logout);
        myPosts = findViewById(R.id.my_posts);
        //refresh = findViewById(R.id.refresh);

        avatar = findViewById(R.id.profile_avatar);
        userName = findViewById(R.id.p_User);
        location = findViewById(R.id.p_location);
        tagOne = findViewById(R.id.tag_one);
        tagTwo = findViewById(R.id.tag_two);
        tagThree = findViewById(R.id.tag_three);
        ratingValue = findViewById(R.id.rating_value);

        menu_manager = getSupportFragmentManager();
        menu_trans = menu_manager.beginTransaction();
        menu_trans.add(R.id.menu, menu);

        menu_trans.addToBackStack(null);
        menu_trans.commit();

        Bundle args = new Bundle();
        args.putString("value","ME");
        menu.putArguments(args);

        goWish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), WishList.class);
                startActivity(intent);
            }
        });

        goProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ProfileChangeActivity.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);

                AppState.isLoggedIn = false;
                AppState.userID = null;
                AppState.userName = null;
                AppState.current_lati = "";
                AppState.current_longi = "";
                AppState.radius = 1000;

                startActivity(intent);

                /*Intent mStartActivity = new Intent(getApplicationContext(), LoginActivity.class);
                int mPendingIntentId = 123456;
                PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager mgr = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                System.exit(0);*/

            }
        });

        myPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MyPosts.class);
                startActivity(intent);
            }
        });

        /*refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
            }
        });*/

        Picasso.get()
                .load(AppState.userAvatar)
                //.resize(300, 300)
                .transform(new CircleTransform())
                .into(avatar);

        setProfile();

    }


    @Override
    public void onBackPressed() {
// super.onBackPressed();
// Not calling **super**, disables back button in current screen.
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

        if (id == R.id.map) {
            Intent i = new Intent(this, MapsActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);  //if none of the above are true, do the default and return a boolean.
    }

    private void setProfile(){
        FirebaseDatabase mref = FirebaseDatabase.getInstance();

        final DatabaseReference db = mref
                .getReference()
                .child("Users");

        db.child(AppState.userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if(dataSnapshot.child("name").getValue() != null) {
                    userName.setText(dataSnapshot.child("name").getValue().toString());
                }

                if (dataSnapshot.child("setting").child("avatar").getValue() != null) {

                    Picasso.get()
                            .load(dataSnapshot.child("avatar").getValue().toString())
                            //.resize(300, 300)
                            .transform(new CircleTransform())
                            .into(avatar);
                }

                if (dataSnapshot.hasChild("setting")) {

                    if (dataSnapshot.child("setting").child("location").getValue() != null) {
                        location.setText("Location: " + dataSnapshot.child("setting").child("location").getValue().toString());
                    }
                    if (dataSnapshot.child("setting").child("fav").getValue() != null) {
                        tagOne.setText(dataSnapshot.child("setting").child("fav").child("one").getValue().toString());
                        tagTwo.setText(dataSnapshot.child("setting").child("fav").child("two").getValue().toString());
                        tagThree.setText(dataSnapshot.child("setting").child("fav").child("three").getValue().toString());
                    }
                }

                if (dataSnapshot.hasChild("Rating")) {
                    if (dataSnapshot.child("Rating").child("rating").getValue() != null) {
                        String rating = dataSnapshot.child("Rating").child("rating").getValue().toString().substring(0, 3);
                        ratingValue.setText("★ Rating: " + rating);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
