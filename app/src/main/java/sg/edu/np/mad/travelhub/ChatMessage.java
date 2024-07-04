package sg.edu.np.mad.travelhub;

public class ChatMessage {
    private String text;
    private boolean isUser;

    public ChatMessage(String text, boolean isUser) {
        this.text = text;
        this.isUser = isUser;
    }

    public String getText() {
        return text;
    }

    public boolean isUser() {
        return isUser;
    }

    public String getMessage() {
        return text;
    }

    public boolean isUserMessage() {
        return isUser;
    }
}
