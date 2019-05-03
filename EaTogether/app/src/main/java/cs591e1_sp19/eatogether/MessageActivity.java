package cs591e1_sp19.eatogether;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MessageActivity extends AppCompatActivity {
    private android.support.v4.app.FragmentManager fragmentManager;
    private MessageFragment msgFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        String post_id = getIntent().getStringExtra("post_id");
        String rest_id = getIntent().getStringExtra("rest_id");

        Bundle bundle = new Bundle();
        bundle.putString("post_id", post_id);
        bundle.putString("res_id", rest_id);

        // Add fragment to FragmentManager and attach it to our activity
        fragmentManager = getSupportFragmentManager();
        msgFrag = new MessageFragment();
        msgFrag.setArguments(bundle);

        fragmentManager
                .beginTransaction()
                .add(R.id.recyclerViewHolder, msgFrag, "userFrag")
                .commit();

        // Get other user's ID
        Intent i = getIntent();
        String otherUserId = i.getStringExtra("otherUserId");
        this.setTitle(otherUserId);
//        String otherUserAvatar = i.getStringExtra("otherUserAvatar");

        // Show the fragment that we need
        showMsgFrag();

    }

    private void showMsgFrag() {
        fragmentManager
                .beginTransaction()
                .replace(R.id.recyclerViewHolder, msgFrag)
                .commit();
    }
}
