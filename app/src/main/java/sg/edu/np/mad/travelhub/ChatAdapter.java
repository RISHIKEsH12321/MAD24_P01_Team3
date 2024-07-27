package sg.edu.np.mad.travelhub;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ChatMessage> messageList;

    // Define constants for message types
    private static final int USER_MESSAGE = 1;
    private static final int GEMINI_MESSAGE = 2;

    public ChatAdapter(List<ChatMessage> messageList) {
        this.messageList = messageList;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = messageList.get(position);
        return message.isUserMessage() ? USER_MESSAGE : GEMINI_MESSAGE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case USER_MESSAGE:
                View userView = inflater.inflate(R.layout.chat_msg_item, parent, false);
                return new UserMessageViewHolder(userView);
            case GEMINI_MESSAGE:
                View geminiView = inflater.inflate(R.layout.chat_msg_item_gemini, parent, false);
                return new GeminiMessageViewHolder(geminiView);
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messageList.get(position);

        switch (holder.getItemViewType()) {
            case USER_MESSAGE:
                ((UserMessageViewHolder) holder).bind(message);
                break;
            case GEMINI_MESSAGE:
                ((GeminiMessageViewHolder) holder).bind(message);
                break;
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

        public void bind(ChatMessage message) {
            textViewUserMessage.setText(message.getMessage());
        }
    }

    // ViewHolder for Gemini messages
    public static class GeminiMessageViewHolder extends RecyclerView.ViewHolder {
        TextView textViewGeminiMessage;

        public GeminiMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewGeminiMessage = itemView.findViewById(R.id.textViewGeminiMessage);
        }

        public void bind(ChatMessage message) {
            textViewGeminiMessage.setText(message.getMessage());
        }
    }
}