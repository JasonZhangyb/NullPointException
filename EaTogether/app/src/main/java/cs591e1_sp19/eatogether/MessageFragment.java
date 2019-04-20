package cs591e1_sp19.eatogether;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A simple {@link Fragment} subclass.
 */
public class MessageFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ChatAdapter mAdapter;

    private DatabaseReference msgDatabase;
    private DatabaseReference userDatabase;
    private ChildEventListener childEventListener;
    private DatabaseReference invDatabase;

    private Button sendButton;
    private EditText inputEditText;
    private Button inviteButton;

    private String currentUid;
    private String otherUid;
    private String otherUserAvatar;


    public MessageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Create a view
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        // Bind the RecyclerView to the fragment
        recyclerView = view.findViewById(R.id.recyclerView);

        // Use a linear layout manager.
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        // Binds the sendButton and inputEditText.
        sendButton = view.findViewById(R.id.sendButton);
        inputEditText = view.findViewById(R.id.inputEditText);

        //Binds the inviteButton
        inviteButton = view.findViewById(R.id.inviteButton);

        // Bind our custom adapter to the RecyclerView.
        mAdapter = new ChatAdapter(FirebaseDatabase.getInstance().getReference().child("users"));
        recyclerView.setAdapter(mAdapter);

        // Determine which chat to retrieve
        currentUid = AppState.userID;
        otherUid = AppState.otherChatUserId;
        otherUserAvatar = AppState.otherChatUserAvatar;

        String chatKey = generateChatKey(currentUid, otherUid);

        // Get a reference to the Firebase DBs.
        msgDatabase = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("Chats")
                .child(chatKey)
                .child("Chat")
                .child("Msgs");

        userDatabase = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("Users");

        invDatabase = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("Chats")
                .child(chatKey)
                .child("Chat")
                .child("invite");

        invDatabase.setValue(false);

        // Initialize and attach onClickListeners and onChildEventListeners
        // for the sendButton and msgDatabase, respectively.
        setSendButtonListener();
        setChildEventListener();
        setInviteButtonListener();

        return view;
    }

    @Override
    public void onStart() {
        // TODO: Start listening for changes in Firebase DB
        super.onStart();
        msgDatabase.addChildEventListener(childEventListener);
    }

    @Override
    public void onStop() {
        // TODO: Stop Listening for changes in Firebase DB
        super.onStop();
        msgDatabase.removeEventListener(childEventListener);
    }


    private void setChildEventListener() {
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Message newMsg = dataSnapshot.child("msg").getValue(Message.class);

//                Toast.makeText(getApplicationContext(), "Message ID: " + newMsg.getId() + " | " + newMsg.getText(), Toast.LENGTH_SHORT).show();

                mAdapter.addItemToDataset(newMsg);
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

    private String generateChatKey(String currentUid, String otherUid) {
        StringBuilder chatKeyBuilder = new StringBuilder();

        if (currentUid.compareTo(otherUid) < 0) {
            chatKeyBuilder.append(currentUid);
            chatKeyBuilder.append(otherUid);
        } else {
            chatKeyBuilder.append(otherUid);
            chatKeyBuilder.append(currentUid);
        }

        return chatKeyBuilder.toString();
    }

    private void setSendButtonListener() {
        // Set onClickListener for the send button.
        View.OnClickListener sendButtonListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = inputEditText.getText().toString();

                if (input == null || input.isEmpty()) {
                    Toast.makeText(getActivity(), "Please enter a message to send!", Toast.LENGTH_SHORT).show();
                } else {
                    String newId = msgDatabase.push().getKey();

                    Message msg = new Message(newId, input, currentUid);

                    msgDatabase.child(newId).child("msg").setValue(msg);
                }

                inputEditText.setText("");
            }
        };

        sendButton.setOnClickListener(sendButtonListener);
    }


    private void setInviteButtonListener() {
        // Set onClickListener for the send button.
        View.OnClickListener inviteButtonListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invDatabase.setValue(true);
            }
        };

        inviteButton.setOnClickListener(inviteButtonListener);
    }
}
