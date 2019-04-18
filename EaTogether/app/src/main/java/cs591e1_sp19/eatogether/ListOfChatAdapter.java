package cs591e1_sp19.eatogether;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListOfChatAdapter extends RecyclerView.Adapter<ListOfChatAdapter.UserItemViewHolder> {
    private List<String> mDataset;
    private Context mContext;

    // Implements a new ViewHolder for our row items,
    // since ViewHolder itself is abstract.
    public static class UserItemViewHolder extends RecyclerView.ViewHolder {
        private TextView nameLabel;
        private TextView name;
        private CircleImageView avatarImageView;
        private LinearLayout itemLinearLayout;

        public UserItemViewHolder(View rowView) {
            super(rowView);
            nameLabel = rowView.findViewById(R.id.rowTextHeader);
            name = rowView.findViewById(R.id.rowTextBody);
            avatarImageView = rowView.findViewById(R.id.rowImageView);
            itemLinearLayout = rowView.findViewById(R.id.itemLinearLayout);
        }

        public LinearLayout getItemLinearLayout() {
            return itemLinearLayout;
        }

        public TextView getNameLabel() {
            return nameLabel;
        }

        public TextView getName() {
            return name;
        }

        public CircleImageView getAvatarImageView() {
            return avatarImageView;
        }
    }

    public ListOfChatAdapter(Context mContext) {
        this.mDataset = new ArrayList<>();
        this.mContext = mContext;
    }

    // Create new views
    @Override
    public UserItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_recycler_row, parent, false);
        return new UserItemViewHolder(rowView);
    }

    // Replaces the contents of a view
    @Override
    public void onBindViewHolder(UserItemViewHolder holder, int position) {
        // Step 1: get the element from the dataset at the given position
        // Step 2: replace the contents of the view with that element
        String chatId = mDataset.get(position);
//        final User item = mDataset.get(position);
//        Picasso.get().load(item.getAvatar()).into(holder.getAvatarImageView());
        holder.nameLabel.setText("Chat ID: ");
        holder.name.setText(chatId);

        // Step 3: set onClickListener for our item so that
        // we'll be able to go into the actual chat.
//        View.OnClickListener onClickListener = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                loadChatFragment(item);
//            }
//        };

        // Make the entire row listen for click events.
//        holder.getItemLinearLayout().setOnClickListener(onClickListener);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    // Add a new item to the data set.
    public void addItemToDataset(String item) {
        mDataset.add(item);
    }

    // Updates the entire data set.
    public void removeAll() {
        final int numItems = mDataset.size();
        mDataset.clear();
        notifyItemRangeRemoved(0, numItems);
    }

    // A helper method that loads the corresponding chat fragment.
//    private void loadChatFragment(User item) {
//        RecyclerViewFragment chatFrag = new RecyclerViewFragment();
//        Bundle chatFragArgs = new Bundle();
//        chatFragArgs.putString("currentUid", "u000001");
//        chatFragArgs.putString("otherUid", item.getUid());
//        chatFrag.setArguments(chatFragArgs);
//
//        // Have the activity load the new fragment.
//        if (mContext == null) {
//            return;
//        }
//
//        if (mContext instanceof MainActivity) {
//            MainActivity mainActivity = (MainActivity) mContext;
//            mainActivity.loadFragment(R.id.recyclerViewHolder, chatFrag);
//        }
//    }
}
