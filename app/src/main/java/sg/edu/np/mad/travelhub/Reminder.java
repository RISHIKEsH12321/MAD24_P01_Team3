package sg.edu.np.mad.travelhub;

import java.io.Serializable;

public class Reminder implements Serializable {
    public String reminderTitle;
    public String eventID;
    public String reminderTime;
    public String reminderId;

    public Reminder(String reminderTitle, String eventID, String reminderTime, String reminderId){
        this.reminderTitle = reminderTitle;
        this.eventID = eventID;
        this.reminderTime = reminderTime;
        this.reminderId = reminderId;

    }
    public Reminder(){}

    @Override
    public String toString() {
        return "Reminder{" +
                "reminderTitle='" + reminderTitle + '\'' +
                ", eventID='" + eventID + '\'' +
                ", reminderTime='" + reminderTime + '\'' +
                '}';
    }

}
