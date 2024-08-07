package sg.edu.np.mad.travelhub;

import android.util.Log;

import com.google.firebase.database.ServerValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParentItem {
    //private String postId;
    private String parentName, parentDescription, parentImage, parentUser;
    //private List<ChildItem> childItemList;
    private Map<String, ChildMain> childData;
    private String parentKey;
    private Long timeStamp;
    //prob init parentuser here
    public ParentItem(){
        this.childData = new HashMap<>();
    }

    public ParentItem(String parentName, String parentDescription, String parentImage, String parentUser, Map<String, ChildMain> childData
    , String parentKey) {
        this.parentName = parentName;
        this.parentDescription = parentDescription;
        this.parentImage = parentImage;
        this.parentUser = parentUser;
        this.childData = childData;
        this.parentKey = parentKey;
    }

    public Map<String, ChildMain> getChildData() {
        return childData;
    }

    public void setChildData(Map<String, ChildMain> childData) {

        this.childData = childData;
    }

    public String getParentName() {
        return parentName;
    }

    public String getParentDescription() {
        return parentDescription;
    }

    public void setParentDescription(String parentDescription) {
        this.parentDescription = parentDescription;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getParentImage() {
        return parentImage;
    }

    public void setParentImage(String parentImage) {
        this.parentImage = parentImage;
    }

    public String getParentUser() {
        return parentUser;
    }

    public void setParentUser(String parentUser) {
        this.parentUser = parentUser;
    }

    public String getParentKey() {
        return parentKey;
    }

    public void setParentKey(String parentKey) {
        this.parentKey = parentKey;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }
}