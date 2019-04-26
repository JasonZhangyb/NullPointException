package cs591e1_sp19.eatogether;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class UserInfo extends AppCompatActivity {

    TextView info_name, info_loc, info_pref;
    ImageView info_avatar;
    Button btn_info;
    RecyclerView recView_info;

    EventModel event, event_guest;

    HashMap<String, String> guests1, guests2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        info_name = findViewById(R.id.info_name);
        info_loc = findViewById(R.id.info_loc);
        info_pref = findViewById(R.id.info_pref);
        info_avatar = findViewById(R.id.info_avatar);
        btn_info = findViewById(R.id.btn_info);
        recView_info = findViewById(R.id.recView_info);

        Boolean isCreator = getIntent().getStringExtra("isCreator").equals(AppState.userID);
        final String creator_name = getIntent().getStringExtra("creator_name");
        final String creator_avatar = getIntent().getStringExtra("creator_avatar");
        final String post_id = getIntent().getStringExtra("postID");
        final String res_id = getIntent().getStringExtra("resID");
        final String res_name = getIntent().getStringExtra("resName");
        final String guest_id = getIntent().getStringExtra("guestID");
        final String time1 = getIntent().getStringExtra("time1");
        final String time2 = getIntent().getStringExtra("time2");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference ref_posts = database.getReference("Posts");
        final DatabaseReference ref_users = database.getReference("Users");

        guests1 = new HashMap<>();
        guests2 = new HashMap<>();

        if (isCreator){
            btn_info.setText("INVITE");
            btn_info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AppState.onGoingPost = post_id;
                    AppState.onGoingRes = res_id;

                    guests1.put("guest", guest_id);
                    guests2.put("guest", AppState.userID);
                    event = new EventModel(
                            AppState.userID,
                            res_id,
                            res_name,
                            post_id,
                            AppState.current_lati,
                            AppState.current_longi,
                            guests1,
                            time1,
                            time2);
                    event_guest = new EventModel(
                            guest_id,
                            res_id,
                            res_name,
                            post_id,
                            AppState.current_lati,
                            AppState.current_longi,
                            guests2,
                            time1,
                            time2
                    );
                    ref_users.child(AppState.userID).child("Ongoing").setValue(event);
                    ref_users.child(guest_id).child("Ongoing").setValue(event_guest);
                    //ref_users.child(AppState.userID).child("Posts").child(post_id).removeValue();
                    //ref_posts.child(res_id).child(post_id).removeValue();

                }
            });
        } else {
            AppState.onGoingPost = post_id;
            AppState.onGoingRes = res_id;

        }



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
