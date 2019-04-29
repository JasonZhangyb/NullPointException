package cs591e1_sp19.eatogether;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatsList extends AppCompatActivity {

    ListView lstView_chats_list;
    ChatsListAdapter chats_list_adapter;

    ArrayList<ChatModel> rooms;
    private MenuFragment  menu = new MenuFragment();;
    private FragmentManager menu_manager;
    private FragmentTransaction menu_trans;
    private RelativeLayout rll_notice;
    private ImageView notice;
    private int count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats_list);

        rll_notice= (RelativeLayout) findViewById(R.id.No_Chat_Notice);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference ref_chats = database.getReference("ChatsAlt");


        //Add menu dynamically
        menu_manager = getSupportFragmentManager();
        menu_trans = menu_manager.beginTransaction();
        menu_trans.add(R.id.menu, menu);

        menu_trans.addToBackStack(null);
        menu_trans.commit();

        //send value to menu fragment to indicate which icon is pressed
        Bundle args = new Bundle();
        args.putString("value","MESSAGE");
        menu.putArguments(args);

        ref_chats.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                rooms = new ArrayList<>();
                for(final DataSnapshot snapshot : dataSnapshot.getChildren()){

                    ChatModel chat_room = snapshot.getValue(ChatModel.class);

                    if (chat_room.status.equals("inUse")) {
                        if (chat_room.guests.containsKey(AppState.userID) || chat_room.creator_id.equals(AppState.userID))
                            rooms.add(chat_room);
                    }

                    lstView_chats_list = findViewById(R.id.lstView_chats_list);
                    chats_list_adapter = new ChatsListAdapter(getApplicationContext(), rooms);
                    lstView_chats_list.setAdapter(chats_list_adapter);
                    lstView_chats_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                            Log.v("test_onClick","here");
                            Intent i = new Intent(getApplicationContext(), Chat.class);
                            i.putExtra("rest_id", rooms.get(position).res_id);
                            i.putExtra("post_id", rooms.get(position).post_id);
                            i.putExtra("creator_id", rooms.get(position).creator_id);
                            i.putExtra("creator_name", rooms.get(position).creator_name);
                            i.putExtra("creator_avatar", rooms.get(position).creator_avatar);
                            i.putExtra("restaurant_name", rooms.get(position).res_name);
                            i.putExtra("latitude", rooms.get(position).res_latitude);
                            i.putExtra("longitude", rooms.get(position).res_longitude);
                            i.putExtra("date", rooms.get(position).date);
                            i.putExtra("month", rooms.get(position).month);
                            i.putExtra("year", rooms.get(position).year);
                            i.putExtra("time1", rooms.get(position).time1);
                            i.putExtra("time2", rooms.get(position).time2);
                            startActivity(i);
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //If there is no chat in the message
        //Show a image notice dynamically
        FirebaseDatabase.getInstance().getReference().child("ChatsAlt")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String user_id = (snapshot.child("creator_id").getValue().toString());
                            if(user_id.equals(AppState.userID)){
                                count=1;
                                break;
                            }
                            System.out.println(user_id);
                            for (DataSnapshot snapshot1:snapshot.child("guests").getChildren()){
                                if(snapshot1.getKey().equals(AppState.userID)){
                                    count=1;
                                    break;
                                }
                                System.out.println(snapshot1.getKey());
                            }
                        }
                        if (count==0) {
                            System.out.println("hiiii");
                            notice = new ImageView(ChatsList.this);
                            notice.setLayoutParams(new RelativeLayout.LayoutParams(600, 600));
                            notice.setImageResource(R.drawable.no_coversation_notice);
                            rll_notice.addView(notice);

                        }

                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
    @Override
    public void onBackPressed() {
// super.onBackPressed();
// Not calling **super**, disables back button in current screen.
    }



}

class ChatsListAdapter extends BaseAdapter {

    Context context;
    ArrayList<ChatModel> rooms;

    public ChatsListAdapter(Context c, ArrayList<ChatModel> p){
        context = c;
        rooms = p;
    }

    @Override
    public int getCount() {
        return rooms.size();
    }

    @Override
    public Object getItem(int position) {
        return rooms.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row;

        final ChatModel room = rooms.get(position);

        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.message_recycler_row, parent, false);
        }
        else{
            row = convertView;
        }

        TextView user_name = row.findViewById(R.id.rowTextHeader);
        TextView res_name = row.findViewById(R.id.rowTextBody);
        TextView date = row.findViewById(R.id.date_time);
        ImageView user_avatar = row.findViewById(R.id.rowImageView);

        user_name.setText(room.creator_name);
        res_name.setText(room.res_name);
        date.setText(room.date + "/" + room.month + "/" + room.year);
        Picasso.get().load(room.creator_avatar).into(user_avatar);

        return row;

    }


}
