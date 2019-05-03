package cs591e1_sp19.eatogether;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatItemViewHolder> {
    private List<Message> mDataset;
    private DatabaseReference userDatabase;

    // Implements a new ViewHolder for our row items,
    // since ViewHolder itself is abstract.
    public static class ChatItemViewHolder extends RecyclerView.ViewHolder {
        private TextView header;
        private TextView body;
        private CircleImageView imageView;

        public ChatItemViewHolder(View rowView) {
            super(rowView);
            header = rowView.findViewById(R.id.rowTextHeader);
            body = rowView.findViewById(R.id.rowTextBody);
            imageView = rowView.findViewById(R.id.rowImageView);
        }

        public TextView getBody() {
            return body;
        }

        public TextView getHeader() {
            return header;
        }

        public CircleImageView getImageView() {
            return imageView;
        }
    }

    public ChatAdapter(DatabaseReference userDatabase) {
        this.mDataset = new ArrayList<>();
        this.userDatabase = AppState.getDatabaseReference(AppState.USER_DATABASE);
    }

    // Create new views
    @Override
    public ChatItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_recycler_row, parent, false);
        return new ChatItemViewHolder(rowView);
    }

    // Replaces the contents of a view
    @Override
    public void onBindViewHolder(final ChatItemViewHolder holder, int position) {
        // Step 1: get the element from the dataset at the given position
        // Step 2: replace the contents of the view with that element
        final Message item = mDataset.get(position);
//        String header =  item.getId();
        holder.getBody().setText(item.getText());

        userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Log.i("FirebaseChatAdapter", "onDataChange entered! Sender ID: " + item.getSenderUid());
                User user = dataSnapshot.child(item.getSenderUid()).getValue(User.class);
                holder.getHeader().setText(user.getEmail());
                Log.i("FirebaseChatAdapter", "Avatar URL: " + user.getAvatar());
                Picasso.get().load(user.getAvatar()).into(holder.getImageView());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    // Add a new item to the data set.
    public void addItemToDataset(Message item) {
        mDataset.add(item);
    }

    // Updates the entire data set.
    public void removeAll() {
        final int numItems = mDataset.size();
        mDataset.clear();
        notifyItemRangeRemoved(0, numItems);
    }
}
