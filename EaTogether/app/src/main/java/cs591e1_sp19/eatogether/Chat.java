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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
    Button btn_send, btn_inv;
    ChatModel chats;
    MsgModel msg;
    ArrayList<MsgModel> msgs;
    HashMap<String, String> guests;

    RecyclerView recView_chat;
    ChatsAdapter chat_adapter;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref_chats = database.getReference("ChatsAlt");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        txt_input = findViewById(R.id.txt_input);
        btn_send = findViewById(R.id.btn_send);
        btn_inv = findViewById(R.id.btn_inv);
        recView_chat = findViewById(R.id.recView_chat);

        final String post_id = getIntent().getStringExtra("post_id");
        final String rest_id = getIntent().getStringExtra("rest_id");
        final String creator_id = getIntent().getStringExtra("creator_id");
        final String creator_name = getIntent().getStringExtra("creator_name");
        final String creator_avatar = getIntent().getStringExtra("creator_avatar");
        final String restaurant_name = getIntent().getStringExtra("restaurant_name");

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
                            restaurant_name);

                    ref_msg.setValue(chats);
                    //ref_msg.child("guests").setValue(guests);

                } else {
                    ChatModel data = dataSnapshot.child(post_id).getValue(ChatModel.class);
                    ArrayList<MsgModel> old_msgs = new ArrayList<>();
                    for(DataSnapshot snapshot : dataSnapshot.child(post_id).child("msg").getChildren()){
                        old_msgs.add(snapshot.getValue(MsgModel.class));
                    }

                    if (!data.guests.containsKey(AppState.userID) && !AppState.userID.equals(data.creator_id)){

                        data.guests.put(AppState.userID ,AppState.userID);

                        ref_msg.setValue(data);
                        for (MsgModel i: old_msgs){
                            ref_msg.child("msg").push().setValue(i);
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

                DatabaseReference ref = ref_msg.child("msg").push();

                msg = new MsgModel(AppState.userID, AppState.userName,input);

                ref.setValue(msg);

                recView_chat.setAdapter(null);

            }
        });


        ref_msg.child("msg").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                msgs = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    MsgModel msg = snapshot.getValue(MsgModel.class);

                    msgs.add(msg);

                    recView_chat.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    chat_adapter = new ChatsAdapter(getApplicationContext(), msgs);
                    recView_chat.setAdapter(chat_adapter);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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

class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.MyViewHolder> {

    Context context;
    ArrayList<MsgModel> msgs;

    public ChatsAdapter(Context c, ArrayList<MsgModel> p){
        context = c;
        msgs = p;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView header, body;
        ImageView user_avatar;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            header = itemView.findViewById(R.id.rowTextHeader);
            body = itemView.findViewById(R.id.rowTextBody);
            user_avatar = itemView.findViewById(R.id.rowImageView);

        }
    }

    @NonNull
    @Override
    public ChatsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.message_recycler_row,viewGroup, false);
        return  new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatsAdapter.MyViewHolder myViewHolder, int i) {
        final MsgModel msg = msgs.get(i);

        myViewHolder.header.setText(msg.sender_name);
        myViewHolder.body.setText(msg.txt);
        //Picasso.get().load().into(myViewHolder.user_avatar);


    }

    @Override
    public int getItemCount() {
        return msgs.size();
    }

}
