package sg.edu.np.mad.travelhub;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    public Boolean isFirebaseEvents;

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
        this.isFirebaseEvents = false;
    }

    public CompleteEvent(){ this.isFirebaseEvents = false; }

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

    // Nested EventDetails class
    public static class EventDetails {
        public String Category;
        public String Date;
        public String EventName;
        public ArrayList<String> Notes;
        public ArrayList<ItineraryEvent> itineraryEventList;
        public ArrayList<Reminder> reminders;
        public ArrayList<ToBringItem> toBringItems;
        public ArrayList<Map<String, Object>> attachmentImageList;

        public EventDetails(String category, String date, String eventName, ArrayList<String> notes, ArrayList<ItineraryEvent> itineraryEventList, ArrayList<Reminder> reminders, ArrayList<ToBringItem> toBringItems, ArrayList<Map<String, Object>> attachmentImageList) {
            this.Category = category;
            this.Date = date;
            this.EventName = eventName;
            this.Notes = notes;
            this.itineraryEventList = itineraryEventList;
            this.reminders = reminders;
            this.toBringItems = toBringItems;
            this.attachmentImageList = attachmentImageList;
        }
    }

    public EventDetails toEventDetails() {
        // Convert attachmentImageList to a list of maps
        ArrayList<Map<String, Object>> attachmentImageListMap = new ArrayList<>();
        if (attachmentImageList != null) {
            for (ImageAttachment attachment : attachmentImageList) {
                Map<String, Object> map = new HashMap<>();
                map.put("uri", attachment.getURI());
                attachmentImageListMap.add(map);
            }
        }

        return new EventDetails(category, date, eventName, notesList, itineraryEventList, reminderList, toBringItems, attachmentImageListMap);
    }
}
