package sg.edu.np.mad.travelhub;

import com.google.gson.Gson;
import java.io.Serializable;
import java.util.ArrayList;

public class CompleteEventUser implements Serializable {
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

    // Constructor
    public CompleteEventUser(ArrayList<ImageAttachment> attachmentImageList,
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

    // Default constructor
    public CompleteEventUser(){ this.isFirebaseEvents = false; }

    // Getters and setters
    public ArrayList<ImageAttachment> getAttachmentImageList() { return attachmentImageList; }
    public void setAttachmentImageList(ArrayList<ImageAttachment> attachmentImageList) { this.attachmentImageList = attachmentImageList; }

    public ArrayList<ItineraryEvent> getItineraryEventList() { return itineraryEventList; }
    public void setItineraryEventList(ArrayList<ItineraryEvent> itineraryEventList) { this.itineraryEventList = itineraryEventList; }

    public ArrayList<ToBringItem> getToBringItems() { return toBringItems; }
    public void setToBringItems(ArrayList<ToBringItem> toBringItems) { this.toBringItems = toBringItems; }

    public ArrayList<String> getNotesList() { return notesList; }
    public void setNotesList(ArrayList<String> notesList) { this.notesList = notesList; }

    public ArrayList<Reminder> getReminderList() { return reminderList; }
    public void setReminderList(ArrayList<Reminder> reminderList) { this.reminderList = reminderList; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }

    public String getEventID() { return eventID; }
    public void setEventID(String eventID) { this.eventID = eventID; }

    public Boolean getIsFirebaseEvents() { return isFirebaseEvents; }
    public void setIsFirebaseEvents(Boolean isFirebaseEvents) { this.isFirebaseEvents = isFirebaseEvents; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CompleteEventUser{");
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

    public String completeEventToJsonConverter (CompleteEventUser completeEvent) {
        Gson gson = new Gson();
        completeEvent.attachmentImageList.clear();
        return gson.toJson(completeEvent);
    }
}
