package sg.edu.np.mad.travelhub;

import java.io.Serializable;

public class ToBringItem implements Serializable {
    public String itemName;
    public boolean ticked;
    public String eventID;
    public String itemID;

    // Default constructor required for Firebase
    public ToBringItem() {}

    // Constructor
    public ToBringItem(String itemName, boolean ticked, String eventID, String itemID) {
        this.itemName = itemName;
        this.ticked = ticked;
        this.eventID = eventID;
        this.itemID = itemID;
    }

    // Getters and setters
    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public boolean isTicked() {
        return ticked;
    }

    public void setTicked(boolean ticked) {
        this.ticked = ticked;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }
}
