package sg.edu.np.mad.travelhub;

public class UserMessage {
    private String messageId;
    private String message;
    private String senderUid;

    public UserMessage() {}

    public UserMessage(String messageId, String message, String senderUid) {
        this.messageId = messageId;
        this.message = message;
        this.senderUid = senderUid;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }
}
