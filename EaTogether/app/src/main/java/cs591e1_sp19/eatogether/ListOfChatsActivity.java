package cs591e1_sp19.eatogether;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ListOfChatsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ListOfChatAdapter mAdapter;

    private DatabaseReference chatDatabase;
    private DatabaseReference userDatabase;
    private ChildEventListener childEventListener;

    private MenuFragment  menu = new MenuFragment();;
    private FragmentManager menu_manager;
    private FragmentTransaction menu_trans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_chats);

        // Bind the RecyclerView to the fragment
        recyclerView = findViewById(R.id.recyclerView);

        // Use a linear layout manager.
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Bind our custom adapter to the RecyclerView.
        mAdapter = new ListOfChatAdapter(this);
        recyclerView.setAdapter(mAdapter);

        // Get a reference to the Firebase DB.
        userDatabase = AppState.getDatabaseReference(AppState.USER_DATABASE);
        chatDatabase = AppState.getDatabaseReference(AppState.CHAT_DATABASE);

        setChildEventListener();

        // Add menu dynamically
        menu_manager = getSupportFragmentManager();
        menu_trans = menu_manager.beginTransaction();
        menu_trans.add(R.id.menu, menu);

        menu_trans.addToBackStack(null);
        menu_trans.commit();

    }

    @Override
    protected void onStart() {
        super.onStart();
        chatDatabase.addChildEventListener(childEventListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        chatDatabase.removeEventListener(childEventListener);
    }

    private void setChildEventListener() {
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User newUser = dataSnapshot.child("user").getValue(User.class);

//                Toast.makeText(getApplicationContext(), "Message ID: " + newMsg.getId() + " | " + newMsg.getText(), Toast.LENGTH_SHORT).show();

                mAdapter.addItemToDataset(dataSnapshot.getKey());
                mAdapter.notifyItemInserted(mAdapter.getItemCount() - 1);
                recyclerView.smoothScrollToPosition(mAdapter.getItemCount() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }
}
