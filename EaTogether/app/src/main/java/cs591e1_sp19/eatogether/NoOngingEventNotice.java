package cs591e1_sp19.eatogether;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class NoOngingEventNotice extends AppCompatActivity {

    private MenuFragment  menu = new MenuFragment();;
    private FragmentManager menu_manager;
    private FragmentTransaction menu_trans;
    private Button btnSearch;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.no_ongoing_notice);

        btnSearch = (Button) findViewById(R.id.btnSearch);

        // Add menu dynamically
        menu_manager = getSupportFragmentManager();
        menu_trans = menu_manager.beginTransaction();
        menu_trans.add(R.id.menu, menu);

        menu_trans.addToBackStack(null);
        menu_trans.commit();

        //Send value to menu fragment to indicate which icon is pressed
        Bundle args = new Bundle();
        args.putString("value","EVENT");
        menu.putArguments(args);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NoOngingEventNotice.this, RestaurantSearch.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
// super.onBackPressed();
// Not calling **super**, disables back button in current screen.
    }
}
