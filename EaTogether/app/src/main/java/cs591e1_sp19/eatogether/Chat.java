package cs591e1_sp19.eatogether;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class Chat extends AppCompatActivity {

    EditText txt_input;
    Button btn_send;
    ChatModel chats;
    MsgModel msg;
    ArrayList<MsgModel> msgs;
    HashMap<String, String> guests;

    ListView lstView_chat;
    ChatsAdapter chat_adapter;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref_chats = database.getReference("ChatsAlt");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        txt_input = findViewById(R.id.txt_input);
        btn_send = findViewById(R.id.btn_send);
        lstView_chat = findViewById(R.id.lstView_chat);

        final String post_id = getIntent().getStringExtra("post_id");
        final String rest_id = getIntent().getStringExtra("rest_id");
        final String creator_id = getIntent().getStringExtra("creator_id");
        final String creator_name = getIntent().getStringExtra("creator_name");
        final String creator_avatar = getIntent().getStringExtra("creator_avatar");
        final String restaurant_name = getIntent().getStringExtra("restaurant_name");
        final String time1 = getIntent().getStringExtra("time1");
        final String time2 = getIntent().getStringExtra("time2");
        final String latitude = getIntent().getStringExtra("latitude");
        final String longitude = getIntent().getStringExtra("longitude");

        final DatabaseReference ref_msg = ref_chats.child(post_id);

        guests = new HashMap<>();

        ref_chats.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(post_id)){

                    guests.put(AppState.userID ,AppState.userID);

                    chats = new ChatModel(
                            creator_id,
                            creator_name,
                            creator_avatar,
                            guests,
                            post_id,
                            rest_id,
                            restaurant_name,
                            latitude,
                            longitude,
                            time1,
                            time2,
                            "inUse");

                    ref_msg.setValue(chats);
                    //ref_msg.child("guests").setValue(guests);

                } else {

                    ChatModel data = dataSnapshot.child(post_id).getValue(ChatModel.class);

                    if (data.status.equals("abandoned")){
                        guests.put(AppState.userID ,AppState.userID);

                        chats = new ChatModel(
                                creator_id,
                                creator_name,
                                creator_avatar,
                                guests,
                                post_id,
                                rest_id,
                                restaurant_name,
                                latitude,
                                longitude,
                                time1,
                                time2,
                                "inUse");

                        ref_msg.setValue(chats);
                    } else {

                        ArrayList<MsgModel> old_msgs = new ArrayList<>();
                        AppState.creatorID = data.creator_id;
                        for (DataSnapshot snapshot : dataSnapshot.child(post_id).child("msg").getChildren()) {
                            old_msgs.add(snapshot.getValue(MsgModel.class));
                        }

                        if (!data.guests.containsKey(AppState.userID) && !AppState.userID.equals(data.creator_id)) {

                            data.guests.put(AppState.userID, AppState.userID);

                            ref_msg.setValue(data);
                            for (MsgModel i : old_msgs) {
                                ref_msg.child("msg").push().setValue(i);
                            }

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //setting up database for chat

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = txt_input.getText().toString();

                txt_input.getText().clear();

                DatabaseReference ref = ref_msg.child("msg").push();

                msg = new MsgModel(AppState.userID, AppState.userName,input, AppState.userAvatar);

                ref.setValue(msg);

                lstView_chat.setAdapter(null);

                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

            }
        });


        ref_msg.child("msg").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                msgs = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    MsgModel msg = snapshot.getValue(MsgModel.class);

                    msgs.add(msg);

                    lstView_chat = findViewById(R.id.lstView_chat);
                    chat_adapter = new ChatsAdapter(getApplicationContext(), msgs);
                    lstView_chat.setAdapter(chat_adapter);
                    lstView_chat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Intent intent = new Intent(getApplicationContext(), UserInfo.class);

                            intent.putExtra("creator_id", AppState.creatorID);
                            intent.putExtra("creator_avatar", creator_avatar);
                            intent.putExtra("creator_name", creator_name);
                            intent.putExtra("resID", rest_id);
                            intent.putExtra("resName", restaurant_name);
                            intent.putExtra("postID", post_id);
                            intent.putExtra("guestID", msgs.get(i).sender_id);
                            intent.putExtra("time1", time1);
                            intent.putExtra("time2", time2);
                            intent.putExtra("latitude", latitude);
                            intent.putExtra("longitude", longitude);

                            startActivity(intent);
                        }
                    });


                }

                //lstView_chat.setSelection(chat_adapter.getCount() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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

class ChatsAdapter extends BaseAdapter {

    Context context;
    ArrayList<MsgModel> msgs;

    public ChatsAdapter(Context c, ArrayList<MsgModel> p){
        context = c;
        msgs = p;
    }

    @Override
    public int getCount() {
        return msgs.size();
    }

    @Override
    public Object getItem(int position) {
        return msgs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;

        final MsgModel msg = msgs.get(position);

        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.message_recycler_row, parent, false);
        }
        else{
            row = convertView;
        }

        TextView header = row.findViewById(R.id.rowTextHeader);
        TextView body = row.findViewById(R.id.rowTextBody);
        final ImageView user_avatar = row.findViewById(R.id.rowImageView);

        // Retrieving sender's user avatar.
        AppState.getDatabaseReference(AppState.USER_DATABASE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Picasso.get().load(dataSnapshot.child(msg.sender_id).child("avatar").getValue().toString()).into(user_avatar);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        header.setText(msg.sender_name);
        body.setText(msg.txt);

        return row;
    }

}
