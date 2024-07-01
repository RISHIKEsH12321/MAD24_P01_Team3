package sg.edu.np.mad.travelhub;

public class Reminder {
    public String reminderTitle;
    public String eventID;
    public String reminderTime;
    public String reminderId;

    public Reminder(String reminderTitle, String eventID, String reminderTime){
        this.reminderTitle = reminderTitle;
        this.eventID = eventID;
        this.reminderTime = reminderTime;
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
