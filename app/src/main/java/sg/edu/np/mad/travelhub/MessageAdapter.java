package sg.edu.np.mad.travelhub;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;
    private Context context;
    private List<UserMessage> messageList;

    public MessageAdapter(Context context) {
        this.context = context;
        this.messageList = new ArrayList<>();
    }

    public void add(UserMessage message) {
        // Add new messages to the end of the list, so it displays the correct order
        messageList.add(message);
        notifyItemInserted(messageList.size() - 1);
    }

    public void clear() {
        messageList.clear();
        notifyDataSetChanged();
    }

    public List<UserMessage> getMessageList() {
        return messageList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case VIEW_TYPE_SENT:
                View view = inflater.inflate(R.layout.chat_msg_item, parent, false);
                return new UserMessageViewHolder(view);
            case VIEW_TYPE_RECEIVED:
                View receivedview = inflater.inflate(R.layout.chat_msg_item_gemini, parent, false);
                return new ReceivedMessageViewHolder(receivedview);
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        UserMessage message = messageList.get(position);
        if (message.getSenderUid().equals(FirebaseAuth.getInstance().getUid())) {
            ((MessageAdapter.UserMessageViewHolder) holder).bind(message);
        }
        else {
            ((MessageAdapter.ReceivedMessageViewHolder) holder).bind(message);
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (messageList.get(position).getSenderUid().equals(FirebaseAuth.getInstance().getUid())) {
            return VIEW_TYPE_SENT;
        }
        else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    // ViewHolder for user messages
    public static class UserMessageViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUserMessage;

        public UserMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUserMessage = itemView.findViewById(R.id.message_text);
        }

        public void bind(UserMessage message) {
            textViewUserMessage.setText(message.getMessage());
        }
    }

    // ViewHolder for Gemini messages
    public static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView textViewReceivedMessage;

        public ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewReceivedMessage = itemView.findViewById(R.id.textViewGeminiMessage);
        }

        public void bind(UserMessage message) {
            textViewReceivedMessage.setText(message.getMessage());
        }
    }

}
