package sg.edu.np.mad.travelhub;

import java.util.List;
import java.util.Map;

public class Event {

    private EventDetails eventDetails;
    private String users;
    private String notes;
    private List<String> attachmentImageList;
    private List<String> itineraryEventList;
    private List<String> toBringItems;

    public Event() {}

    public Event(EventDetails eventDetails, String users, String notes,
                 List<String> attachmentImageList, List<String> itineraryEventList,
                 List<String> toBringItems) {
        this.eventDetails = eventDetails;
        this.users = users;
        this.notes = notes;
        this.attachmentImageList = attachmentImageList;
        this.itineraryEventList = itineraryEventList;
        this.toBringItems = toBringItems;
    }

    public EventDetails getEventDetails() {
        return eventDetails;
    }

    public void setEventDetails(EventDetails eventDetails) {
        this.eventDetails = eventDetails;
    }

    public String getUsers() {
        return users;
    }

    public void setUsers(String users) {
        this.users = users;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<String> getAttachmentImageList() {
        return attachmentImageList;
    }

    public void setAttachmentImageList(List<String> attachmentImageList) {
        this.attachmentImageList = attachmentImageList;
    }

    public List<String> getItineraryEventList() {
        return itineraryEventList;
    }

    public void setItineraryEventList(List<String> itineraryEventList) {
        this.itineraryEventList = itineraryEventList;
    }

    public List<String> getToBringItems() {
        return toBringItems;
    }

    public void setToBringItems(List<String> toBringItems) {
        this.toBringItems = toBringItems;
    }

    // Nested class for eventDetails
    public static class EventDetails {
        private String category;
        private String date;
        private String eventName;

        public EventDetails() {}

        public EventDetails(String category, String date, String eventName) {
            this.category = category;
            this.date = date;
            this.eventName = eventName;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getEventName() {
            return eventName;
        }

        public void setEventName(String eventName) {
            this.eventName = eventName;
        }
    }
}
