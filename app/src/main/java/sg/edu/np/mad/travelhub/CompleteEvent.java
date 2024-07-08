package sg.edu.np.mad.travelhub;

import android.net.Uri;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;

public class CompleteEvent implements Serializable {
    public ArrayList<ImageAttachment> attachmentImageList;
    public ArrayList<ItineraryEvent> itineraryEventList;
    public ArrayList<ToBringItem> toBringItems;
    public ArrayList<String> notesList;
    public ArrayList<Reminder> reminderList;
    public String date;
    public String category;
    public String eventName;
    public String eventID;

    public CompleteEvent(ArrayList<ImageAttachment> attachmentImageList,
                         ArrayList<ItineraryEvent> itineraryEventList,
                         ArrayList<ToBringItem> toBringItems,
                         ArrayList<String> notesList,
                         ArrayList<Reminder> reminderList,
                         String date,
                         String category,
                         String eventName) {
        this.attachmentImageList = attachmentImageList;
        this.itineraryEventList = itineraryEventList;
        this.toBringItems = toBringItems;
        this.notesList = notesList;
        this.reminderList = reminderList;
        this.date = date;
        this.category = category;
        this.eventName = eventName;
    }

    public CompleteEvent(){}

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CompleteEvent{");
        sb.append("eventID='").append(eventID).append('\'');
        sb.append(", eventName='").append(eventName).append('\'');
        sb.append(", date='").append(date).append('\'');
        sb.append(", category='").append(category).append('\'');
        sb.append(", notesList=").append(notesList);
        sb.append(", reminderList=").append(reminderList);
        sb.append(", toBringItems=").append(toBringItems);
        sb.append(", itineraryEventList=").append(itineraryEventList);
        sb.append(", attachmentImageList=").append(attachmentImageList);
        sb.append('}');
        return sb.toString();
    }

    public String CompleteEventToJsonConverter (CompleteEvent completeEvent) {
        // Convert CompleteEvent object to JSON string
        Gson gson = new Gson();
        CompleteEvent qrEvent = completeEvent;
        qrEvent.attachmentImageList.clear();

        String json = gson.toJson(qrEvent);

        return json;
    }
}
