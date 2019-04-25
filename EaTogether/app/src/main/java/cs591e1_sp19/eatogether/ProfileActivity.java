package cs591e1_sp19.eatogether;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jakewharton.processphoenix.ProcessPhoenix;

import static android.content.Intent.EXTRA_TEXT;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class ProfileActivity extends AppCompatActivity {

    private Button goWish, goProf, logout, myPosts;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        goWish = findViewById(R.id.wish_list);
        goProf = findViewById(R.id.profile_change);
        logout = findViewById(R.id.logout);
        myPosts = findViewById(R.id.my_posts);

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

                /*AppState.isLoggedIn = false;
                AppState.userID = null;
                AppState.userName = null;
                AppState.current_lati = "";
                AppState.current_longi = "";
                AppState.radius = 1000;*/

                Intent mStartActivity = new Intent(getApplicationContext(), LoginActivity.class);
                int mPendingIntentId = 123456;
                PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager mgr = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                System.exit(0);

            }
        });

        myPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MyPosts.class);
                startActivity(intent);
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
            Toast.makeText(getBaseContext(), "wishlist", Toast.LENGTH_LONG).show();
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

}
