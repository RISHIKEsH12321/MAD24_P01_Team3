package sg.edu.np.mad.travelhub;

import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;

public class ToBringItem implements Serializable {
    String eventID;
    String itemID;
    String itemName;
    Boolean ticked;

    public ToBringItem(String e, String i, Boolean t, String n){
        this.eventID = e;
        this.itemID = i;
        this.ticked = t;
        this.itemName = n;
    }
    public ToBringItem(){}


    @Override
    public String toString() {
        return "ToBringItem{" +
                "eventID='" + eventID + '\'' +
                ", itemID='" + itemID + '\'' +
                ", itemName='" + itemName + '\'' +
                ", ticked=" + ticked +
                '}';
    }



}
