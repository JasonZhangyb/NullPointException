package cs591e1_sp19.eatogether;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.TimeZone;

public class NewPost extends AppCompatActivity {

    DatabaseReference ref;
    TextView res_name;
    EditText note;
    Button next;
    public int year;
    public int month;
    public int day;

    String latitude, longitude;

    String res_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        res_name = findViewById(R.id.res_name);
        note = findViewById(R.id.note);
        next = findViewById(R.id.next_page);

        res_name.setText(getIntent().getStringExtra("resName"));
        res_img = getIntent().getStringExtra("resImg");
        latitude = getIntent().getStringExtra("latitude");
        longitude = getIntent().getStringExtra("longitude");


        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        ref = database.getReference("Posts");


        final String res_id = getIntent().getStringExtra("resID");

        // Get date picker object.
        DatePicker datePicker = (DatePicker)findViewById(R.id.datePicker);
        datePicker.init(2019, 04, 25, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int day) {
                NewPost.this.year = year;
                NewPost.this.month = month;
                NewPost.this.day = day;
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), PostingActivity.class);
                i.putExtra("resID", res_id);
                i.putExtra("resName", res_name.getText().toString());
                i.putExtra("resImg", res_img);
                i.putExtra("latitude", latitude);
                i.putExtra("longitude", longitude);
                i.putExtra("year", year);
                i.putExtra("day", day);
                i.putExtra("month", month);

                startActivity(i);
            }
        });


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

}
